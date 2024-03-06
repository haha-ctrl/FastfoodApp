package com.example.fastfoodapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.fastfoodapp.R
import com.example.fastfoodapp.firestore.FirestoreClass
import com.example.fastfoodapp.model.CartItem
import com.example.fastfoodapp.model.Item
import com.example.fastfoodapp.utils.*
import com.google.firebase.firestore.FirebaseFirestore


class ItemDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mItemId: String = ""
    private lateinit var mItemDetails: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ITEM_ID)) {
            mItemId =
                intent.getStringExtra(Constants.EXTRA_ITEM_ID)!!
            Log.i("Item Id", mItemId)
        }

        var itemOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_ITEM_OWNER_ID)) {
            itemOwnerId =
                intent.getStringExtra(Constants.EXTRA_ITEM_OWNER_ID)!!
        }

        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<MSPButton>(R.id.btn_go_to_cart)
        if (FirestoreClass().getCurrentUserID() == itemOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        btn_add_to_cart.setOnClickListener(this)

        getItemDetails()

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
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
        val tv_item_details_stock_quantity = findViewById<MSPTextView>(R.id.tv_item_details_stock_quantity)
        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)


        mItemDetails = item

        // Populate the item details in the UI.
        GlideLoader(this@ItemDetailsActivity).loadItemPicture(
            item.image,
            iv_item_detail_image
        )

        tv_item_details_title.text = item.title
        tv_item_details_price.text = "${item.price}đ"
        tv_item_details_description.text = item.description
        tv_item_details_stock_quantity.text = item.stock_quantity




        if(item.stock_quantity.toInt() == 0){

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            btn_add_to_cart.visibility = View.GONE

            tv_item_details_stock_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            tv_item_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this@ItemDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{

            // There is no need to check the cart list if the item owner himself is seeing the item details.
            if (FirestoreClass().getCurrentUserID() == item.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ItemDetailsActivity, mItemId)
            }
        }

    }


    private fun getItemDetails() {

        // Show the item dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the item details.
        FirestoreClass().getItemDetails(this@ItemDetailsActivity, mItemId)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ItemDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }


    private fun addToCart() {

        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mItemId,
            mItemDetails.title,
            mItemDetails.price,
            mItemDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ItemDetailsActivity, cartItem)
    }


    fun addToCartSuccess() {
        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<MSPButton>(R.id.btn_go_to_cart)

        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@ItemDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun itemExistsInCart() {
        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<MSPButton>(R.id.btn_go_to_cart)
        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }

}