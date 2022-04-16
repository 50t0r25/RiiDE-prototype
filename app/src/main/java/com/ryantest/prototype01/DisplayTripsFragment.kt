package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DisplayTripsFragment : Fragment(R.layout.fragment_display_trips) {

    private lateinit var tripsRv : RecyclerView
    private lateinit var noTripsFoundTv : TextView
    private lateinit var backButton : Button
    private lateinit var db: FirebaseFirestore
    private lateinit var tripsList: MutableList<TripItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripsRv = requireView().findViewById(R.id.tripsRv)
        noTripsFoundTv = requireView().findViewById(R.id.noTripsFoundTv)
        backButton = requireView().findViewById(R.id.displayTripsBackButton)

        db = Firebase.firestore

        // Makes function for item clicks that will be passed to the adapter constructor
        // Will be used inside the adapter as a click listener to load a X trip's details
        fun onListItemClick(tripItems: List<TripItem>,position: Int) {
            (activity as MainActivity).replaceCurrentFragment(TripDetailsFragment(tripItems[position].ID))
        }
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tripsList = emptyList<TripItem>().toMutableList()

        (activity as MainActivity).createLoadingDialog()
        // Fetch all trips from db
        db.collection("trips").get()
            .addOnSuccessListener { trips ->
                // If no trips are found, hide recyclerView and show the no trips available text
                if (trips.isEmpty) {
                    tripsRv.visibility = View.GONE
                    noTripsFoundTv.visibility = View.VISIBLE
                    (activity as MainActivity).dismissLoadingDialog()
                } else {
                    // Save every found trip's data inside the tripsList list of TripItem(s)
                    // TripItem is a data class that stores all needed info to show trips inside small elements
                    for (trip in trips) {
                        val date = trip.data["date"] as HashMap<*, *>

                        val tripID = trip.id
                        val fromTo = "From ${trip.data["departure"]} to ${trip.data["destination"]}"
                        val price = "${trip.data["price"]} DZD"
                        val seats = "${(trip.data["seatsLeft"].toString().toInt() - trip.data["maxPassengers"].toString().toInt())}/${trip.data["maxPassengers"]} Seats"
                        val tripDate = (activity as MainActivity).formatDate(date["day"].toString().toInt(),date["month"].toString().toInt(),date["year"].toString().toInt())

                        tripsList.add(TripItem(tripID,fromTo,price,seats,tripDate))
                    }
                    (activity as MainActivity).dismissLoadingDialog()

                    // Initialize the adapter to show the list of found trips inside the recyclerView
                    val adapter = TripsAdapter(tripsList,{tripItems,position -> onListItemClick(tripItems,position)})
                    tripsRv.adapter = adapter
                    tripsRv.layoutManager = LinearLayoutManager(context)
                }
        }
    }

}