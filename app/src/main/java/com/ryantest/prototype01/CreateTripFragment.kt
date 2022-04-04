package com.ryantest.prototype01

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.util.*

class CreateTripFragment : Fragment(R.layout.fragment_adding_trip), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Current time vars
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    // Input time vars
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    private lateinit var departureTv : TextView
    private lateinit var destinationTv : TextView
    private lateinit var dateTimeTv : TextView
    private lateinit var cancelButton : Button
    private lateinit var chooseTimeButton : Button
    private lateinit var confirmButton : Button

    private lateinit var departure : String
    private lateinit var destination : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        departureTv = requireView().findViewById(R.id.departureTv)
        destinationTv = requireView().findViewById(R.id.destinationTv)
        dateTimeTv = requireView().findViewById(R.id.dateTimeTv)
        cancelButton = requireView().findViewById(R.id.cTripCancelButton)
        chooseTimeButton = requireView().findViewById(R.id.chooseTimeButton)
        confirmButton = requireView().findViewById(R.id.cTripConfirmButton)

        departure = (activity as MainActivity).departure
        destination = (activity as MainActivity).destination

        departureTv.text = departure
        destinationTv.text = destination

        // Get the current date and time
        getDateTimeCalendar()
        // Init the Tv with the current time
        displayTimeInTv(hour,minute,day,month,year)

        chooseTimeButton.setOnClickListener {
            // Show the DatePicker, which will then show the TimePicker
            DatePickerDialog(requireContext(),this,year,month,day).show()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    // Function runs when positive button in DatePicker dialog is clicked
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        // Show the TimePicker after choosing the date
        TimePickerDialog(requireContext(),this,hour,minute,true).show()
    }

    // Function runs when positive button in TimePicker dialog is clicked
    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        // Checks if the date isn't in the past
        // Doesn't check the hours/minutes cuz i'm lazy
        if ((savedYear < year) || (savedDay < day && savedMonth == month && savedYear == year) || (savedMonth < month && savedYear == year)) {
            Toast.makeText(context,
                "Time travel is still not a thing",
                Toast.LENGTH_SHORT).show()
        } else {
            displayTimeInTv(savedHour,savedMinute,savedDay,savedMonth,savedYear)
        }
    }

    // Turns an Integer into a 2 digit String (for correct time display)
    // yes i know it's ugly
    fun intDoubleDigit(int : Int) : String {
        var newInt = int.toString()
        if (newInt.length == 1) {
            newInt = "0$newInt"
        }
        return newInt
    }

    // Displays time and date in the TextView
    // AGAIN I KNOW IT4SZ UGLY DONT @ ME
    fun displayTimeInTv(fHour : Int, fMinute : Int, fDay : Int, fMonth : Int, fYear : Int) {
        dateTimeTv.text = "Time: ${intDoubleDigit(fHour)}:${intDoubleDigit(fMinute)}\nDate: ${intDoubleDigit(fDay)}/${intDoubleDigit(fMonth)}/$fYear"
    }
}