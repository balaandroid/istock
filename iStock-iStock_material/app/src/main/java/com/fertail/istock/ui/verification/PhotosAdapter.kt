package com.fertail.istock.ui.verification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.R
import com.fertail.istock.ui.ReportClickListener
import com.fertail.istock.ui.stock_help.ItemsHelpModel

class PhotosAdapter (private val mList: List<ItemsHelpModel>,
                     private val listener : ReportClickListener) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_upload, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        if (position > 1) {
            holder.imgClose.setBackgroundResource(R.drawable.download_completed_icon)
        }else {
            holder.imgClose.setBackgroundResource(R.drawable.close_icon)
        }

        holder.itemView.setOnClickListener {
//            listener.itemClicked()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var imgClose : ImageButton = ItemView.findViewById(R.id.imgClose)
    }
}