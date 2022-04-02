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

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailEt: TextInputEditText
    private lateinit var usernameEt: TextInputEditText
    private lateinit var passwordEt: TextInputEditText
    private lateinit var passwordConfirmEt: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        val confirmButton = getView()?.findViewById<Button>(R.id.confirmSignupButton)
        val cancelButton = getView()?.findViewById<Button>(R.id.signupCancelButton)
        emailEt = requireView().findViewById(R.id.signupEmailEt)
        usernameEt = requireView().findViewById(R.id.signupUsernameEt)
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
        var usernameTaken = false
        val email = emailEt.text.toString().trim()
        val username = usernameEt.text.toString().trim().lowercase()
        val password = passwordEt.text.toString().trim()
        val passwordConfirm = passwordConfirmEt.text.toString().trim()


        val user = hashMapOf(
            "username" to username,
            "email" to email,
        )


        if (password.equals(passwordConfirm, false)
            && email.isNotBlank()
            && password.isNotBlank()
            && checkStringSafety(username)
        ) {

            (activity as MainActivity).createLoadingDialog()

            db.collection("users").whereEqualTo("username",username).get()
                .addOnSuccessListener { documents ->
                    // Check if the username is already taken
                    for (document in documents) { if (document != null) usernameTaken = true }
                    if (!usernameTaken) {
                        // Authenticate the user
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success

                                    // Add user to the database
                                    db.collection("users").document(auth.currentUser!!.uid).set(user)

                                    Toast.makeText(
                                        context,
                                        "Account successfully created",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Save user data
                                    (activity as MainActivity).userEmail = email
                                    (activity as MainActivity).username = username

                                    // Refreshes the activity to logged in state and sets the new fragment
                                    (activity as MainActivity).refreshMainActivity()
                                    (activity as MainActivity).setCurrentFragment(ProfileLoggedinFragment())

                                    (activity as MainActivity).dismissLoadingDialog()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    (activity as MainActivity).dismissLoadingDialog()
                                    Toast.makeText(
                                        context,
                                        "Authentication failed\n" + task.exception?.localizedMessage.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    } else {
                        // If the username is already taken, cancel
                        (activity as MainActivity).dismissLoadingDialog()
                        Toast.makeText(
                            context,
                            "Username already taken",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnFailureListener {
                    // Error while checking if the username already exists
                    (activity as MainActivity).dismissLoadingDialog()
                    Toast.makeText(
                        context,
                        "Failed to access the database\n" + it.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            if (!checkStringSafety(username)) {
                // Username isn't safe
                Toast.makeText(
                    context, "Username cannot contain '@' and must be 15 characters or less",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Input not filled in correctly
                Toast.makeText(
                    context, "Please refill the fields correctly",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Used to make sure the username is sanitized to avoid exploits
    private fun checkStringSafety(input : String) : Boolean {
        return !((input.length >15) or (input.contains('@')))
    }
}