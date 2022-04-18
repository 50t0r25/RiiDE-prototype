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

class DisplayPassengersFragment(newTripID: String) : Fragment(R.layout.fragment_display_passengers) {

    private lateinit var passengersRv : RecyclerView
    private lateinit var noPassengersFoundTv : TextView
    private lateinit var backButton : Button
    private lateinit var db: FirebaseFirestore
    private lateinit var passengersList: MutableList<PassengerItem>

    private val tripID = newTripID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passengersRv = requireView().findViewById(R.id.passengersRv)
        noPassengersFoundTv = requireView().findViewById(R.id.noPassengersFoundTv)
        backButton = requireView().findViewById(R.id.displayPassengersBackButton)

        db = Firebase.firestore
        passengersList = emptyList<PassengerItem>().toMutableList()

        // Makes function for item clicks that will be passed to the adapter constructor
        // Will be used inside the adapter as a click listener to load X passenger's profile
        fun onListItemClick(position: Int) {
            // TODO: Load passenger's profile
            // (activity as MainActivity).replaceCurrentFragment(UserInfoFragment(passengersList[position].ID))
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        (activity as MainActivity).createLoadingDialog()

        // Fetch all passengers in trip from db
        db.collection("trips").document(tripID)
            .collection("passengers").get()
            .addOnSuccessListener { passengers ->

                // If no passengers are found, hide recyclerView and show the no passengers found text
                if (passengers.isEmpty) {

                    passengersRv.visibility = View.GONE
                    noPassengersFoundTv.visibility = View.VISIBLE
                    (activity as MainActivity).dismissLoadingDialog()

                } else {

                    // Save each passengers ID and username to the PassengerItem List
                    for (passenger in passengers) {

                        passengersList.add(PassengerItem(passenger.id, passenger["username"].toString()))
                    }

                    (activity as MainActivity).dismissLoadingDialog()

                    // Initialize the adapter to show the list of found passengers inside the recyclerView
                    val adapter = PassengersAdapter(passengersList,{position -> onListItemClick(position)})
                    passengersRv.adapter = adapter
                    passengersRv.layoutManager = LinearLayoutManager(context)

                }
            }

    }

}