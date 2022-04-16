package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var passengerDriverToggle : ToggleButton
    private lateinit var searchCreateButton : Button
    private lateinit var allTripsButton : Button
    private lateinit var departure : TextInputEditText
    private lateinit var destination : TextInputEditText

    private var isPassenger = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passengerDriverToggle = requireView().findViewById(R.id.driverPassengerToggle)
        searchCreateButton = requireView().findViewById(R.id.searchCreateButton)
        allTripsButton = requireView().findViewById(R.id.allTripsButton)
        departure = requireView().findViewById(R.id.departureEt)
        destination = requireView().findViewById(R.id.destinationEt)

        // Checks for the passenger/driver toggle state
        passengerDriverToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                searchCreateButton.text = "Create"
                isPassenger = false
            } else {
                searchCreateButton.text = "Search"
                isPassenger = true
            }
        }

        allTripsButton.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(DisplayTripsFragment())
        }

        searchCreateButton.setOnClickListener {
            if (isPassenger) {
                // TODO: Search for a trip
            } else {
                // Checks if the user is logged in first
                if ((activity as MainActivity).isLoggedIn) {
                    // Get the departure and destination, put them in the activity's global variables
                    // Then start the fragment to create trips
                    (activity as MainActivity).departure = departure.text.toString().trim().lowercase()
                    (activity as MainActivity).destination = destination.text.toString().trim().lowercase()

                    if (!((activity as MainActivity).departure == "" || (activity as MainActivity).destination == "")) {
                        departure.setText("")
                        destination.setText("")

                        (activity as MainActivity).replaceCurrentFragment(CreateTripFragment())
                    } else {
                        // Empty field
                        Toast.makeText(
                            context, "Please refill the fields correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // User not logged in
                    Toast.makeText(
                        context, "Please Login or create an account first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}