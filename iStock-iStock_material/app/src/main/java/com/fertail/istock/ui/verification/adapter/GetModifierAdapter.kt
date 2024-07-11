package com.fertail.istock.ui.verification.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.ItemAttributeBinding
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.AttributesItem
import com.fertail.istock.model.BusinessesItem
import com.fertail.istock.model.ModifierItem
import com.fertail.istock.model.NounItem


class GetModifierAdapter(
    private val mList: ArrayList<AttributesItem>,
    private val listener: ItemSelectedForNoune
) :
    RecyclerView.Adapter<GetModifierAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFunctionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList.get(position)) {
                binding.idDescription.text = this.modifier
                binding.baseCardview.setOnClickListener {
                    listener.selectedItem(mList[position].modifier?:"")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemFunctionalBinding) :
        RecyclerView.ViewHolder(binding.root)
}
