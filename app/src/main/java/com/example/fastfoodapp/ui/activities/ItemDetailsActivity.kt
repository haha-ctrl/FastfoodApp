package com.example.fastfoodapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.example.fastfoodapp.R
import com.example.fastfoodapp.firestore.FirestoreClass
import com.example.fastfoodapp.model.Item
import com.example.fastfoodapp.utils.Constants
import com.example.fastfoodapp.utils.GlideLoader
import com.example.fastfoodapp.utils.MSPTextView
import com.example.fastfoodapp.utils.MSPTextViewBold
import com.google.firebase.firestore.FirebaseFirestore


class ItemDetailsActivity : BaseActivity() {
    private var mItemId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mItemId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mItemId)
        }

        getItemDetails()


    }


    private fun setupActionBar() {
        val toolbar_item_details_activity = findViewById<Toolbar>(R.id.toolbar_item_details_activity)

        setSupportActionBar(toolbar_item_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_item_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun itemDetailsSuccess(item: Item) {
        val iv_item_detail_image = findViewById<ImageView>(R.id.iv_item_detail_image)
        val tv_item_details_title = findViewById<MSPTextViewBold>(R.id.tv_item_details_title)
        val tv_item_details_price = findViewById<MSPTextView>(R.id.tv_item_details_price)
        val tv_item_details_description = findViewById<MSPTextView>(R.id.tv_item_details_description)
        val tv_item_details_available_quantity = findViewById<MSPTextView>(R.id.tv_item_details_available_quantity)
        // Hide Progress dialog.
        hideProgressDialog()

        // Populate the item details in the UI.
        GlideLoader(this@ItemDetailsActivity).loadItemPicture(
            item.image,
            iv_item_detail_image
        )

        tv_item_details_title.text = item.title
        tv_item_details_price.text = "${item.price}đ"
        tv_item_details_description.text = item.description
        tv_item_details_available_quantity.text = item.stock_quantity
    }


    private fun getItemDetails() {

        // Show the item dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the item details.
        FirestoreClass().getProductDetails(this@ItemDetailsActivity, mItemId)
    }
}