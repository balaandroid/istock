package com.fertail.istock.ui.verification

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.*
import com.fertail.istock.databinding.FragmentBomBinding
import com.fertail.istock.model.BOMModel
import com.fertail.istock.model.NumberData
import com.fertail.istock.model.UserDetailsResponse
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.verification.adapter.NumberAdapter
import com.fertail.istock.util.CommonUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BOMFragment : BaseFragment(), itemClicked {

    private var _binding: FragmentBomBinding? = null
    private val binding get() = _binding!!

    val numberArray: ArrayList<NumberData> = ArrayList()
    var adapter: NumberAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBomBinding.inflate(inflater, container, false)

        initToolbor()
        initCustomTabClick()
        initViewPager()


        initRecyclerView()

        binding.itemClick.setOnClickListener {

            val bomModel = BOMModel()
            bomModel.uniqueId = System.currentTimeMillis().toString()

            if (resources.getBoolean(R.bool.isTablet)){
                bomModel.equipmentId = VerificationDetailsActivity.mPVData.uniqueId
                VerificationDetailsActivity.mPVData.bomModel.add(bomModel)
            }else{
                bomModel.equipmentId = VerificationDetailsMobileActivity.mPVData.uniqueId
                VerificationDetailsMobileActivity.mPVData.bomModel.add(bomModel)
            }

            initRecyclerView()
            bomPageAdapter.updateAdapter(VerificationDetailsActivity.mPVData.bomModel.size)
            binding.viewPager.currentItem = VerificationDetailsActivity.mPVData.bomModel.size - 1
        }

        binding.oldTagText.text = VerificationDetailsActivity.mPVData.uniqueId
        binding.ideqpno.text = VerificationDetailsActivity.mPVData.sAPEquipment ?: NoData
        binding.idDescriptionContent.text = VerificationDetailsActivity.mPVData.equipmentDesc
        binding.oldTagNoValue.text = VerificationDetailsActivity.mPVData.oldTagNo


        return binding.root
    }

    private fun initRecyclerView() {
        numberArray.clear()

        val lastPos = VerificationDetailsActivity.mPVData.bomModel.size - 1
        var itrater: Int = 0
        VerificationDetailsActivity.mPVData.bomModel.forEach {
            var data = NumberData()
            data.number = (itrater + 1).toString()
            data.isSelected = itrater == lastPos

            numberArray.add(data)
            itrater += 1
        }

        if (!numberArray.isNullOrEmpty()) {
            numberArray[numberArray.size - 1].let {
                it.isSelected = true
            }
        }

        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyBOM.layoutManager = horizontalLayoutManagaer
        adapter = NumberAdapter(numberArray, this, requireActivity())
        binding.rcyBOM.adapter = adapter

    }

    lateinit var bomPageAdapter: BOMPageAdapter

    private fun initViewPager() {

        if (VerificationDetailsActivity.mPVData.bomModel.size == 0) {

            val bomModel = BOMModel()
            bomModel.uniqueId = System.currentTimeMillis().toString()

            if (resources.getBoolean(R.bool.isTablet)){
                bomModel.equipmentId = VerificationDetailsActivity.mPVData.uniqueId
                VerificationDetailsActivity.mPVData.bomModel.add(bomModel)
            }else{
                bomModel.equipmentId = VerificationDetailsMobileActivity.mPVData.uniqueId
                VerificationDetailsMobileActivity.mPVData.bomModel.add(bomModel)
            }



//            VerificationDetailsActivity.mPVData.bomModel.add(BOMModel())
        }
        bomPageAdapter =
            BOMPageAdapter(childFragmentManager, VerificationDetailsActivity.mPVData.bomModel.size)

        binding.viewPager.adapter = bomPageAdapter
    }

    private fun initToolbor() {

        binding.toolbarLocation.toolbarTitle.text = resources.getText(R.string.location)
        binding.toolbarLocation.btnMenu.visibility = View.GONE
        binding.toolbarLocation.btnBack.visibility = View.VISIBLE
        binding.toolbarLocation.btnLogout.visibility = View.GONE
        binding.toolbarLocation.btnSearch.visibility = View.GONE

        binding.toolbarLocation.btnBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.footer.idBack.visibility = View.VISIBLE

        binding.footer.btnContinue.text = resources.getText(R.string.done)

        binding.footer.btnContinue.setOnClickListener {



            CommonUtils.showAlertWithCancel(
                requireContext(),
                "Item Saved successfully in local database",
                object : CommonInterface {
                    override fun btnPositiveSelected(dialog: DialogInterface) {

                        val gson = Gson()
                        val myType = object : TypeToken<UserDetailsResponse>() {}.type
                        val userDetailsResponse = gson.fromJson<UserDetailsResponse>(
                            iStockApplication.appPreference.KEY_USER_DETAILS,
                            myType
                        )


                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                        val currentDate = sdf.format(Date())
                        System.out.println(" C DATE is  " + currentDate)

                        VerificationDetailsActivity.mPVData.isSaved = true
                        VerificationDetailsActivity.mPVData.mVerificateionDate = currentDate
                        VerificationDetailsActivity.mPVData.mVerifiedBy = userDetailsResponse.userid

                        var i = 0
                        var id = -1
                        for (x in iStockApplication.pvDataModel.data) {

                            if (x.uniqueId.equals(VerificationDetailsActivity.mPVData.uniqueId)) {
                                id = i
                                break
                            }

                            i += 1
                        }

                        if (id != -1) {
                            iStockApplication.pvDataModel.data.removeAt(id)
                        }

                        iStockApplication.saveData(iStockApplication.pvDataModel.data)
                        iStockApplication.saveCompletedItem(VerificationDetailsActivity.mPVData)

                        dialog.dismiss()
                        requireActivity().finish()
                    }

                    override fun btnNegativeSelected(dialog: DialogInterface) {
                        dialog.dismiss()
                    }

                })


        }

        binding.footer.idCancle.setOnClickListener {
            requireActivity().finish()
        }

        binding.footer.idBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.customTab.imgTick1.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick2.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick3.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick4.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick5.setBackgroundResource(R.drawable.tick_orange_five_icon)


    }


    fun initCustomTabClick() {

        binding.customTab.constrainPhysicalObservation.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainEquipmentInfo.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainPhotos.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainLocation.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainBom.setOnClickListener {
//            it.findNavController().navigate(R.id.action_nav_bom)
        }
    }

    fun showAlert() {
        Toast.makeText(requireContext(), "Fill Asset Conditions first", Toast.LENGTH_SHORT).show()
    }

    override fun clickedItem(pos: Int) {
        binding.viewPager.currentItem = pos
    }


}

interface itemClicked {
    fun clickedItem(pos: Int)
}