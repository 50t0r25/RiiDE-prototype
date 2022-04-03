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
    private lateinit var departure : TextInputEditText
    private lateinit var destination : TextInputEditText

    private var isPassenger = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passengerDriverToggle = requireView().findViewById(R.id.driverPassengerToggle)
        searchCreateButton = requireView().findViewById(R.id.searchCreateButton)
        departure = requireView().findViewById(R.id.departureEt)
        destination = requireView().findViewById(R.id.destinationEt)

        passengerDriverToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                searchCreateButton.text = "Create"
                isPassenger = false
            } else {
                searchCreateButton.text = "Search"
                isPassenger = true
            }
        }

        searchCreateButton.setOnClickListener {
            if ((activity as MainActivity).isLoggedIn) {
                if (isPassenger) {
                    // TODO: Search for a trip
                } else {
                    (activity as MainActivity).departure = departure.text.toString().trim()
                    (activity as MainActivity).destination = destination.text.toString().trim()

                    if (!((activity as MainActivity).departure.equals("") || (activity as MainActivity).destination.equals(""))) {
                        departure.setText("")
                        destination.setText("")

                        (activity as MainActivity).replaceCurrentFragment(CreateTripFragment())
                    } else {
                        Toast.makeText(
                            context, "Please refill the fields correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    context, "Please Login or create an account first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}