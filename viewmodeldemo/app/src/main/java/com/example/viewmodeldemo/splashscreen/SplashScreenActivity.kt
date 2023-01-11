package com.example.viewmodeldemo.splashscreen


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.databinding.ActivitySplashScreenBinding
import com.example.viewmodeldemo.preferense.Preferences
import com.example.viewmodeldemo.profile.ProfileActivity
import com.example.viewmodeldemo.signIn.SignInActivity
import com.example.viewmodeldemo.signUp.activity.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

 

    @Inject
    lateinit var prefreces: Preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        binding.signUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finishAffinity()
        }


        binding.btnStart.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({

                if (prefreces.getPrefBoolean("login")) {
                    startActivity(Intent(this, ProfileActivity::class.java))
                } else {
                    startActivity(Intent(this, SignInActivity::class.java))

                }
                finish()

            }, 500)
        }
    }
}