package com.pamsimas.pamsimasapps.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ActivitySplashScreenBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.login.LoginActivity
import com.pamsimas.pamsimasapps.ui.onboarding.OnboardingActivity

class SplashScreenActivity : AppCompatActivity() {

    private val timeOut: Long = 2000
    private lateinit var binding: ActivitySplashScreenBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var users = Users()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseUser = firebaseAuth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (firebaseUser != null) {
                users.isAuthenticated = true
                users.userId = firebaseUser.uid
                users.name = firebaseUser.displayName
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_USER, users)
                startActivity(intent)
                finish()

            } else {
                val intent = Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, timeOut)

    }

}