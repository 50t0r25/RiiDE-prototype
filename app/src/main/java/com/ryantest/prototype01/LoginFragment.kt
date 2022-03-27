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

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEt : TextInputEditText
    private lateinit var passwordEt : TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val confirmButton = getView()?.findViewById<Button>(R.id.confirmLoginButton)
        val cancelButton = getView()?.findViewById<Button>(R.id.loginCancelButton)
        emailEt = requireView().findViewById(R.id.loginEmailEt)
        passwordEt = requireView().findViewById(R.id.loginPasswordEt)

        cancelButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        confirmButton?.setOnClickListener {
            login()
        }
    }

    private fun login() {

        val email = emailEt.text.toString().trim()
        val password = passwordEt.text.toString().trim()

        if (email.isNotBlank() && password.isNotBlank()) {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(context,
                            "Logged in successfully!",
                            Toast.LENGTH_SHORT).show()

                        //set the activity's profile fragment variable as the logged in one then load it
                        (activity as MainActivity?)?.fragmentProfile = ProfileLoggedinFragment()
                        (activity as MainActivity?)?.setCurrentFragment(ProfileLoggedinFragment())

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(context,
                            "Authentication failed.\n" + task.exception?.localizedMessage.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}