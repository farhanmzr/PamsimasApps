package com.pamsimas.pamsimasapps.ui.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ActivityMainBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.home.HomeFragment
import com.pamsimas.pamsimasapps.ui.dashboard.profile.ProfileFragment
import com.pamsimas.pamsimasapps.ui.dashboard.search.SearchFragment
import com.pamsimas.pamsimasapps.ui.dashboard.verification.VerificationFragment
import com.pamsimas.pamsimasapps.ui.login.LoginActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExit = false

    private lateinit var usersData: Users
    private val mainViewModel: MainViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        const val HOME_FRAGMENT_TAG = "home_fragment_tag"
        const val SEARCH_FRAGMENT_TAG = "search_fragment_tag"
        const val PROFILE_FRAGMENT_TAG = "profile_fragment_tag"
        const val VERIFICATION_FRAGMENT_TAG = "verification_fragment_tag"
        const val CHILD_FRAGMENT = "child_fragment"

        const val EXTRA_USER = "extra_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val profileFragment = ProfileFragment()
        val verificationFragment = VerificationFragment()

        usersData = intent.getParcelableExtra<Users>(EXTRA_USER) as Users
        Log.e("savedInstanceFalse", "homeFragment")
        mainViewModel.setUserProfile(usersData.userId.toString()).observe(this) { userProfile ->
            if (userProfile != null) {
                usersData = userProfile
                if (usersData.verified == true) {
                    setCurrentFragment(homeFragment, HOME_FRAGMENT_TAG)
                } else {
                    setCurrentFragment(verificationFragment, VERIFICATION_FRAGMENT_TAG)
                }
            }
        }

        setCurrentFragment(homeFragment, HOME_FRAGMENT_TAG)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    setCurrentFragment(homeFragment, HOME_FRAGMENT_TAG)
                }
                R.id.cari -> {
                    setCurrentFragment(searchFragment, SEARCH_FRAGMENT_TAG)
                }
                R.id.akun -> {
                    setCurrentFragment(profileFragment, PROFILE_FRAGMENT_TAG)
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment, fragmentTag: String) {
        supportFragmentManager.commit {
            replace(R.id.host_fragment_activity_main, fragment, fragmentTag)
        }
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed()
            return
        } else if (supportFragmentManager.findFragmentByTag(CHILD_FRAGMENT) != null) {
            super.onBackPressed()
            binding.bottomNavigation.visibility = View.VISIBLE
            return
        }

        this.doubleBackToExit = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExit = false }, 2000)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(@NonNull firebaseAuth: FirebaseAuth) {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}