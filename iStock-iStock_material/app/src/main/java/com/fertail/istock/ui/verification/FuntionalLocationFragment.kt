package com.fertail.istock.ui.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.FragmentFunctionalLocationBinding
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.GetAllMasterDataResponse
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.bottomsheet.FunctionalLocationChooseBottomsheet
import com.fertail.istock.view_model.GetAllMasterViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FuntionalLocationFragment : BaseFragment() {

    private var _binding: FragmentFunctionalLocationBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: GetAllMasterViewModel
    lateinit var data: GetAllMasterDataResponse


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFunctionalLocationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[GetAllMasterViewModel::class.java]

        observeViewModel()

        if (iStockApplication.appPreference.KEY_All_Master.equals("")) {
            viewModel.getAllMaster()
        } else {
            val gson = Gson()
            val myType = object : TypeToken<GetAllMasterDataResponse>() {}.type
            data = gson.fromJson<GetAllMasterDataResponse>(
                iStockApplication.appPreference.KEY_All_Master,
                myType
            )
            updateData()
        }




        return binding.root
    }

    private fun observeViewModel() {
        viewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
//                data = it
                updateData()
            }
        })
        viewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })

    }


    private fun updateData() {

        val tempBusiness =
            data.businesses.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.business)) }

        if (tempBusiness.size > 0) {
            binding.assetBusiness.setText(tempBusiness[0].businessName)
        }

        val assetMajorClass1 =
            data.majorClasses.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.majorClass)) }

        if (assetMajorClass1.size > 0) {
            binding.assetMajorClass.setText(assetMajorClass1[0].majorClass)
        }

        val assetMinorClass1 =
            data.minorClasses.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.minorClass)) }

        if (assetMinorClass1.size > 0) {
            binding.assetMinorClass.setText(assetMinorClass1[0].minorClass)
            binding.assetMinorClassFuntional.setText(assetMinorClass1[0].minorClass)
        }

        val tempAssetEqui =
            data.equipmentClasses.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.equipmentClass)) }

        if (tempAssetEqui.size > 0) {
            binding.assetEquipmentClassFunctional.setText(tempAssetEqui[0].equipmentClass)
        }

        val assetRegion1 =
            data.regions.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.region)) }

        if (assetRegion1.size > 0) {
            binding.assetRegion.setText(assetRegion1[0].region)
        }

        val assetArea1 =
            data.areas.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.area)) }

        if (assetArea1.size > 0) {
            binding.assetArea.setText(assetArea1[0].area)
        }

        val assetSubArea1 =
            data.subAreas.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.subArea)) }

        if (assetSubArea1.size > 0) {
            binding.assetSubArea.setText(assetSubArea1[0].subArea)
        }

        val assetSubSystemIdentifier1 =
            data.identifiers.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.identifier)) }

        if (assetSubSystemIdentifier1.size > 0) {
            binding.assetSubSystemIdentifier.setText(assetSubSystemIdentifier1[0].identifier)
        }

        val assetEquipmentType1 =
            data.equipmentTypes.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.equipmentType)) }

        if (assetEquipmentType1.size > 0) {
            binding.assetEquipmentType.setText(assetEquipmentType1[0].equipmentType)
        }

        if (!data.locations.isNullOrEmpty()) {
            val flocCode1 =
                data.locations.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.function)) }

            if (!flocCode1.isNullOrEmpty()) {
                binding.flocCode.setText(flocCode1[0].location)
            }
        }


        val assetCity1 =
            data.cities.filter { s -> (s.id.equals(VerificationDetailsActivity.mPVData.city)) }

        if (!assetCity1.isNullOrEmpty()) {
            binding.assetCity.setText(assetCity1[0].city)
        }

        binding.assetEquipmentType.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet = FunctionalLocationChooseBottomsheet(
                assetEquipmentType,
                data,
                object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.equipmentType = id
//                        VerificationDetailsActivity.mPVData.equipmentTypeCode = code
//                        VerificationDetailsActivity.mPVData.equipmentTypeID = id
                        binding.assetEquipmentType.setText(name)
                    }
                })
            modalBottomSheet.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)

        }

        binding.assetEquipmentClassFunctional.setOnClickListener {

            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet = FunctionalLocationChooseBottomsheet(
                assetEquipmentClassFunctional,
                data,
                object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.equipmentClass = id
//                        VerificationDetailsActivity.mPVData.equipmentClassCode = code
//                        VerificationDetailsActivity.mPVData.equipmentClassID = id
                        binding.assetEquipmentClassFunctional.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetBusiness.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetBusiness, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.business = id
//                        VerificationDetailsActivity.mPVData.businessCode = code
//                        VerificationDetailsActivity.mPVData.businessID = id
                        binding.assetBusiness.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetMajorClass.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetMajorClass, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.majorClass = id
//                        VerificationDetailsActivity.mPVData.majorCode = code
//                        VerificationDetailsActivity.mPVData.majorID = id
                        binding.assetMajorClass.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetMinorClassFuntional.setOnClickListener {
            binding.assetMinorClass.post {
                binding.assetMinorClass.performClick()
            }
        }

        binding.assetMinorClass.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetMinorClass, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.minorClass = id
//                        VerificationDetailsActivity.mPVData.minorClassCode = code
//                        VerificationDetailsActivity.mPVData.minorClassID = id
                        binding.assetMinorClass.setText(name)
                        binding.assetMinorClassFuntional.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetRegion.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetRegion, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.region = id
//                        VerificationDetailsActivity.mPVData.regionCode = code
//                        VerificationDetailsActivity.mPVData.regionID = id
                        binding.assetRegion.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetCity.setOnClickListener {

            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetCity, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.city = id
//                        VerificationDetailsActivity.mPVData.cityCode = code
//                        VerificationDetailsActivity.mPVData.cityID = id
                        binding.assetCity.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetArea.setOnClickListener {

            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetArea, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.area = id
//                        VerificationDetailsActivity.mPVData.areaCode = code
//                        VerificationDetailsActivity.mPVData.areaID = id
                        binding.assetArea.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.assetSubArea.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(assetSubArea, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.subArea = id
//                        VerificationDetailsActivity.mPVData.subAreaCode = code
//                        VerificationDetailsActivity.mPVData.subAreaID = id
                        binding.assetSubArea.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }


        binding.assetSubSystemIdentifier.setOnClickListener {
            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet = FunctionalLocationChooseBottomsheet(
                assetSubSystemIdentifier,
                data,
                object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.identifier = id
//                        VerificationDetailsActivity.mPVData.identifierCode = code
//                        VerificationDetailsActivity.mPVData.identifierID = id
                        binding.assetSubSystemIdentifier.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

        binding.flocCode.setOnClickListener {

            var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
            modalBottomSheet =
                FunctionalLocationChooseBottomsheet(flocCode, data, object : itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()
                        VerificationDetailsActivity.mPVData.functionalLoc = id
//                        VerificationDetailsActivity.mPVData.functionalLocCode = code
//                        VerificationDetailsActivity.mPVData.functionalLocID = id

                        binding.flocCode.setText(name)
                    }
                })
            modalBottomSheet!!.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }

    }

    interface itemSelected {
        fun selectedItem(id: String, code: String, name: String)
    }
}