package id.ac.polbeng.mystock.onlineservice.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.ac.polbeng.mystock.onlineservice.databinding.ActivitySplashScreenBinding
import id.ac.polbeng.mystock.onlineservice.helpers.Config

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this@SplashScreenActivity,
                LoginActivity::class.java))
            finish()
        }, Config.SPLASH_SCREEN_DELAY)
    }
}
