package id.ac.polbeng.mystock.onlineservice.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.RequestOptions
import id.ac.polbeng.mystock.onlineservice.R
import id.ac.polbeng.mystock.onlineservice.activities.EditProfileActivity
import id.ac.polbeng.mystock.onlineservice.activities.LoginActivity
import id.ac.polbeng.mystock.onlineservice.databinding.FragmentProfileBinding
import id.ac.polbeng.mystock.onlineservice.helpers.Config
import id.ac.polbeng.mystock.onlineservice.helpers.SessionHandler
import id.ac.polbeng.mystock.onlineservice.models.DefaultResponse
import id.ac.polbeng.mystock.onlineservice.models.User
import id.ac.polbeng.mystock.onlineservice.services.ServiceBuilder
import id.ac.polbeng.mystock.onlineservice.services.UserService
import id.ac.polbeng.mystock.onlineservice.viewmodel.ProfileViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val session = SessionHandler(requireContext())
        val user: User? = session.getUser()
        val titikDua = ": "

        // Set user data if available
        if (user != null) {
            val url = Config.PROFILE_IMAGE_URL + user.gambar
            Glide.with(requireContext())
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                )
                .into(binding.imgLogo)

            binding.tvNama.text = titikDua + user.nama
            binding.tvTanggalLahir.text = titikDua + user.tanggalLahir
            binding.tvJenisKelamin.text = titikDua + user.jenisKelamin
            binding.tvNomorHP.text = titikDua + user.nomorHP
            binding.tvAlamat.text = titikDua + user.alamat
            binding.tvEmail.text = titikDua + user.email
            binding.tvWaktuSesi.text = titikDua + session.getExpiredTime()
        }

        // Edit Profile Button
        binding.btnEditProfil.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Delete User Button
        binding.btnHapusUser.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Hapus Akun")
            builder.setMessage(
                "Apakah Anda yakin ingin menghapus akun? Anda tidak akan bisa lagi login ke akun ini."
            )
            builder.setIcon(R.drawable.baseline_delete_forever_24)

            builder.setPositiveButton("Ya") { dialog, _ ->
                if (user != null) {
                    deleteUser(user.id, session)
                } else {
                    Toast.makeText(context, "Data user tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun deleteUser(userId: Int, session: SessionHandler) {
        val userService: UserService = ServiceBuilder.buildService(UserService::class.java)
        val requestCall: Call<DefaultResponse> = userService.deleteUser(userId)
        showLoading(true)

        requestCall.enqueue(object : Callback<DefaultResponse> {
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    context,
                    "Error terjadi ketika menghapus user: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.error == false) {
                    val defaultResponse = response.body()
                    defaultResponse?.let {
                        session.removeUser()
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Gagal menghapus user: ${response.body()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
