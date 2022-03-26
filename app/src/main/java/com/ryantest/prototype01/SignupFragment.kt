package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class SignupFragment : Fragment(R.layout.fragment_signup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = getView()?.findViewById<Button>(R.id.signupCancelButton)

        cancelButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}