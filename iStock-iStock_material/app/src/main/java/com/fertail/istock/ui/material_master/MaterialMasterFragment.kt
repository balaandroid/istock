package com.fertail.istock.ui.material_master

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fertail.istock.DashboardActivity
import com.fertail.istock.databinding.FragmentAssetMasterBinding
import com.fertail.istock.databinding.FragmentMaterialMasterBinding
import com.fertail.istock.ui.home.HomeViewModel

class MaterialMasterFragment : Fragment() {

    private var _binding: FragmentMaterialMasterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentMaterialMasterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initToolbor()
        return root
    }

    private fun initToolbor() {

        binding.toolbarMaterial?.btnMenu?.setOnClickListener {
            (activity as DashboardActivity?)?.openDrawer()
        }

        binding.toolbarMaterial?.btnLogout?.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}