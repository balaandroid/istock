package com.fertail.istock.ui.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fertail.istock.databinding.FragmentVerificationBinding
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.ReportClickListener

class VerficationNewFragment : BaseFragment(), ReportClickListener {

    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!

    private val data = ArrayList<PVData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun itemClicked(data: PVData) {
    }
}