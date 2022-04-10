package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
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

class TripDetailsFragment : Fragment(R.layout.fragment_trip_details) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var timeTv : TextView
    private lateinit var departureTv : TextView
    private lateinit var destinationTv : TextView
    private lateinit var driverUsernameTv : TextView
    private lateinit var seatsLeftTv : TextView
    private lateinit var priceTv : TextView

    private lateinit var backButton : TextView
    private lateinit var deleteButton : TextView

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
        deleteButton = requireView().findViewById(R.id.deleteDetailsTv)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        (activity as MainActivity).createLoadingDialog()
        // Get the current user data from DB or Cache
        db.collection("users").document(auth.currentUser!!.uid).get(Source.CACHE)
            .addOnSuccessListener { user ->
                var isDriver = false
                // Get the tripID that the user is currently in
                val tripID = user.data?.get("currentTripID").toString()
                db.collection("trips").document(tripID).get()
                    .addOnSuccessListener { trip ->
                        // Save the trip data to their variables
                        // TODO: save passengers data
                        // Since Date and Driver are Object fields, we cast them as HashMaps
                        val tripData = trip.data
                        val date = tripData?.get("date") as HashMap<*, *>
                        val driver = tripData["driver"] as HashMap<*, *>
                        // Check if the user is the current driver
                        if (driver["userID"]?.equals(auth.currentUser!!.uid)!!) isDriver = true

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
                        seatsLeftTv.text = "${(tripData["seatsLeft"].toString().toInt() - tripData["maxPassengers"].toString().toInt())}/${tripData["maxPassengers"]}"
                        priceTv.text = tripData["price"].toString().plus(" DZD")

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
                                    (activity as MainActivity).createLoadingDialog()
                                    // TODO: Kick passengers out of trip
                                    // Flag user as no longer in a trip
                                    db.collection("users").document(auth.currentUser!!.uid).update("isInTrip", false)
                                        .addOnSuccessListener {
                                            // Delete this trip's document from the DB
                                            db.collection("trips").document(tripID).delete()
                                                .addOnSuccessListener {
                                                    (activity as MainActivity).dismissLoadingDialog()
                                                    Toast.makeText(context,
                                                        "Trip deleted successfully",
                                                        Toast.LENGTH_SHORT).show()
                                                    (activity as MainActivity).isInTrip = false
                                                    parentFragmentManager.popBackStack()

                                                    dialog.dismiss()
                                                }
                                        }
                                        .addOnFailureListener {
                                            // Failed setting user as no longer in trip
                                            (activity as MainActivity).dismissLoadingDialog()
                                            Toast.makeText(
                                                context,
                                                "Failed to access the database\n" + it.localizedMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()
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
                            "Failed to access the database\n" + it.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                // Can't access cached database to fetch user data
                (activity as MainActivity).dismissLoadingDialog()
                Toast.makeText(
                    context,
                    "Failed to access the database\n" + it.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}