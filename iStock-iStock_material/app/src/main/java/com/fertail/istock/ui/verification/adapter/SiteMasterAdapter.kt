package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.SiteMaster

class SiteMasterAdapter constructor(val param: ItemSelectedForNoune) :
    PagingDataAdapter<SiteMaster, SiteMasterAdapter.MainViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SiteMaster>() {
            override fun areItemsTheSame(oldItem: SiteMaster, newItem: SiteMaster): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SiteMaster, newItem: SiteMaster): Boolean =
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
            this.idDescription.text = item?.highLevelLocation?:""
            this.baseCardview.setOnClickListener {
                param.selectedItem(item?.highLevelLocation?:"")
            }
        }


    }
}