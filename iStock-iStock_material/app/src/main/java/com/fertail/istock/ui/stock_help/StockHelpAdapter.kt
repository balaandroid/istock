package com.fertail.istock.ui.stock_help

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.R

class StockHelpAdapter (private val mList: List<ItemsHelpModel>) : RecyclerView.Adapter<StockHelpAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_help, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.fixed_layout.setTag(position)

        holder.hidden_view.visibility  =   if (ItemsViewModel.showDiscription == false){
          View.GONE
        }else {
            View.VISIBLE
        }

        holder.arrow.setBackgroundResource(if (ItemsViewModel.showDiscription == false){
            R.drawable.ic_baseline_keyboard_arrow_down_24
        }else {
            R.drawable.ic_baseline_keyboard_arrow_up_24
        })

        holder.fixed_layout.setOnClickListener {
            val pos = it.getTag() as Int
            val tempModel = mList[pos]
            tempModel.showDiscription = !tempModel.showDiscription!!
            notifyItemChanged(pos)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val helpTitle: TextView = itemView.findViewById(R.id.helpTitle)
        val helpDiscription: TextView = itemView.findViewById(R.id.helpDiscription)
        val arrow: ImageButton = itemView.findViewById(R.id.arrow_button)
        val hidden_view : LinearLayout = itemView.findViewById(R.id.hidden_view)
        val fixed_layout : ConstraintLayout = itemView.findViewById(R.id.fixed_layout)
    }
}