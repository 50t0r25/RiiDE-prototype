package com.ryantest.prototype01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var fragmentProfile : Fragment
    private var fragmentHome = HomeFragment()
    private lateinit var loading: AlertDialog
    private lateinit var loadingBuilder : MaterialAlertDialogBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Change the profile fragment variable to the logged in one if the user is signed in
        val currentUser = auth.currentUser
        if(currentUser != null){
            fragmentProfile = ProfileLoggedinFragment()
        } else {
            fragmentProfile = ProfileFragment()
        }

        setCurrentFragment(fragmentHome) // Defaults view to home fragment

        // Nav bar clicks
        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_home -> {
                    Log.i("NavBar","Home pressed")
                    setCurrentFragment(fragmentHome)
                    true
                }
                R.id.page_profile -> {
                    Log.i("NavBar","Profile pressed")
                    setCurrentFragment(fragmentProfile)
                    true
                }
                else -> false
            }
        }
    }

    // Replaces fragment while adding to backstack
    fun replaceCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.explode_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }

    // Replaces fragment while CLEARING the backstack
    fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out
            )
            replace(R.id.flFragment, fragment)

            // This somehow clears the back stack
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            commit()
        }

    fun createLoadingDialog() {
        loadingBuilder = MaterialAlertDialogBuilder(this)
            .setMessage("Please wait...")
            .setCancelable(false)
        loading = loadingBuilder.create()
        loading.show()
    }

    fun dismissLoadingDialog() {
        loading.dismiss()
    }
}