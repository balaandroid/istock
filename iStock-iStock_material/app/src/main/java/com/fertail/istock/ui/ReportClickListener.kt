package com.fertail.istock.ui

import com.fertail.istock.model.PVData

interface ReportClickListener {
    fun itemClicked(data : PVData)
}