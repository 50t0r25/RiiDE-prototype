package com.ryantest.prototype01

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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TripDetailsFragment(newTripID: String) : Fragment(R.layout.fragment_trip_details) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var timeTv : TextView
    private lateinit var departureTv : TextView
    private lateinit var destinationTv : TextView
    private lateinit var driverUsernameTv : TextView
    private lateinit var seatsLeftTv : TextView
    private lateinit var priceTv : TextView

    private lateinit var backButton: Button
    private lateinit var deleteButton : Button
    private lateinit var joinButton : Button
    private lateinit var leaveButton : Button

    private val tripID = newTripID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        timeTv = requireView().findViewById(R.id.timeDetailsTv)
        departureTv = requireView().findViewById(R.id.departureDetailsTv)
        destinationTv = requireView().findViewById(R.id.destinationDetailsTv)
        driverUsernameTv = requireView().findViewById(R.id.driverUsernameDetailsTv)
        seatsLeftTv = requireView().findViewById(R.id.seatsLeftTv)
        priceTv = requireView().findViewById(R.id.priceDetailsTv)
        backButton = requireView().findViewById(R.id.backDetailsButton)
        deleteButton = requireView().findViewById(R.id.deleteDetailsButton)
        joinButton = requireView().findViewById(R.id.joinDetailsButton)
        leaveButton = requireView().findViewById(R.id.leaveDetailsButton)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        (activity as MainActivity).createLoadingDialog()

        var isDriver = false

        // Get the tripID that the user is currently in
        db.collection("trips").document(tripID).get()
            .addOnSuccessListener { trip ->

                // Save the trip data to their variables
                // TODO: save passengers data
                // Since Date and Driver are Object fields, we cast them as HashMaps
                val tripData = trip.data
                val date = tripData?.get("date") as HashMap<*, *>
                val driver = tripData["driver"] as HashMap<*, *>
                val seatsLeft = tripData["seatsLeft"].toString().toInt()

                // Will add " (Full)" next to the number of taken seats if the trip is full
                var seatsFull = ""
                if (seatsLeft == 0) seatsFull = " (Full)"

                // Check if the user is logged in
                if ((activity as MainActivity).isLoggedIn) {
                    // if user is the driver, show the delete button
                    if (driver["userID"]?.equals(auth.currentUser!!.uid)!!) {

                        isDriver = true
                        deleteButton.visibility = View.VISIBLE

                    } else {

                        // if user is in this trip, but not driver (user is passenger), show leave button
                        // Else (user is not in a trip), show Join button
                        if ((activity as MainActivity).isInTrip && ((activity as MainActivity).currentTripID == tripID)) {
                            leaveButton.visibility = View.VISIBLE
                        } else if ((!(activity as MainActivity).isInTrip) && (seatsLeft > 0)) {
                            joinButton.visibility = View.VISIBLE
                        }
                    }
                }

                (activity as MainActivity).displayTimeInTv(timeTv,
                    date["hour"].toString().toInt(),
                    date["minute"].toString().toInt(),
                    date["day"].toString().toInt(),
                    date["month"].toString().toInt(),
                    date["year"].toString().toInt())

                departureTv.text = tripData["departure"].toString()
                destinationTv.text = tripData["destination"].toString()

                // If the user is the driver, adds "(You)" next to their username
                if (isDriver) {
                    driverUsernameTv.text = driver["username"].toString().plus(" (You)")
                } else {
                    driverUsernameTv.text = driver["username"].toString()
                }
                seatsLeftTv.text = "${(tripData["maxPassengers"].toString().toInt()) - seatsLeft}/${tripData["maxPassengers"]}".plus(seatsFull)
                priceTv.text = tripData["price"].toString().plus(" DZD")

                // Join button clicked
                // will add trip info to user document
                // then add user as a passenger in trip ("passengers" collection)
                joinButton.setOnClickListener {

                    // Check if user is online
                    if ((activity as MainActivity).isOnline()) {
                        // User has internet

                        (activity as MainActivity).createLoadingDialog()

                        // New data to put in user document
                        val newUserData = hashMapOf(
                            "isInTrip" to true,
                            "currentTripID" to tripID
                        )

                        // Add the new data to user document
                        db.collection("users").document(auth.currentUser!!.uid).set(newUserData, SetOptions.merge())
                            .addOnSuccessListener {

                                // Cache the new variables
                                (activity as MainActivity).isInTrip = true
                                (activity as MainActivity).currentTripID = tripID

                                // Update the number of seats left in the trip document
                                db.collection("trips").document(tripID).update("seatsLeft", seatsLeft - 1)
                                    .addOnSuccessListener {

                                        // Add the user as a passenger in the "passengers" collection inside the trip document
                                        db.collection("trips").document(tripID)
                                            .collection("passengers").document(auth.currentUser!!.uid).set(
                                                hashMapOf("username" to (activity as MainActivity).username)
                                            )
                                            .addOnSuccessListener {
                                                // Everything successful

                                                (activity as MainActivity).dismissLoadingDialog()

                                                Toast.makeText(context,
                                                    "Trip joined successfully",
                                                    Toast.LENGTH_SHORT).show()

                                                (activity as MainActivity).navBar.selectedItemId = R.id.page_profile
                                            }
                                    }
                            }


                    } else {
                        // No network access

                        Toast.makeText(context,
                            "Cannot connect to the internet, please check your network",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                // Leave button clicked
                // will set user as no longer in trip
                // then remove user from the trip's "passengers" collection
                leaveButton.setOnClickListener {

                    // Check if user is online
                    if ((activity as MainActivity).isOnline()) {
                        // User has internet

                        (activity as MainActivity).createLoadingDialog()

                        // Set user as not in a trip
                        db.collection("users").document(auth.currentUser!!.uid).update("isInTrip", false)
                            .addOnSuccessListener {

                                // Cache variable
                                (activity as MainActivity).isInTrip = true

                                // Update the number of seats left in the trip document
                                db.collection("trips").document(tripID).update("seatsLeft", seatsLeft + 1)
                                    .addOnSuccessListener {

                                        // Delete user from the passengers subcollection
                                        db.collection("trips").document(tripID)
                                            .collection("passengers").document(auth.currentUser!!.uid).delete()
                                            .addOnSuccessListener {
                                                // Everything successful

                                                (activity as MainActivity).dismissLoadingDialog()

                                                Toast.makeText(context,
                                                    "Trip left successfully",
                                                    Toast.LENGTH_SHORT).show()

                                                parentFragmentManager.popBackStack()
                                            }
                                    }
                            }
                    } else {
                        // No network access

                        Toast.makeText(context,
                            "Cannot connect to the internet, please check your network",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                // Delete button clicked
                // Display a warning, if user presses positive button, delete the trip from the DB
                // Prior to deleting, we have to flag driver and all passengers as no longer in a trip
                deleteButton.setOnClickListener {

                    // Display the warning
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to delete this trip?\nthis will also kick all the passengers.")
                        .setNeutralButton("Cancel") { dialog, _ ->
                            // User clicks cancel

                            dialog.dismiss()
                        }
                        .setPositiveButton("Delete") { dialog, _ ->
                            // User clicks delete

                            if ((activity as MainActivity).isOnline()) {
                                // User has network access

                                (activity as MainActivity).createLoadingDialog()

                                // TODO: Kick passengers out of trip

                                // Flag user as no longer in a trip
                                db.collection("users").document(auth.currentUser!!.uid).update("isInTrip", false)
                                    .addOnSuccessListener {

                                        (activity as MainActivity).isInTrip = false

                                        // Delete this trip's document from the DB
                                        db.collection("trips").document(tripID).delete()
                                            .addOnSuccessListener {

                                                (activity as MainActivity).dismissLoadingDialog()

                                                Toast.makeText(context,
                                                    "Trip deleted successfully",
                                                    Toast.LENGTH_SHORT).show()

                                                parentFragmentManager.popBackStack()

                                                dialog.dismiss()
                                            }
                                    }
                                    .addOnFailureListener {
                                        // Failed setting user as no longer in trip

                                        (activity as MainActivity).dismissLoadingDialog()

                                        Toast.makeText(
                                            context,
                                            it.localizedMessage,
                                            Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                // No network access

                                Toast.makeText(context,
                                    "Cannot connect to the internet, please check your network",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        .show()
                }

                (activity as MainActivity).dismissLoadingDialog()
            }
            .addOnFailureListener {
                // Cannot access trip data

                (activity as MainActivity).dismissLoadingDialog()

                Toast.makeText(
                    context,
                    it.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}