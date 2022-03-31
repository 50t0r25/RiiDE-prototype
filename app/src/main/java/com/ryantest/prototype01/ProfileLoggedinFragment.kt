package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileLoggedinFragment : Fragment(R.layout.fragment_profile_loggedin) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var logoutButton : Button
    private lateinit var userEmailTv : TextView
    private lateinit var userNameTv : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        logoutButton = requireView().findViewById(R.id.logoutButton)
        userEmailTv = requireView().findViewById(R.id.userEmailTv)
        userNameTv = requireView().findViewById(R.id.userNameTv)

        userEmailTv.text = (activity as MainActivity).userEmail
        userNameTv.text = (activity as MainActivity).username

        logoutButton.setOnClickListener {
            // Logout the user then set the activity's profile fragment variable as the non-logged in one
            // then load that new fragment
            FirebaseAuth.getInstance().signOut()
            (activity as MainActivity).fragmentProfile = ProfileFragment()
            (activity as MainActivity).setCurrentFragment(ProfileFragment())
        }
    }
}