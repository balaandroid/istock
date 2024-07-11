package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.AttributesItem
import com.fertail.istock.model.NounTable
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notifyAll
import javax.inject.Inject

class PagingAdapter constructor(val param: ItemSelectedForNoune) :
    PagingDataAdapter<NounTable, PagingAdapter.MainViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NounTable>() {
            override fun areItemsTheSame(oldItem: NounTable, newItem: NounTable): Boolean =
                oldItem.localId == newItem.localId

            override fun areContentsTheSame(oldItem: NounTable, newItem: NounTable): Boolean =
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
            this.idDescription.text = item?.noun?:""

            this.baseCardview.setOnClickListener {
                param.selectedItem(item?.noun?:"")
            }
        }


    }
}