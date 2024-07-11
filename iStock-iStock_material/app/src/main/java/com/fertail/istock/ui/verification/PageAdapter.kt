package com.fertail.istock.ui.verification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return FuntionalLocationFragment()
            }
            1 -> {
                return AssetAttributesFragment()
            }
            2 -> {
                return AssetBuildingFragment()
            }
            3 -> {
                return BOMFragment()
            }
            else -> {
                return FuntionalLocationFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Functional Location"
            }
            1 -> {
                return "Asset Attributes"
            }
            2 -> {
                return "Asset Building"
            }
            3 -> {
                return "BOM"
            }
        }
        return super.getPageTitle(position)
    }

}