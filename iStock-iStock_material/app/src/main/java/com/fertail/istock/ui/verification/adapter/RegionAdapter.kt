package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.BusinessesItem
import com.fertail.istock.model.MajorClassesItem
import com.fertail.istock.model.MinorClassesItem
import com.fertail.istock.model.RegionsItem
import com.fertail.istock.ui.verification.FuntionalLocationFragment

class RegionAdapter(
    private val mList: List<RegionsItem>,
    private val listener: FuntionalLocationFragment.itemSelected
) :
    RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFunctionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList.get(position)) {
                binding.idDescription.text = this.region
                binding.baseCardview.setOnClickListener {
                    listener.selectedItem(mList[position].id!!, mList[position].regionCode!!, mList[position].region!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemFunctionalBinding) : RecyclerView.ViewHolder(binding.root)
}
