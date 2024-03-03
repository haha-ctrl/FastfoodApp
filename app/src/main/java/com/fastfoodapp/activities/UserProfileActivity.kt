package com.fastfoodapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fastfoodapp.R
import com.fastfoodapp.firestore.FirestoreClass
import com.fastfoodapp.model.User
import com.fastfoodapp.utils.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var muserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Retrieve the User details from intent extra.
        // START
        // Create a instance of the User model class.

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            muserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        // END

        // After receiving the user details from intent set it to the UI.
        // START
        // Here, the some of the edittext components are disabled because it is added at a time of Registration.
        val et_first_name = findViewById<MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<MSPEditText>(R.id.et_last_name)
        val et_email = findViewById<MSPEditText>(R.id.et_email)

        et_first_name.isEnabled = false
        et_first_name.setText(muserDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(muserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(muserDetails.email)
        // END

        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        val btn_summit = findViewById<MSPButton>(R.id.btn_submit)
        btn_summit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        val et_mobile_number = findViewById<MSPEditText>(R.id.et_mobile_number)
        val rb_male = findViewById<MSPRadioButton>(R.id.rb_male)
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Remove the message and Call the image selection function here when the user already have the read storage permission.
                        // START
                        // showErrorSnackBar("You already have the storage permission.",false)
                        Constants.showImageChooser(this@UserProfileActivity)
                        // END
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit ->{
                    if(validateUserProfileDetails()){

                        // Create a HashMap of user details to be updated in the database and add the values init.
                        // START
                        val userHashMap = HashMap<String, Any>()

                        // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.

                        // Here we get the text from editText and trim the space
                        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

                        val gender = if (rb_male.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }

                        if (mobileNumber.isNotEmpty()) {
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }

                        userHashMap[Constants.GENDER] = gender
                        // END
                        showProgressDialog(resources.getString(R.string.please_wait))

                        // call the registerUser function of FireStore class to make an entry in the database.
                        FirestoreClass().updateUserProfileData(
                            this@UserProfileActivity,
                            userHashMap
                        )

                        //showErrorSnackBar("Your details are valid. You can update them.",false)
                    }
                }
            }
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Remove the message and Call the image selection function here when the user grant the read storage permission.
                // START
                // showErrorSnackBar("The storage permission is granted.",false)

                Constants.showImageChooser(this@UserProfileActivity)
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        val selectedImageFileUri = data.data!!

                        // iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(selectedImageFileUri, iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    //  Create a function to validate the input entries for profile details.
    // START
    /**
     * A function to validate the input entries for profile details.
     */
    private fun validateUserProfileDetails(): Boolean {
        val et_mobile_number = findViewById<MSPEditText>(R.id.et_mobile_number)
        return when {

            // We have kept the user profile picture is optional.
            // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number is not empty as it is mandatory to enter.
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }
    // END


    fun userProfileUpdateSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()


        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }
}