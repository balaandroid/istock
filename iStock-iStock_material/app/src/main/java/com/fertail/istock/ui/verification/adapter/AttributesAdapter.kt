package com.fertail.istock.ui.verification.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.VerificationDetailsMobileActivity
import com.fertail.istock.VerificationDetailsNewActivity
import com.fertail.istock.databinding.ItemAttributeBinding
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.model.AttributesItem
import com.fertail.istock.model.BusinessesItem
import com.fertail.istock.model.ModifierItem
import com.fertail.istock.model.NounItem


class AttributesAdapter(
    private val mList: List<AttributesItem>,
    val action: String
) :
    RecyclerView.Adapter<AttributesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder) {
            with(mList.get(position)) {
                binding.lefttext.text = this.characteristic
                binding.rigttext.setText(this.definition)

                binding.rigttext.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (action.equals("mobile")){
                            VerificationDetailsMobileActivity.mPVData.characteristics.get(position).definition =  s.toString()
                        }else{
                            VerificationDetailsNewActivity.mPVData.characteristics.get(position).definition =  s.toString()
                        }
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemAttributeBinding) : RecyclerView.ViewHolder(binding.root)
}
