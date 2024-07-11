package com.fertail.istock.ui.asset_master

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fertail.istock.DashboardActivity
import com.fertail.istock.databinding.FragmentAssetMasterBinding
import com.fertail.istock.databinding.FragmentHomeBinding
import com.fertail.istock.ui.home.HomeViewModel

class AssetMasterFragment : Fragment()  {

    private var _binding: FragmentAssetMasterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentAssetMasterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initToolbor()
        return root
    }

    private fun initToolbor() {

        binding.toolbarAsset?.btnMenu?.setOnClickListener {
            (activity as DashboardActivity?)?.openDrawer()
        }

        binding.toolbarAsset?.btnLogout?.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}