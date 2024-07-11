package com.fertail.istock.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.Constant
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.BottomSheetFunctionalBinding
import com.fertail.istock.model.*
import com.fertail.istock.ui.verification.FuntionalLocationFragment
import com.fertail.istock.ui.verification.adapter.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_functional_location.*

class FunctionalLocationChooseBottomsheet(
    internal var type: String,
    var data: GetAllMasterDataResponse,
    var param: FuntionalLocationFragment.itemSelected
) : BottomSheetDialogFragment(), Constant {

    lateinit var buisnessAdapter: BuisnessAdapter
    lateinit var majorClassAdapter: MajorClassAdapter
    lateinit var mainorClassAdapter: MinorClassAdapter
    lateinit var regionAdapter: RegionAdapter
    lateinit var cityAdapter: CityAdapter
    lateinit var areaAdapter: AreaAdapter
    lateinit var subAreaAdapter: SubAreaAdapter
    lateinit var subSystemIdentifierAdapter: SubSystemIdentifierAdapter
    lateinit var flocCodeAdapter: FlocCodeAdapter
    lateinit var equipmentClasse: EquipmentClassAdapter
    lateinit var equipmentTypes : EquipmentTypeAdapter
    lateinit var physicalObsAdapter : PhysicalObsAdapter

    private var _binding: BottomSheetFunctionalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetFunctionalBinding.inflate(inflater, container, false)

        binding.progressbar.visibility = View.VISIBLE

        binding.idTitle.text = type
        initRecyclerView()
        return binding.root
    }


    private fun initRecyclerView() {

        if (type.equals(assetBusiness)) {
            binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            buisnessAdapter = BuisnessAdapter(data.businesses, param)
            binding.helpRecyclerView.adapter = buisnessAdapter
        }

        if (type.equals(assetMajorClass)) {
            if (!VerificationDetailsActivity.mPVData.business.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                majorClassAdapter = MajorClassAdapter(
                    getMajorArray(VerificationDetailsActivity.mPVData.business!!),
                    param
                )
                binding.helpRecyclerView.adapter = majorClassAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Please select correct business class",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (type.equals(assetMinorClass)) {

            if (!VerificationDetailsActivity.mPVData.majorClass.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                mainorClassAdapter = MinorClassAdapter(
                    getMinorArray(VerificationDetailsActivity.mPVData.majorClass!!),
                    param
                )
                binding.helpRecyclerView.adapter = mainorClassAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Please select correct major class",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (type.equals(assetRegion)) {
            binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            regionAdapter = RegionAdapter(data.regions, param)
            binding.helpRecyclerView.adapter = regionAdapter
        }


        if (type.equals(assetCity)) {

            if (!VerificationDetailsActivity.mPVData.region.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                cityAdapter =
                    CityAdapter(getCity(VerificationDetailsActivity.mPVData.region), param)
                binding.helpRecyclerView.adapter = cityAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please select correct region", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        if (type.equals(assetArea)) {

            if (!VerificationDetailsActivity.mPVData.city.isNullOrEmpty()) {

                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                areaAdapter =
                    AreaAdapter(getArea(VerificationDetailsActivity.mPVData.city), param)
                binding.helpRecyclerView.adapter = areaAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please select correct city", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        if (type.equals(assetSubArea)) {

            if (!VerificationDetailsActivity.mPVData.area.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                subAreaAdapter =
                    SubAreaAdapter(getSubArea(VerificationDetailsActivity.mPVData.area), param)
                binding.helpRecyclerView.adapter = subAreaAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please select correct area", Toast.LENGTH_SHORT)
                    .show()
            }

        }



        if (type.equals(assetSubSystemIdentifier)) {

            if (!VerificationDetailsActivity.mPVData.minorClass.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                subSystemIdentifierAdapter = SubSystemIdentifierAdapter(
                    getIdendifier(VerificationDetailsActivity.mPVData.minorClass),
                    param
                )
                binding.helpRecyclerView.adapter = subSystemIdentifierAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Please select correct minor class",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        if (type.equals(flocCode)) {

            if (!VerificationDetailsActivity.mPVData.area.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                flocCodeAdapter =
                    FlocCodeAdapter(getFlocCode(VerificationDetailsActivity.mPVData.area), param)
                binding.helpRecyclerView.adapter = flocCodeAdapter
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please select correct area", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        if (type.equals(assetEquipmentClassFunctional)) {
            binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            equipmentClasse = EquipmentClassAdapter(data.equipmentClasses, param)
            binding.helpRecyclerView.adapter = equipmentClasse
        }

        if (type.equals(assetEquipmentType)) {

            if (!VerificationDetailsActivity.mPVData.equipmentClass.isNullOrEmpty()) {
                binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                equipmentTypes =
                    EquipmentTypeAdapter(getEquipmentType(VerificationDetailsActivity.mPVData.equipmentClass), param)
                binding.helpRecyclerView.adapter = equipmentTypes
            } else {
                binding.idNoData.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please select Equipment Class", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (type.equals(PhysicalObs)) {
            binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            physicalObsAdapter = PhysicalObsAdapter(data.physicalObs, param)
            binding.helpRecyclerView.adapter = physicalObsAdapter
        }


        binding.progressbar.visibility = View.GONE
    }

    private fun getEquipmentType(equipmentClassID: String?): List<EquipmentTypes> {
        val tempData: ArrayList<EquipmentTypes> = ArrayList()
        data.equipmentTypes.forEach {
            if (equipmentClassID.equals(it.equClass_Id)) {
                tempData.add(it)
            }
        }
        return tempData
    }

    private fun getFlocCode(areaID: String?): List<LocationsItem> {
        val tempData: ArrayList<LocationsItem> = ArrayList()
        if (!data.locations.isNullOrEmpty()) {
            data.locations.forEach {
                if (areaID.equals(it.areaId)) {
                    tempData.add(it)
                }
            }
        }
        return tempData
    }

    private fun getIdendifier(minorClassID: String?): List<IdentifiersItem> {
        val tempData: ArrayList<IdentifiersItem> = ArrayList()
        data.identifiers.forEach {
            if (minorClassID.equals(it.fClassId)) {
                tempData.add(it)
            }
        }
        return tempData
    }

    private fun getSubArea(areaID: String?): List<SubAreasItem> {
        val tempData: ArrayList<SubAreasItem> = ArrayList()
        data.subAreas.forEach {
            if (areaID.equals(it.areaId)) {
                tempData.add(it)
            }
        }
        return tempData
    }

    private fun getArea(cityID: String?): List<AreasItem> {
        val tempData: ArrayList<AreasItem> = ArrayList()
        data.areas.forEach {
            if (cityID.equals(it.cityId)) {
                tempData.add(it)
            }
        }
        return tempData
    }

    private fun getCity(regionID: String?): List<CitiesItem> {
        val tempData: ArrayList<CitiesItem> = ArrayList()
        data.cities.forEach {
            if (regionID.equals(it.regionId)) {
                tempData.add(it)
            }
        }
        return tempData
    }

    private fun getMajorArray(businessID: String): List<MajorClassesItem> {
        val tempData: ArrayList<MajorClassesItem> = ArrayList()
        data.majorClasses.forEach {
            if (businessID.equals(it.businessId)) {
                tempData.add(it)
            }
        }

        return tempData
    }

    private fun getMinorArray(majorID: String): List<MinorClassesItem> {
        val tempData: ArrayList<MinorClassesItem> = ArrayList()
        data.minorClasses.forEach {
            if (majorID.equals(it.majorId)) {
                tempData.add(it)
            }
        }

        return tempData
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}