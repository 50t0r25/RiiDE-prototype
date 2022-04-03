package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class CreateTripFragment : Fragment(R.layout.fragment_adding_trip) {

    private lateinit var departureTv : TextView
    private lateinit var destinationTv : TextView
    private lateinit var cancelButton : Button
    private lateinit var chooseTimeButton : Button
    private lateinit var confirmButton : Button

    private lateinit var departure : String
    private lateinit var destination : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        departureTv = requireView().findViewById(R.id.departureTv)
        destinationTv = requireView().findViewById(R.id.destinationTv)
        cancelButton = requireView().findViewById(R.id.cTripCancelButton)
        chooseTimeButton = requireView().findViewById(R.id.chooseTimeButton)
        confirmButton = requireView().findViewById(R.id.cTripConfirmButton)

        departure = (activity as MainActivity).departure
        destination = (activity as MainActivity).destination

        departureTv.text = departure
        destinationTv.text = destination

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }
}