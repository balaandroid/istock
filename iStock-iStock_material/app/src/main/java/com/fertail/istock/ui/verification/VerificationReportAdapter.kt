package com.fertail.istock.ui.verification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.Constant
import com.fertail.istock.databinding.ItemReportNewBinding
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.ReportClickListener



class VerificationReportAdapter(
    private val mList: List<PVData>,
    private val listener: ReportClickListener, ) :
    RecyclerView.Adapter<VerificationReportAdapter.ViewHolder>(), Constant {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {
                binding.oldTagText.text = this.assetNo
                binding.ideqpno.text = this.stroageBin
                binding.oldTagNoValue.text = this.stroageLocation
                binding.idDescriptionContent.text = this.equipmentDesc

                binding.baseCardview.setOnClickListener {
                    val itemselected = mList[position]
//                    itemselected.equipment = ""
                    listener.itemClicked(itemselected)
                }

                binding.conatner4.setOnClickListener {
                    val itemselected = mList[position]
//                    itemselected.equipment = "Not Available"
                    listener.itemClicked(itemselected)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemReportNewBinding) : RecyclerView.ViewHolder(binding.root)

}
