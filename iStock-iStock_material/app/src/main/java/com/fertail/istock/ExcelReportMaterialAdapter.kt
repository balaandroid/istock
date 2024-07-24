package com.fertail.istock

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.util.UriWithDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExcelReportMaterialAdapter(var context:Context, var list:MutableList<UriWithDate>, var onClick:(position:Int)->Unit):RecyclerView.Adapter<ExcelReportMaterialAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val heading = itemView.findViewById<TextView>(R.id.txt_heading)
        val download = itemView.findViewById<CardView>(R.id.base_cardview)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ExcelReportMaterialAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.excel_report_layout, parent, false)
        return ViewHolder(view)

    }


    override fun onBindViewHolder(holder: ExcelReportMaterialAdapter.ViewHolder, position: Int) {
        holder.apply {

            val data=list[position]
//            val currentDateTimeList = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            heading.text= "Material data ${data.date}"

            download.setOnClickListener {
                onClick(position)

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newItems: MutableList<UriWithDate>) {
        list.clear()
        list = newItems
        notifyDataSetChanged()
    }




}