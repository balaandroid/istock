package com.fertail.istock.ui.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fertail.istock.DashboardActivity
import com.fertail.istock.databinding.FragmentAssetMasterBinding
import com.fertail.istock.databinding.FragmentDownloadBinding
import com.fertail.istock.databinding.FragmentHelpBinding
import com.fertail.istock.ui.home.HomeViewModel

class DownloadFragment : Fragment() {
    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        initToolbor()
        return binding.root
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