package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.LocationMaster

class LocationHierarchyAdapter constructor(val param: ItemSelectedForNoune) :
    PagingDataAdapter<LocationMaster, LocationHierarchyAdapter.MainViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocationMaster>() {
            override fun areItemsTheSame(oldItem: LocationMaster, newItem: LocationMaster): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LocationMaster, newItem: LocationMaster): Boolean =
                oldItem == newItem
        }
    }

    inner class MainViewHolder(val binding: ItemFunctionalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemFunctionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            this.idDescription.text = item?.locationHierarchy?:""
            this.baseCardview.setOnClickListener {
                param.selectedItem(item?.locationHierarchy?:"")
            }
        }


    }
}