package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileLoggedinFragment : Fragment(R.layout.fragment_profile_loggedin) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var logoutButton : Button
    private lateinit var tripDetailsButton : Button
    private lateinit var userEmailTv : TextView
    private lateinit var userNameTv : TextView
    private lateinit var isInTripTv : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        logoutButton = requireView().findViewById(R.id.logoutButton)
        tripDetailsButton = requireView().findViewById(R.id.tripDetailsButton)
        userEmailTv = requireView().findViewById(R.id.userEmailTv)
        userNameTv = requireView().findViewById(R.id.userNameTv)
        isInTripTv = requireView().findViewById(R.id.isInTripTv)

        userEmailTv.text = (activity as MainActivity).userEmail
        userNameTv.text = (activity as MainActivity).username

        // Display the trip user state from the cached variable
        if ((activity as MainActivity).isInTrip) {
            isInTripTv.text = "Current trip details:"
            tripDetailsButton.visibility = View.VISIBLE
        } else {
            isInTripTv.text = "You are currenly not in a trip"
            tripDetailsButton.visibility = View.GONE
        }

        // Check the trip user state from the database
        // then add a button listener if user is in a trip
        db.collection("users").document(auth.currentUser!!.uid).get(Source.CACHE)
            .addOnSuccessListener { user ->
                if (user.data?.get("isInTrip").toString().toBoolean()) {
                    // User is in a trip
                    (activity as MainActivity).isInTrip = true
                    isInTripTv.text = "Current trip details:"
                    tripDetailsButton.visibility = View.VISIBLE

                    tripDetailsButton.setOnClickListener {
                        (activity as MainActivity).replaceCurrentFragment(TripDetailsFragment(user.data?.get("currentTripID").toString()))
                    }
                } else {
                    // User isn't currently in a trip
                    (activity as MainActivity).isInTrip = false
                    isInTripTv.text = "You are currenly not in a trip"
                    tripDetailsButton.visibility = View.GONE
                }

            }
            .addOnFailureListener {
                Toast.makeText(context,
                    it.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }

        logoutButton.setOnClickListener {
            // Logout the user, refresh the main activity
            // then load the new non-loggedIn fragment
            FirebaseAuth.getInstance().signOut()
            (activity as MainActivity).refreshMainActivity()
            (activity as MainActivity).setCurrentFragment(ProfileFragment())
        }
    }
}