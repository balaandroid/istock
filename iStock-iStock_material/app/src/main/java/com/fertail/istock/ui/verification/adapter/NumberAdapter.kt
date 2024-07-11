package com.fertail.istock.ui.verification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.R
import com.fertail.istock.databinding.ItemFunctionalBinding
import com.fertail.istock.databinding.PageInnerItemBinding
import com.fertail.istock.model.*
import com.fertail.istock.ui.verification.BOMFragment
import com.fertail.istock.ui.verification.FuntionalLocationFragment
import com.fertail.istock.ui.verification.itemClicked

class NumberAdapter(
    private val mList: ArrayList<NumberData>,
    private val listener: itemClicked,
    private val requireActivity: FragmentActivity
) :
    RecyclerView.Adapter<NumberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PageInnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.myTextView.text = mList[position].number

                if (this.isSelected) {
                    binding.myTextView.setBackgroundResource(R.drawable.bg_paging_rect_primary)
                    binding.myTextView.setTextColor(requireActivity.resources.getColor(R.color.white))
                }else {
                    binding.myTextView.setBackgroundResource(R.drawable.bg_paging_rect)
                    binding.myTextView.setTextColor(requireActivity.resources.getColor(R.color.black))
                }

                binding.myTextView.setTag(position)
                binding.myTextView.setOnClickListener {
                   val pos = binding.myTextView.getTag() as Int
                    mList.forEach {
                        it.isSelected = it.number!!.equals(mList[pos].number)
                    }

                    notifyDataSetChanged()
                    listener.clickedItem(pos)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: PageInnerItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
