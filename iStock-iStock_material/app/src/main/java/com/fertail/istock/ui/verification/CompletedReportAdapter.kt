package com.fertail.istock.ui.verification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.Constant
import com.fertail.istock.R
import com.fertail.istock.databinding.ItemCompletedReportBinding
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.ReportClickListener
import com.fertail.istock.util.CommonUtils

class CompletedReportAdapter(
    val mList: ArrayList<PVData>,
    private val listener: ReportClickListener,
    private val requireContext: Context
) :
    RecyclerView.Adapter<CompletedReportAdapter.ViewHolder>(), Constant {

    var showSelectIcon: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCompletedReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateshowSelectIcon(select: Boolean) {
        showSelectIcon = select
        if (!select) {
            mList.forEach {
                it.isSelected = false
            }
        }else{
            var i =0
            mList.forEach {
                if (i < 10 ){
                    it.isSelected = true
                    i++
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {
                if(this.category.equals("Equipment",true)){
                    binding.oldTagText.text = this.fixedAssetNo
                    binding.ideqpno.text = this.assetNo
                    binding.idDescriptionContent.text = this.equipmentDesc
                    binding.oldTagNoValue.text = this.oldTagNo
                }else{
                    binding.txtEquip.text="Bin :"
                    binding.idfl.text="ItemCode :"
                    binding.idDescription.text="Material Dec :"
                    binding.oldTagNoDiscription.text="S.Location :"

                    binding.oldTagText.text = this.assetNo
                    binding.ideqpno.text = this.stroageBin
                    binding.idDescriptionContent.text = this.equipmentDesc
                    binding.oldTagNoValue.text = this.stroageLocation
                }

                if (showSelectIcon) {
                    binding.selectItem.visibility = View.VISIBLE
                } else {
                    binding.selectItem.visibility = View.GONE
                }

                if (this.isSelected) {
                    binding.selectItem.setBackgroundResource(R.drawable.ic_check_box_blue)
                } else {
                    binding.selectItem.setBackgroundResource(R.drawable.ic_check_box)
                }

                binding.baseCardview.setOnClickListener {
                    listener.itemClicked(mList[position])
                }
                binding.selectItem.tag = position
                binding.selectItem.setOnClickListener {

                    val pos = binding.selectItem.tag as Int

                    if (mList.filter { s -> (s.isSelected) }.size < 10) {
                        val selectedData = mList[pos]
                        selectedData.isSelected = !selectedData.isSelected
                        mList.set(pos, selectedData)
                        notifyItemChanged(pos)
                    } else {

                        val selectedData = mList[pos]
                        if (selectedData.isSelected) {
                            selectedData.isSelected = false
                            mList.set(pos, selectedData)
                            notifyItemChanged(pos)
                        } else {
                            CommonUtils.showAlert(
                                requireContext,
                                "You can only select a maximum of 10 items"
                            )
                        }
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemCompletedReportBinding) :
        RecyclerView.ViewHolder(binding.root)
}
