package com.fertail.istock.ui.stock_help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.DashboardActivity
import com.fertail.istock.R
import com.fertail.istock.databinding.FragmentHelpBinding
import com.fertail.istock.databinding.FragmentStockHelpBinding
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.util.CommonUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StockHelpFragment : BaseFragment() {
    private var _binding: FragmentStockHelpBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockHelpBinding.inflate(inflater, container, false)
        initToolbor()
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {

       binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val data = ArrayList<ItemsHelpModel>()
        for (i in 1..4) {
            data.add(ItemsHelpModel("test", "Item " + i))
        }
        val adapter = StockHelpAdapter(data)
        binding.helpRecyclerView.adapter = adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.toolbarStockHelp.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
            Toast.makeText(requireContext(),"You are online!", Toast.LENGTH_SHORT).show()
        } else {
            binding.toolbarStockHelp.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
            Toast.makeText(requireContext(),"You are offline!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(requireContext())){
            binding.toolbarStockHelp.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }else{
            binding.toolbarStockHelp.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        _binding = null
    }

    private fun initToolbor() {

        binding.toolbarStockHelp.toolbarTitle.text = resources.getText(R.string.menu_asked_questions)
        binding.toolbarStockHelp.btnSearch.visibility = View.VISIBLE
        binding.toolbarStockHelp.btnBack.visibility = View.VISIBLE
        binding.toolbarStockHelp.btnMenu.visibility = View.GONE
        binding.toolbarStockHelp.btnLogout.visibility = View.GONE

        binding.toolbarStockHelp.btnBack.setOnClickListener {
            (activity as DashboardActivity?)?.backClicked()
        }

        binding.toolbarStockHelp.btnSearch.setOnClickListener {

        }
    }
}