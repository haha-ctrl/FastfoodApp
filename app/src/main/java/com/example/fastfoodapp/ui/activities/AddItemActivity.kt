package com.example.fastfoodapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fastfoodapp.R
import com.example.fastfoodapp.firestore.FirestoreClass
import com.example.fastfoodapp.model.Item
import com.example.fastfoodapp.utils.Constants
import com.example.fastfoodapp.utils.GlideLoader
import com.example.fastfoodapp.utils.MSPButton
import com.example.fastfoodapp.utils.MSPEditText
import java.io.IOException

class AddItemActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null
    private var mItemImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        setupActionBar()

        val iv_add_update_item = findViewById<ImageView>(R.id.iv_add_update_item)
        iv_add_update_item.setOnClickListener(this)

        val btn_submit_add_item = findViewById<MSPButton>(R.id.btn_submit_add_item)
        btn_submit_add_item.setOnClickListener(this)
    }

    private fun setupActionBar() {
        val toolbar_add_item_activity = findViewById<Toolbar>(R.id.toolbar_add_item_activity)
        setSupportActionBar(toolbar_add_item_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_item_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_item -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddItemActivity)
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

                R.id.btn_submit_add_item -> {
                    if (validateitemDetails()) {
                        uploadItemImage()
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddItemActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val iv_add_update_item = findViewById<ImageView>(R.id.iv_add_update_item)
        val iv_item_image = findViewById<ImageView>(R.id.iv_item_image)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {

            // Replace the add icon with edit icon once the image is selected.
            iv_add_update_item.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddItemActivity,
                    R.drawable.ic_vector_edit
                )
            )

            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the item image in the ImageView.
                GlideLoader(this@AddItemActivity).loadUserPicture(
                    mSelectedImageFileUri!!,
                    iv_item_image
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun validateitemDetails(): Boolean {
        val et_item_title = findViewById<MSPEditText>(R.id.et_item_title)
        val et_item_price = findViewById<MSPEditText>(R.id.et_item_price)
        val et_item_description = findViewById<MSPEditText>(R.id.et_item_description)
        val et_item_quantity = findViewById<MSPEditText>(R.id.et_item_quantity)

        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_item_image), true)
                false
            }

            TextUtils.isEmpty(et_item_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_item_title), true)
                false
            }

            TextUtils.isEmpty(et_item_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_item_price), true)
                false
            }

            TextUtils.isEmpty(et_item_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_item_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_item_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_item_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {

        mItemImageURL = imageURL

        uploadItemDetails()

    }


    private fun uploadItemDetails() {
        val et_item_title = findViewById<MSPEditText>(R.id.et_item_title)
        val et_item_price = findViewById<MSPEditText>(R.id.et_item_price)
        val et_item_description = findViewById<MSPEditText>(R.id.et_item_description)
        val et_item_quantity = findViewById<MSPEditText>(R.id.et_item_quantity)

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.MY_STORE_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val item = Item (
            FirestoreClass().getCurrentUserID(),
            username,
            et_item_title.text.toString().trim { it <= ' ' },
            et_item_price.text.toString().trim { it <= ' ' },
            et_item_description.text.toString().trim { it <= ' ' },
            et_item_quantity.text.toString().trim { it <= ' ' },
            mItemImageURL
        )

        FirestoreClass().uploadItemDetails(this@AddItemActivity, item)
    }


    private fun uploadItemImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddItemActivity,
            mSelectedImageFileUri,
            Constants.item_IMAGE
        )
    }


    fun itemUploadSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddItemActivity,
            resources.getString(R.string.item_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}