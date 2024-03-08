package com.example.fastfoodapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.fastfoodapp.R
import com.example.fastfoodapp.databinding.ActivitySoldItemDetailsBinding
import com.example.fastfoodapp.model.SoldItem
import com.example.fastfoodapp.utils.Constants
import com.example.fastfoodapp.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldItemDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivitySoldItemDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldItemDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupActionBar()

        var itemDetails: SoldItem = SoldItem()

        if (intent.hasExtra(Constants.EXTRA_SOLD_ITEM_DETAILS)) {
            itemDetails =
                intent.getParcelableExtra<SoldItem>(Constants.EXTRA_SOLD_ITEM_DETAILS)!!
        }

        setupUI(itemDetails)
    }


    private fun setupActionBar() {
        val toolbar_sold_item_details_activity = findViewById<Toolbar>(R.id.toolbar_sold_item_details_activity)
        setSupportActionBar(toolbar_sold_item_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sold_item_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun setupUI(itemDetails: SoldItem) {

        binding.tvSoldItemDetailsId.text = itemDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = itemDetails.order_date
        binding.tvSoldItemDetailsDate.text = formatter.format(calendar.time)

        GlideLoader(this@SoldItemDetailsActivity).loadItemPicture(
            itemDetails.image,
            binding.ivItemItemImage
        )
        binding.tvItemItemName.text = itemDetails.title
        binding.tvItemItemPrice.text ="${itemDetails.price}đ"
        binding.tvSoldItemQuantity.text = itemDetails.sold_quantity

        binding.tvSoldDetailsAddressType.text = itemDetails.address.type
        binding.tvSoldDetailsFullName.text = itemDetails.address.name
        binding.tvSoldDetailsAddress.text =
            "${itemDetails.address.address}, ${itemDetails.address.zipCode}"
        binding.tvSoldDetailsAdditionalNote.text = itemDetails.address.additionalNote

        if (itemDetails.address.otherDetails.isNotEmpty()) {
            binding.tvSoldDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvSoldDetailsOtherDetails.text = itemDetails.address.otherDetails
        } else {
            binding.tvSoldDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvSoldDetailsMobileNumber.text = itemDetails.address.mobileNumber

        binding.tvSoldItemSubTotal.text = itemDetails.sub_total_amount
        binding.tvSoldItemShippingCharge.text = itemDetails.shipping_charge
        binding.tvSoldItemTotalAmount.text = itemDetails.total_amount
    }
}