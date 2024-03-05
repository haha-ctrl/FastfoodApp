package com.example.fastfoodapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fastfoodapp.R
import com.example.fastfoodapp.model.Item
import com.example.fastfoodapp.utils.GlideLoader
import com.example.fastfoodapp.utils.MSPTextView
import com.example.fastfoodapp.utils.MSPTextViewBold

class DashboardItemsListAdapter (
    private val context: Context,
    private var list: ArrayList<Item>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        val iv_dashboard_item_image = holder.itemView.findViewById<ImageView>(R.id.iv_dashboard_item_image)
        val tv_dashboard_item_title = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_dashboard_item_title)
        val tv_dashboard_item_price = holder.itemView.findViewById<MSPTextView>(R.id.tv_dashboard_item_price)

        if (holder is MyViewHolder) {
            GlideLoader(context).loadItemPicture(
                model.image,
                iv_dashboard_item_image
            )
            tv_dashboard_item_title.text = model.title
            tv_dashboard_item_price.text = "${model.price}đ"
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
