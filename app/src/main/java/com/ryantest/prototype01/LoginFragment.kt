package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = getView()?.findViewById<Button>(R.id.loginCancelButton)

        cancelButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}