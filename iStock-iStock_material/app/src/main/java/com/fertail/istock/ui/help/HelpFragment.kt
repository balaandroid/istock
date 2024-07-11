package com.fertail.istock.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fertail.istock.DashboardActivity
import com.fertail.istock.databinding.FragmentAssetMasterBinding
import com.fertail.istock.databinding.FragmentHelpBinding
import com.fertail.istock.ui.home.HomeViewModel

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initToolbor()
        return root
    }

    private fun initToolbor() {

        binding.toolbarHelp?.btnMenu?.setOnClickListener {
            (activity as DashboardActivity?)?.openDrawer()
        }

        binding.toolbarHelp?.btnLogout?.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}