package com.fertail.istock.ui.dataclass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ImageZoomActivity
import com.fertail.istock.R
import com.squareup.picasso.Picasso
import java.io.File

class CustomAdapter(private val mList: ArrayList<String>, private val requireActivity: FragmentActivity) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

       if ( item.contains("http")) {
            Picasso.get().load(item).into(holder.imageView)
        }else {
            Picasso.get().load(File(item)).into(holder.imageView)
       }

        holder.imageView.tag = position
        holder.imageView.setOnClickListener {

            ImageZoomActivity.start(requireActivity, object : CustomInterface {

                override fun itemNeedToDelete(position: Int) {
                    mList.removeAt(position)
                    notifyDataSetChanged()
                }

            }, it.getTag() as Int,   mList.get(it.getTag() as Int))
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
    }
}

interface CustomInterface {
    fun itemNeedToDelete(position: Int)
}