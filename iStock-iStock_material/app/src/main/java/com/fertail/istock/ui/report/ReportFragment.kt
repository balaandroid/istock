package com.fertail.istock.ui.report

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.fertail.istock.AppPreference
import com.fertail.istock.DashboardActivity
import com.fertail.istock.LoginActivity
import com.fertail.istock.R
import com.fertail.istock.databinding.FragmentReportBinding
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.util.CommonUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ReportFragment  : BaseFragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        initToolbor()
        initViewClicks()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.toolbarReport.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
            Toast.makeText(requireContext(),"You are online!", Toast.LENGTH_SHORT).show()
        } else {
            binding.toolbarReport.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
            Toast.makeText(requireContext(),"You are offline!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(requireContext())){
            binding.toolbarReport.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }else{
            binding.toolbarReport.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)

    }

    private fun initViewClicks() {
        binding.container1.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_report_list)
        }

        binding.container2.setOnClickListener {
            it.findNavController().navigate(R.id.nav_report_to_verification_latest)
        }

        binding.container3.setOnClickListener {
            it.findNavController().navigate(R.id.action_nav_download)
        }
    }

    private fun initToolbor() {
        binding.toolbarReport.toolbarTitle.text = resources.getText(R.string.menu_stock_report)
        binding.toolbarReport.btnMenu.visibility = View.GONE
        binding.toolbarReport.btnBack.visibility = View.VISIBLE
//        binding.toolbarReport.btnLogout.visibility = View.VISIBLE

        binding.toolbarReport.btnBack.setOnClickListener {
            (activity as DashboardActivity?)?.backClicked()
        }

        binding.toolbarReport.btnLogout.setOnClickListener {

            CommonUtils.showLogotPopup(requireContext())
        }

    }
}