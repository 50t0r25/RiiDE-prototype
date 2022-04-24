package com.ryantest.prototype01

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserInfoFragment(private val userID: String) : Fragment(R.layout.fragment_user_info) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var username : TextView
    private lateinit var email : TextView
    private lateinit var infoTitle : TextView
    private lateinit var infoTv : TextView
    private lateinit var ratingTv : TextView
    private lateinit var infoTextField : TextInputLayout
    private lateinit var infoEditText : TextInputEditText

    private lateinit var ratingBar : RatingBar
    private lateinit var infoScrollView : ScrollView

    private lateinit var backButton : Button
    private lateinit var editSaveButton : Button
    private lateinit var submitRatingButton : Button
    private lateinit var cancelEditButton : Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        username = requireView().findViewById(R.id.userInfoUsernameTv)
        email = requireView().findViewById(R.id.userInfoEmailTv)
        infoTitle = requireView().findViewById(R.id.userInfoSmallTitleTv)
        infoTv = requireView().findViewById(R.id.userInfoTv)
        ratingTv = requireView().findViewById(R.id.userCurrentRatingTv)
        ratingBar = requireView().findViewById(R.id.userInfoRatingBar)
        infoTextField = requireView().findViewById(R.id.userInfoTextField)
        infoEditText = requireView().findViewById(R.id.newInfoEt)
        infoScrollView = requireView().findViewById(R.id.infoScrollView)

        backButton = requireView().findViewById(R.id.userInfoBackButton)
        editSaveButton = requireView().findViewById(R.id.editSaveInfoButton)
        submitRatingButton = requireView().findViewById(R.id.userInfoSubmitRatingButton)
        cancelEditButton = requireView().findViewById(R.id.userInfoCancelEditButton)

        var isCurrentUserProfile = false
        if ((activity as MainActivity).isLoggedIn) {
            if (auth.currentUser!!.uid == userID) isCurrentUserProfile = true
        } else {
            submitRatingButton.visibility = View.GONE
        }

        if (isCurrentUserProfile) {

            submitRatingButton.visibility = View.GONE

            ratingBar.setOnTouchListener { v, event -> true }

        } else {

            email.visibility = View.GONE
            editSaveButton.visibility = View.GONE

        }

        var isEditing = false
        editSaveButton.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                infoScrollView.visibility = View.GONE
                infoTextField.visibility = View.VISIBLE
                if ((activity as MainActivity).filledInfo) infoEditText.setText(infoTv.text)
                editSaveButton.text = "Save"
                cancelEditButton.visibility = View.VISIBLE
            } else {

                val newInfo = infoEditText.text!!.toString().trim()
                val newData = hashMapOf(
                    "info" to newInfo,
                    "filledInfo" to true
                )

                if ((activity as MainActivity).isOnline()) {

                    (activity as MainActivity).createLoadingDialog()

                    db.collection("users").document(auth.currentUser!!.uid)
                        .set(newData, SetOptions.merge())
                        .addOnSuccessListener {
                            (activity as MainActivity).filledInfo = true
                            isEditing = false
                            infoTextField.visibility = View.GONE
                            infoScrollView.visibility = View.VISIBLE
                            infoTv.text = newInfo
                            editSaveButton.text = "Edit"
                            cancelEditButton.visibility = View.GONE

                            (activity as MainActivity).dismissLoadingDialog()

                        }
                } else {

                    Toast.makeText(context,
                        "Cannot connect to the internet, please check your network",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }

        cancelEditButton.setOnClickListener {
            isEditing = false
            infoTextField.visibility = View.GONE
            infoScrollView.visibility = View.VISIBLE
            editSaveButton.text = "Edit"
            cancelEditButton.visibility = View.GONE
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        (activity as MainActivity).createLoadingDialog()

        db.collection("users").document(userID).get()
            .addOnSuccessListener { user ->
                val localUsername = user.data!!["username"].toString()
                val filledInfo = user.data!!["filledInfo"].toString().toBoolean()
                username.text = localUsername
                email.text = user.data!!["email"].toString()
                infoTitle.text = localUsername.plus("'s info:")

                if (filledInfo) {
                    infoTv.text = user.data!!["info"].toString()
                } else {
                    infoTv.text = "No user info provided yet."
                }

                (activity as MainActivity).dismissLoadingDialog()
            }
    }
}