package com.ryantest.prototype01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentHome = HomeFragment()
        val fragmentProfile = ProfileFragment()

        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setCurrentFragment(fragmentHome) //defaults view to home fragment

        //nav bar clicks
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

    //replaces fragment while adding to backstack
    public fun replaceCurrentFragment(fragment: Fragment) =
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

    //replaces fragment while CLEARING the backstack
    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out
            )
            replace(R.id.flFragment, fragment)

            //this somehow clears the back stack
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            commit()
        }


}