package com.fertail.istock.ui.verification

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog.show
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.fertail.istock.BuildConfig
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.FragmentEquipmentBinding
import com.fertail.istock.ui.BaseFragment
import java.io.File

class AssetAttributesFragment : BaseFragment() {

    private var _binding: FragmentEquipmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEquipmentBinding.inflate(inflater, container, false)

        updateData()
        return binding.root
    }

    private fun updateData() {
        binding.assetType.setText(VerificationDetailsActivity.mPVData.equipmentType)
        binding.assetMake.setText(VerificationDetailsActivity.mPVData.make)
        binding.assetModelNo.setText(VerificationDetailsActivity.mPVData.modelNo)
        binding.assetSerialNo.setText(VerificationDetailsActivity.mPVData.serialNo)
        binding.assetQuantity.setText(VerificationDetailsActivity.mPVData.quantity)
        binding.assetYearInstallation.setText(VerificationDetailsActivity.mPVData.yearOfInstall)



        binding.assetUnitOfMeasure.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetPiping.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetDisplacement.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetAccuracy.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetSize.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetRating.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetConfiguration.setText(VerificationDetailsActivity.mPVData.siteName)
        binding.assetCapacity.setText(VerificationDetailsActivity.mPVData.siteName) // need to know

    }


}