package com.ryantest.prototype01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailEt : TextInputEditText
    private lateinit var passwordEt : TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

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

        var email = emailEt.text.toString().trim().lowercase()
        val password = passwordEt.text.toString().trim()

        if (email.isNotBlank() && password.isNotBlank()) {

            (activity as MainActivity?)?.createLoadingDialog()

            // Check if the input is already taken as a username
            // else just log the user assuming the input is an email
            db.collection("users").whereEqualTo("username",email).get()
                .addOnSuccessListener {
                    for (i in it) {
                        if (i != null) email = i.data["email"].toString()
                    }

                    // Authenticate user after checking if the username exists
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {

                                (activity as MainActivity?)?.dismissLoadingDialog()

                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(context,
                                    "Logged in successfully!",
                                    Toast.LENGTH_SHORT).show()

                                // Set the activity's profile fragment variable as the logged in one then load it
                                (activity as MainActivity?)?.fragmentProfile = ProfileLoggedinFragment()
                                (activity as MainActivity?)?.setCurrentFragment(ProfileLoggedinFragment())

                            } else {

                                (activity as MainActivity?)?.dismissLoadingDialog()

                                // If sign in fails, display a message to the user.
                                Toast.makeText(context,
                                    "Authentication failed.\n" + task.exception?.localizedMessage.toString(),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }

                }
                .addOnFailureListener {
                    // Error while checking if the username already exists
                    (activity as MainActivity?)?.dismissLoadingDialog()
                    Toast.makeText(
                        context,
                        "Failed accessing the database.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}