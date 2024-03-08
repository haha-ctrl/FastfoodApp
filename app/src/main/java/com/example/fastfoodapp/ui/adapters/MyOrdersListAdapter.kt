package com.example.fastfoodapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fastfoodapp.R
import com.example.fastfoodapp.model.Order
import com.example.fastfoodapp.ui.activities.MyOrderDetailsActivity
import com.example.fastfoodapp.utils.Constants
import com.example.fastfoodapp.utils.GlideLoader
import com.example.fastfoodapp.utils.MSPTextView
import com.example.fastfoodapp.utils.MSPTextViewBold

// Create an adapter class for my list of orders.
// START
open class MyOrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val iv_item_image = holder.itemView.findViewById<ImageView>(R.id.iv_item_image)
        val tv_item_name = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_item_name)
        val tv_item_price = holder.itemView.findViewById<MSPTextView>(R.id.tv_item_price)
        val ib_delete_item = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_item)

        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadItemPicture(
                model.image,
                iv_item_image
            )

            tv_item_name.text = model.title
            tv_item_price.text = "${model.total_amount}đ"

            ib_delete_item.visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END