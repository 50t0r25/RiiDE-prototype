package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var auth: FirebaseAuth

    lateinit var emailEt : TextInputEditText
    lateinit var passwordEt : TextInputEditText
    lateinit var passwordConfirmEt : TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val confirmButton = getView()?.findViewById<Button>(R.id.confirmSignupButton)
        val cancelButton = getView()?.findViewById<Button>(R.id.signupCancelButton)
        emailEt = requireView().findViewById(R.id.loginEmailEt)
        passwordEt = requireView().findViewById(R.id.loginPasswordEt)
        passwordConfirmEt = requireView().findViewById(R.id.loginPasswordConfirmEt)
        cancelButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        confirmButton?.setOnClickListener {
            singupUser()
        }
    }

    fun singupUser() {
        val email = emailEt.text.toString().trim()
        val password = passwordEt.text.toString()
        val passwordConfirm = passwordConfirmEt.text.toString()

        if (password.equals(passwordConfirm, false) && email.isNotBlank()) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Toast.makeText(context, "Authentication successful.",
                            Toast.LENGTH_SHORT).show()
                        //TODO Update UI
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            //TODO add error handling
        }
    }
}