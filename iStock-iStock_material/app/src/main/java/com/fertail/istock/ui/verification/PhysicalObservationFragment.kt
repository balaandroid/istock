package com.fertail.istock.ui.verification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.DashboardActivity
import com.fertail.istock.R
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.FragmentPhysicalObservationBinding
import com.fertail.istock.databinding.FragmentVerificationBinding
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.stock_help.ItemsHelpModel
import com.fertail.istock.ui.stock_help.StockHelpAdapter

class PhysicalObservationFragment  : BaseFragment() {
    private var _binding: FragmentPhysicalObservationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhysicalObservationBinding.inflate(inflater, container, false)
        initToolbor()

        binding.oldTagText.text = VerificationDetailsActivity.mPVData.uniqueId
        binding.ideqpno.text = VerificationDetailsActivity.mPVData.sAPEquipment?: NoData
        binding.idDescriptionContent.text = VerificationDetailsActivity.mPVData.equipmentDesc
        binding.idDescriptionContentEdt.setText(VerificationDetailsActivity.mPVData.equipmentDesc)
        binding.oldTagNoValue.text = VerificationDetailsActivity.mPVData.oldTagNo
        binding.idDescriptionContentEdt.addTextChangedListener(MyTextWatcher())

        if (VerificationDetailsActivity.mPVData.isNewOne) {
            binding.idDescriptionContentEdt.visibility = View.VISIBLE
            binding.idDescriptionContent.visibility = View.GONE
        }else{
            binding.idDescriptionContentEdt.visibility = View.GONE
            binding.idDescriptionContent.visibility = View.VISIBLE
        }

        initViewPager()
        return binding.root
    }


    private fun initViewPager() {

        binding.viewPager.adapter = PageAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager( binding.viewPager)


    }


    private fun initToolbor() {

        binding.toolbarPhysicalObservation.toolbarTitle.text = resources.getText(R.string.menu_physical_verification)


        binding.toolbarPhysicalObservation.btnMenu.visibility = View.GONE
        binding.toolbarPhysicalObservation.btnBack.visibility = View.VISIBLE
        binding.toolbarPhysicalObservation.btnLogout.visibility = View.GONE
        binding.toolbarPhysicalObservation.btnSearch.visibility = View.GONE

        binding.toolbarPhysicalObservation.btnBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.footer.idBack.visibility = View.GONE

        binding.footer.btnContinue.setOnClickListener {
            continueClicked()
        }

        binding.footer.idCancle.setOnClickListener {
            requireActivity().finish()
        }

        initCustomTabClick()
    }

    private fun continueClicked() {
        findNavController().navigate(R.id.action_nav_equipment_info)
    }

    fun initCustomTabClick() {

        binding.customTab.constrainPhysicalObservation.setOnClickListener {

        }

        binding.customTab.constrainEquipmentInfo.setOnClickListener {
            continueClicked()
        }

        binding.customTab.constrainPhotos.setOnClickListener {
            showAlert()
        }

        binding.customTab.constrainLocation.setOnClickListener {
            showAlert()
        }

        binding.customTab.constrainBom.setOnClickListener {
            showAlert()
        }
    }

    fun showAlert(){
        Toast.makeText(requireContext(), "Fill Asset Images first", Toast.LENGTH_SHORT).show()
    }


    class MyTextWatcher() : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            VerificationDetailsActivity.mPVData.equipmentDesc = p0.toString()
        }

    }
}