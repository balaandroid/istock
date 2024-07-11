package com.fertail.istock.ui.verification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BOMPageAdapter (fm: FragmentManager, var pageSize : Int) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return pageSize;
    }

    override fun getItem(position: Int): Fragment {
        return BOMInnerFragment(position)
    }

    fun updateAdapter(size : Int) {
        pageSize = size
        notifyDataSetChanged()
    }
    override fun getPageTitle(position: Int): CharSequence {
//        when(position) {
//            0 -> {
//                return "Functional Location"
//            }
//            1 -> {
//                return "Asset Attributes"
//            }
//            2 -> {
//                return "Asset Building"
//            }
//            3 -> {
//                return "BOM"
//            }
//        }
        return "BOM Inner"
    }

}