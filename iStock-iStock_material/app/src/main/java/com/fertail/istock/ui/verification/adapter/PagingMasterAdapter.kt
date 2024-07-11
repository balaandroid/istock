package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.AllMasterItem

class PagingMasterAdapter constructor(val param: ItemSelectedForNoune) :
    PagingDataAdapter<AllMasterItem, PagingMasterAdapter.MainViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AllMasterItem>() {
            override fun areItemsTheSame(oldItem: AllMasterItem, newItem: AllMasterItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AllMasterItem, newItem: AllMasterItem): Boolean =
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
            this.idDescription.text = item?.fARId?:""

            this.baseCardview.setOnClickListener {

                var value  = item?.fARId + "##"+item?.region

                param.selectedItem(value)
            }
        }


    }
}