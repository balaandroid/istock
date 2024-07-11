package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class NumberData(
    @field:SerializedName("number")
    var number: String? = null,

    @field:SerializedName("isSelected")
    var isSelected: Boolean = false,
)

