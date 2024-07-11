package com.fertail.istock.util

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseViewHolder <T> internal constructor(private val binding: ViewBinding, private val experssion:(T, ViewBinding)->Unit)
: RecyclerView.ViewHolder(binding.root){
    fun bind(item:T){
        experssion(item,binding)
    }
}