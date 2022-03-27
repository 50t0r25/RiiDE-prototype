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

    private lateinit var emailEt : TextInputEditText
    private lateinit var passwordEt : TextInputEditText
    private lateinit var passwordConfirmEt : TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val confirmButton = getView()?.findViewById<Button>(R.id.confirmSignupButton)
        val cancelButton = getView()?.findViewById<Button>(R.id.signupCancelButton)
        emailEt = requireView().findViewById(R.id.signupEmailEt)
        passwordEt = requireView().findViewById(R.id.signupPasswordEt)
        passwordConfirmEt = requireView().findViewById(R.id.signupPasswordConfirmEt)

        cancelButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        confirmButton?.setOnClickListener {
            singupUser()
        }
    }

    private fun singupUser() {
        val email = emailEt.text.toString().trim()
        val password = passwordEt.text.toString().trim()
        val passwordConfirm = passwordConfirmEt.text.toString().trim()

        if (password.equals(passwordConfirm, false)
            && email.isNotBlank()
            && password.isNotBlank()) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI to logged in profile fragment
                        Toast.makeText(context,
                            "Account successfully created!",
                            Toast.LENGTH_SHORT).show()

                        //set the activity's profile fragment variable as the logged in one then load it
                        (activity as MainActivity?)?.fragmentProfile = ProfileLoggedinFragment()
                        (activity as MainActivity?)?.setCurrentFragment(ProfileLoggedinFragment())

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            context,
                            "Authentication failed.\n" + task.exception?.localizedMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(context, "Please refill the fields correctly.",
                Toast.LENGTH_SHORT).show()
        }
    }
}