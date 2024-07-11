package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.AllMasterItem


class MasterAdapter(
    private val mList: ArrayList<AllMasterItem>,
    private val listener: ItemSelectedForNoune
) :
    RecyclerView.Adapter<MasterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFunctionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList.get(position)) {
                binding.idDescription.text = this.fARId
                binding.baseCardview.setOnClickListener {
                    mList[position].fARId?.let { it1 -> listener.selectedItem(it1) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemFunctionalBinding) : RecyclerView.ViewHolder(binding.root)
}
