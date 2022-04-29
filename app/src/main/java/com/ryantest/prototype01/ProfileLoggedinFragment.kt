package com.ryantest.prototype01

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var openProfileButton : Button
    private lateinit var openGithubButton : Button
    private lateinit var helpButton : Button

    private lateinit var isInTripTv : TextView
    private lateinit var welcomeTv : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        logoutButton = requireView().findViewById(R.id.logoutButton)
        tripDetailsButton = requireView().findViewById(R.id.tripDetailsButton)
        openProfileButton = requireView().findViewById(R.id.openProfileInfoButton)
        openGithubButton = requireView().findViewById(R.id.githubButton)
        helpButton = requireView().findViewById(R.id.helpButton)
        isInTripTv = requireView().findViewById(R.id.isInTripTv)
        welcomeTv = requireView().findViewById(R.id.welcomeProfileTv)

        welcomeTv.text = "Welcome to RiiDE, ${(activity as MainActivity).username} !"

        // Display the trip user state from the cached variable
        if ((activity as MainActivity).isInTrip) {
            isInTripTv.text = "Current trip details:"
            tripDetailsButton.visibility = View.VISIBLE
        } else {
            isInTripTv.text = "You currently aren't in a trip"
            tripDetailsButton.visibility = View.GONE
        }

        openGithubButton.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/50t0r25/RiiDE-prototype")
            startActivity(openURL)
        }

        openProfileButton.setOnClickListener {
            // Will open the current user's profile info
            (activity as MainActivity).replaceCurrentFragment(UserInfoFragment(auth.currentUser!!.uid))
        }

        // Check the trip user state from the database
        // then add a button listener if user is in a trip
        db.collection("users").document(auth.currentUser!!.uid).get(Source.CACHE)
            .addOnSuccessListener { user ->
                if (user.data?.get("isInTrip").toString().toBoolean()) {
                    // User is in a trip
                    (activity as MainActivity).isInTrip = true
                    isInTripTv.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    isInTripTv.text = "Current trip details:"
                    tripDetailsButton.visibility = View.VISIBLE

                    tripDetailsButton.setOnClickListener {
                        (activity as MainActivity).replaceCurrentFragment(TripDetailsFragment(user.data?.get("currentTripID").toString()))
                    }
                } else {
                    // User isn't currently in a trip
                    (activity as MainActivity).isInTrip = false
                    isInTripTv.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    isInTripTv.text = "You currently aren't in a trip,\nJoin or create one!"
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

        helpButton.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("What is RiiDE?")
                .setMessage("RiiDE is an algerian local carpooling app, it provides you the ability to create and share, or join trips.\nTo start using it, either search for a trip, join it then contact the driver via the info on their profile, or make your own trip and wait for people to join it.\n\nThis app was made as an end of cycle project for our bachelor's CS degree.\n\nGithub: @50t0r25 @windyznuts")
                .setPositiveButton("Close", null)
                .show()
        }
    }
}