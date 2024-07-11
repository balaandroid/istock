package com.fertail.istock.ui.verification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.FragmentAsetBuildingBinding
import com.fertail.istock.databinding.FragmentEquipmentBinding
import com.fertail.istock.ui.BaseFragment

class AssetBuildingFragment : BaseFragment() {

    private var _binding: FragmentAsetBuildingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAsetBuildingBinding.inflate(inflater, container, false)

        binding.buildingID.setText( VerificationDetailsActivity.mPVData.assetBuilding.buildingId)
        binding.buildingHeight.setText( VerificationDetailsActivity.mPVData.assetBuilding.height)
        binding.buildingLength.setText( VerificationDetailsActivity.mPVData.assetBuilding.length)
        binding.buildingLocation.setText( VerificationDetailsActivity.mPVData.assetBuilding.location)
        binding.buildingWidth.setText( VerificationDetailsActivity.mPVData.assetBuilding.width)
        binding.buildingName.setText( VerificationDetailsActivity.mPVData.assetBuilding.buildingName)

        binding.buildingID.addTextChangedListener(MyTextWatcher("buildingID"))
        binding.buildingHeight.addTextChangedListener(MyTextWatcher("buildingHeight"))
        binding.buildingLength.addTextChangedListener(MyTextWatcher("buildingLength"))
        binding.buildingLocation.addTextChangedListener(MyTextWatcher("buildingLocation"))
        binding.buildingWidth.addTextChangedListener(MyTextWatcher("buildingWidth"))
        binding.buildingName.addTextChangedListener(MyTextWatcher("buildingName"))

        return binding.root
    }

    class MyTextWatcher (val type :String) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            if (type.equals("buildingID")){
                VerificationDetailsActivity.mPVData.assetBuilding.buildingId = p0.toString()
            }

            if (type.equals("buildingHeight")){
                VerificationDetailsActivity.mPVData.assetBuilding.height = p0.toString()
            }

            if (type.equals("buildingLength")){
                VerificationDetailsActivity.mPVData.assetBuilding.length = p0.toString()
            }

            if (type.equals("buildingLocation")){
                VerificationDetailsActivity.mPVData.assetBuilding.location = p0.toString()
            }
            if (type.equals("buildingWidth")){
                VerificationDetailsActivity.mPVData.assetBuilding.width = p0.toString()
            }
              if (type.equals("buildingName")){
                VerificationDetailsActivity.mPVData.assetBuilding.buildingName = p0.toString()
            }


        }

    }
}