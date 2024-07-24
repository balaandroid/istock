package com.fertail.istock.ui.verification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.Constant
import com.fertail.istock.databinding.ItemReportBinding
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.ReportClickListener


class VerificationReportLatestAdapter(
    private val mList: ArrayList<PVData>,
    private val listener: ReportClickListener, ) :
    RecyclerView.Adapter<VerificationReportLatestAdapter.ViewHolder>(), Constant {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {
                binding.oldTagText.text = this.fixedAssetNo
                binding.ideqpno.text = this.assetNo
                binding.oldTagNoValue.text = this.oldTagNo
                binding.idDescriptionContent.text = this.equipmentDesc
                binding.serialNoTxt!!.text=this.serialNo

                if (this.rework?.equals("PV") == true){
                    binding.baseCardview.setCardBackgroundColor(Color.parseColor("#3BF82F2F"))
                }else{
                    binding.baseCardview.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                }



//                val detail =
//                    String.format(binding.idfl.context.getString(R.string.txt_mat_code), this.assetNo)
//                binding.idfl.text = Html.fromHtml(detail)
//
//                val txtEquip =
//                    String.format(binding.txtEquip.context.getString(R.string.txt_sap_code), this.sapcode)
//                binding.txtEquip.text = Html.fromHtml(txtEquip)
//
//                val materialDesc =
//                    String.format(binding.idDescription.context.getString(R.string.txt_slop_code), this.materialDesc)
//                binding.idDescription.text = Html.fromHtml(materialDesc)
//
//                val oldTagNoDiscription =
//                    String.format(binding.oldTagNoDiscription.context.getString(R.string.txt_bin_code), this.storagebin)
//                binding.oldTagNoDiscription.text = Html.fromHtml(oldTagNoDiscription)

                binding.baseCardview.setOnClickListener {
                    val itemselected = mList[position]
                    itemselected.action = ""
                    listener.itemClicked(itemselected)
                }

                binding.conatner4.setOnClickListener {
                    val itemselected = mList[position]
                    itemselected.action = "Not Available"
                    listener.itemClicked(itemselected)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(data: ArrayList<PVData>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }
}
