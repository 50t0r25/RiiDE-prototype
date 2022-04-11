package com.ryantest.prototype01

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    lateinit var username : String
    lateinit var userEmail : String
    var isInTrip = false

    lateinit var departure : String
    lateinit var destination : String

    var isLoggedIn = false

    private lateinit var fragmentProfile : Fragment
    private var fragmentHome = HomeFragment()
    private lateinit var loading: AlertDialog
    private lateinit var loadingBuilder : MaterialAlertDialogBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        db = Firebase.firestore

        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        refreshMainActivity()

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

    fun refreshMainActivity() {
        // Check if user is logged in
        val currentUser = auth.currentUser
        if(currentUser != null){
            // User logged in, set the profile fragment to be the logged in one
            fragmentProfile = ProfileLoggedinFragment()
            isLoggedIn = true

            // Set database source as cache for faster and offline access
            // Then fetch the user data from the DB
            db.collection("users").document(currentUser.uid).get(Source.CACHE)
                .addOnSuccessListener { user ->
                    username = user.data!!["username"].toString()
                    userEmail = user.data!!["email"].toString()
                    isInTrip = user.data!!["isInTrip"].toString().toBoolean()
                }
                .addOnFailureListener {
                    Toast.makeText(this,
                        it.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                }
        } else {
            // User isn't logged in, set profile fragment to the default one
            fragmentProfile = ProfileFragment()
            isLoggedIn = false
        }
    }

    // Turns an Integer into a 2 digit String (for correct time display)
    private fun intDoubleDigit(int : Int) : String {
        var newInt = int.toString()
        if (newInt.length == 1) {
            newInt = "0$newInt"
        }
        return newInt
    }

    // Displays time and date in a TextView
    fun displayTimeInTv(tv : TextView, fHour : Int, fMinute : Int, fDay : Int, fMonth : Int, fYear : Int) {
        tv.text = "Time: ${intDoubleDigit(fHour)}:${intDoubleDigit(fMinute)}\nDate: ${intDoubleDigit(fDay)}/${intDoubleDigit(fMonth)}/$fYear"
    }

    // Function checks if user has internet access
    // Not Async, do not want to waste time on it, because of bad documentation
    // Basically freezes for a few seconds on networks with no internet access
    // Tried "Solving" the above issue by only checking while on a loading screen, still freezes but not noticeable
    // Turns out the above solution doesn't actually display the loading for some reasons
    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}