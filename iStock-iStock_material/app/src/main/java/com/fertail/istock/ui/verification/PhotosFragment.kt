package com.fertail.istock.ui.verification

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.BuildConfig
import com.fertail.istock.R
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.FragmentPhotosBinding
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.ReportClickListener
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.view_model.FileUploadViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class PhotosFragment : BaseFragment(), ReportClickListener, actionSelected {
    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    var mAction: String = ""
    var cameraorgallery: String = ""
    private var uri: Uri? = null
    lateinit var fileUploadViewModel: FileUploadViewModel

    private val activityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                var data = ImageClass()
                if (cameraorgallery.equals(GALLERYTEXT)) {
                    data.uri = it.data!!.data
                } else {
                    data.uri = uri
                }

                getContactBitmapFromURI(iStockApplication.appController!!, data.uri)?.let { it1 ->
                    saveBitmapIntoSDCardImage(
                        iStockApplication.appController!!,
                        it1
                    ).let {

                        if (it != null) {
                            lifecycleScope.launch{
                                Compressor.compress(requireContext(), it).let{
                                    if(NetworkUtils.isConnected(requireContext())) {
                                        fileUploadViewModel.uploadFile(it.name, it, requireContext())
                                    }else {
                                        when (mAction) {
                                            captureCorrosion -> {
                                                VerificationDetailsActivity.mPVData.assetCondition.corrosionImage.add(it!!.absolutePath)
                                                loadCaptureCorrosion()
                                            }

                                            captureDamage -> {
                                                VerificationDetailsActivity.mPVData.assetCondition.damageImage.add(it!!.absolutePath)
                                                loadCaptureDamage()
                                            }

                                            captureLeakage -> {
                                                VerificationDetailsActivity.mPVData.assetCondition.leakageImage.add(it!!.absolutePath)
                                                loadCaptureLeakage()
                                            }

                                            captureTemprature -> {
                                                VerificationDetailsActivity.mPVData.assetCondition.temparatureImage.add(it!!.absolutePath)
                                                loadTemperatureList()
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }



            }

        }

    fun loadCaptureCorrosion() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyCorrosion.layoutManager = horizontalLayoutManagaer
        binding.rcyCorrosion.adapter = CustomAdapter(VerificationDetailsActivity.mPVData.assetCondition.corrosionImage, requireActivity())
    }

    fun loadTemperatureList() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyTemparature.layoutManager = horizontalLayoutManagaer
        binding.rcyTemparature.adapter = CustomAdapter(VerificationDetailsActivity.mPVData.assetCondition.temparatureImage, requireActivity())
    }

    fun loadCaptureLeakage() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyLeakage.layoutManager = horizontalLayoutManagaer
        binding.rcyLeakage.adapter = CustomAdapter(VerificationDetailsActivity.mPVData.assetCondition.leakageImage, requireActivity())
    }

    fun loadCaptureDamage() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyDamage.layoutManager = horizontalLayoutManagaer
        binding.rcyDamage.adapter = CustomAdapter(VerificationDetailsActivity.mPVData.assetCondition.damageImage, requireActivity())
    }

    var mFunctional = ObservableBoolean(true)
    var mNonnFunctional = ObservableBoolean(false)
    var mStandBy = ObservableBoolean(false)

    var mCorrosionHigh = ObservableBoolean(false)
    var mCorrosionMedium = ObservableBoolean(false)
    var mCorrosionNo = ObservableBoolean(true)

    var mDamageHigh = ObservableBoolean(false)
    var mDamageMedium = ObservableBoolean(false)
    var mDamageNo = ObservableBoolean(true)

    var mLeakageHigh = ObservableBoolean(false)
    var mLeakageMedium = ObservableBoolean(false)
    var mLeakageNo = ObservableBoolean(true)

    var mVibrationHigh = ObservableBoolean(false)
    var mVibrationMedium = ObservableBoolean(false)
    var mVibrationNo = ObservableBoolean(true)

    var mTemperatureHigh = ObservableBoolean(false)
    var mTemperatureMedium = ObservableBoolean(false)
    var mTemperatureNo = ObservableBoolean(true)


    var mSmellHigh = ObservableBoolean(false)
    var mSmellMedium = ObservableBoolean(false)
    var mSmellNo = ObservableBoolean(true)

    var mNoiseHigh = ObservableBoolean(false)
    var mNoiseMedium = ObservableBoolean(false)
    var mNoiseNo = ObservableBoolean(true)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photos, container, false)

        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]

        observeViewModel()
        updatedata()

        initToolbor()
        initRecyclerView()
        initViewClicks()

        initCustomTabClick()

        binding.remark.addTextChangedListener(MyTextWatcher("lat1"))

        binding.oldTagText.text = VerificationDetailsActivity.mPVData.uniqueId
        binding.ideqpno.text = VerificationDetailsActivity.mPVData.sAPEquipment?: NoData
        binding.idDescriptionContent.text = VerificationDetailsActivity.mPVData.equipmentDesc
        binding.oldTagNoValue.text = VerificationDetailsActivity.mPVData.oldTagNo
        binding.remark.setText(VerificationDetailsActivity.mPVData.remarks)

        binding.viewModel = this
        return binding.root
    }

    private fun updatedata() {

        if (VerificationDetailsActivity.mPVData.assetCondition?.corrosion.equals("High")) {
            mCorrosionHigh.set(true)
            mCorrosionMedium.set(false)
            mCorrosionNo.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.corrosion.equals("Medium")) {
            mCorrosionHigh.set(false)
            mCorrosionMedium.set(true)
            mCorrosionNo.set(false)
        } else {
            mCorrosionHigh.set(false)
            mCorrosionMedium.set(false)
            mCorrosionNo.set(true)
        }

        if (VerificationDetailsActivity.mPVData.assetCondition?.damage.equals("High")) {
            mDamageHigh.set(true)
            mDamageMedium.set(false)
            mDamageNo.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.damage.equals("Medium")) {
            mDamageHigh.set(false)
            mDamageMedium.set(true)
            mDamageNo.set(false)
        } else {
            mDamageHigh.set(false)
            mDamageMedium.set(false)
            mDamageNo.set(true)
        }

        if (VerificationDetailsActivity.mPVData.assetCondition?.leakage.equals("High")) {
            mLeakageHigh.set(true)
            mLeakageMedium.set(false)
            mLeakageNo.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.leakage.equals("Medium")) {
            mLeakageHigh.set(false)
            mLeakageMedium.set(true)
            mLeakageNo.set(false)
        } else {
            mLeakageHigh.set(false)
            mLeakageMedium.set(false)
            mLeakageNo.set(true)
        }


        if (VerificationDetailsActivity.mPVData.assetCondition?.vibration.equals("High")) {
            mVibrationHigh.set(true)
            mVibrationMedium.set(false)
            mVibrationNo.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.vibration.equals("Medium")) {
            mLeakageHigh.set(false)
            mVibrationMedium.set(true)
            mVibrationNo.set(false)
        } else {
            mLeakageHigh.set(false)
            mVibrationMedium.set(false)
            mVibrationNo.set(true)
        }



        if (VerificationDetailsActivity.mPVData.assetCondition?.temparature.equals("High")) {
            mTemperatureHigh.set(true)
            mTemperatureMedium.set(false)
            mTemperatureNo.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.temparature.equals("Medium")) {
            mTemperatureHigh.set(false)
            mTemperatureMedium.set(true)
            mTemperatureNo.set(false)
        } else {
            mTemperatureHigh.set(false)
            mTemperatureMedium.set(false)
            mTemperatureNo.set(true)
        }


        if (VerificationDetailsActivity.mPVData.assetCondition?.smell.equals("High")) {
            mSmellHigh.set(true)
            mSmellMedium.set(false)
            mSmellNo.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.smell.equals("Medium")) {
            mSmellHigh.set(false)
            mSmellMedium.set(true)
            mSmellNo.set(false)
        } else {
            mSmellHigh.set(false)
            mSmellMedium.set(false)
            mSmellNo.set(true)
        }

        if (VerificationDetailsActivity.mPVData.assetCondition?.assetCondition.equals("Functional")) {
            mNonnFunctional.set(false)
            mFunctional.set(true)
            mStandBy.set(false)
        } else if (VerificationDetailsActivity.mPVData.assetCondition?.assetCondition.equals(
                "standby",
                true
            )
        ) {
            mNonnFunctional.set(false)
            mFunctional.set(false)
            mStandBy.set(true)
        } else {
            mNonnFunctional.set(true)
            mFunctional.set(false)
            mStandBy.set(false)
        }

        if (VerificationDetailsActivity.mPVData.assetCondition.rank.isNullOrEmpty()) {
            calculateRank()
        }else{
            binding.rank.setText(VerificationDetailsActivity.mPVData.assetCondition?.rank)
        }

    }


    private fun initRecyclerView() {
        loadCaptureCorrosion()
        loadTemperatureList()
        loadCaptureLeakage()
        loadCaptureDamage()
    }

    private fun initViewClicks() {

        binding.captureCorrosion.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(captureCorrosion, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.captureDamage.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(captureDamage, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.captureLeakage.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(captureLeakage, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }
        binding.captureTemprature.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(captureTemprature, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.standby.setOnClickListener {
            mNonnFunctional.set(false)
            mFunctional.set(false)
            mStandBy.set(true)
        }

        binding.functional.setOnClickListener {
            mNonnFunctional.set(false)
            mFunctional.set(true)
            mStandBy.set(false)
        }

        binding.nonFunction.setOnClickListener {
            mNonnFunctional.set(true)
            mFunctional.set(false)
            mStandBy.set(false)
        }

        binding.corrosionHigh.setOnClickListener {
            mCorrosionHigh.set(true)
            mCorrosionMedium.set(false)
            mCorrosionNo.set(false)

            calculateRank()
        }

        binding.corrosionMedium.setOnClickListener {
            mCorrosionHigh.set(false)
            mCorrosionMedium.set(true)
            mCorrosionNo.set(false)

            calculateRank()
        }

        binding.corrosionNo.setOnClickListener {
            mCorrosionHigh.set(false)
            mCorrosionMedium.set(false)
            mCorrosionNo.set(true)

            calculateRank()
        }

        binding.damageHigh.setOnClickListener {
            mDamageHigh.set(true)
            mDamageMedium.set(false)
            mDamageNo.set(false)

            calculateRank()
        }

        binding.damageMedium.setOnClickListener {
            mDamageHigh.set(false)
            mDamageMedium.set(true)
            mDamageNo.set(false)

            calculateRank()
        }

        binding.damageNo.setOnClickListener {
            mDamageHigh.set(false)
            mDamageMedium.set(false)
            mDamageNo.set(true)
            calculateRank()
        }

        binding.leakageHigh.setOnClickListener {
            mLeakageHigh.set(true)
            mLeakageMedium.set(false)
            mLeakageNo.set(false)

            calculateRank()
        }

        binding.leakageMedium.setOnClickListener {

            mLeakageHigh.set(false)
            mLeakageMedium.set(true)
            mLeakageNo.set(false)
            calculateRank()

        }

        binding.leakageNo.setOnClickListener {
            mLeakageHigh.set(false)
            mLeakageMedium.set(false)
            mLeakageNo.set(true)

            calculateRank()
        }

        binding.vibrationHigh.setOnClickListener {

            mVibrationHigh.set(true)
            mVibrationMedium.set(false)
            mVibrationNo.set(false)

            calculateRank()
        }

        binding.vibrationMedium.setOnClickListener {
            mVibrationHigh.set(false)
            mVibrationMedium.set(true)
            mVibrationNo.set(false)

            calculateRank()
        }

        binding.vibrationNo.setOnClickListener {
            mVibrationHigh.set(false)
            mVibrationMedium.set(false)
            mVibrationNo.set(true)

            calculateRank()
        }

        binding.temperatureHigh.setOnClickListener {
            mTemperatureHigh.set(true)
            mTemperatureMedium.set(false)
            mTemperatureNo.set(false)

            calculateRank()
        }

        binding.temperatureMedium.setOnClickListener {
            mTemperatureHigh.set(false)
            mTemperatureMedium.set(true)
            mTemperatureNo.set(false)
            calculateRank()
        }

        binding.temperatureNo.setOnClickListener {
            mTemperatureHigh.set(false)
            mTemperatureMedium.set(false)
            mTemperatureNo.set(true)
            calculateRank()
        }

        binding.smellHigh.setOnClickListener {
            mSmellHigh.set(true)
            mSmellMedium.set(false)
            mSmellNo.set(false)

            calculateRank()
        }
        binding.smellMedium.setOnClickListener {
            mSmellHigh.set(false)
            mSmellMedium.set(true)
            mSmellNo.set(false)

            calculateRank()
        }
        binding.smellNo.setOnClickListener {
            mSmellHigh.set(false)
            mSmellMedium.set(false)
            mSmellNo.set(true)

            calculateRank()
        }

        binding.noiseHigh.setOnClickListener {
            mNoiseHigh.set(true)
            mNoiseMedium.set(false)
            mNoiseNo.set(false)

            calculateRank()
        }
        binding.noiseMedium.setOnClickListener {
            mNoiseHigh.set(false)
            mNoiseMedium.set(true)
            mNoiseNo.set(false)

            calculateRank()
        }
        binding.noiseNo.setOnClickListener {
            mNoiseHigh.set(false)
            mNoiseMedium.set(false)
            mNoiseNo.set(true)

            calculateRank()
        }
    }

    private fun calculateRank() {

        if (mCorrosionHigh.get() || mDamageHigh.get()
            || mLeakageHigh.get() || mVibrationHigh.get()
            || mTemperatureHigh.get() || mSmellHigh.get() || mNoiseHigh.get()
        ) {
            binding.rank.text = "5"
            setData()
            return
        }

        if (mDamageNo.get() && mCorrosionNo.get()
            && mLeakageNo.get() && mVibrationNo.get()
            && mTemperatureNo.get() && mSmellNo.get() && mNoiseNo.get()
        ) {
            binding.rank.text = "1"
            setData()
            return
        }

        var i = 0

        if (mCorrosionMedium.get()) {
            i += 1
        }

        if (mDamageMedium.get()) {
            i += 1
        }

        if (mLeakageMedium.get()) {
            i += 1
        }
        if (mVibrationMedium.get()) {
            i += 1
        }

        if (mTemperatureMedium.get()) {
            i += 1
        }
        if (mSmellMedium.get()) {
            i += 1
        }
        if (mNoiseMedium.get()) {
            i += 1
        }

        if (i == 1) {
            binding.rank.text = "2"
        }else if (i in 2..3) {
            binding.rank.text = "3"
        }else if(i > 3) {
            binding.rank.text = "4"
        }

        setData()
    }


    private fun initToolbor() {

        binding.toolbarPhoto.toolbarTitle.text = resources.getText(R.string.photos)
        binding.toolbarPhoto.btnMenu.visibility = View.GONE
        binding.toolbarPhoto.btnBack.visibility = View.VISIBLE
        binding.toolbarPhoto.btnLogout.visibility = View.GONE
        binding.toolbarPhoto.btnSearch.visibility = View.GONE

        binding.toolbarPhoto.btnBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.footer.idBack.visibility = View.VISIBLE

        binding.footer.btnContinue.setOnClickListener {
            continueClicked()
        }

        binding.footer.idCancle.setOnClickListener {
            requireActivity().finish()
        }

        binding.footer.idBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.customTab.imgTick1.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick2.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick3.setBackgroundResource(R.drawable.tick_orange_three_icon)


    }

    private fun continueClicked() {

        if ((mCorrosionHigh.get() || mCorrosionMedium.get()) &&  VerificationDetailsActivity.mPVData.assetCondition.corrosionImage.size == 0) {
            CommonUtils.showAlert(requireContext(),
                "Please add at least one Corrosion image")
            return
        }

        if ((mTemperatureHigh.get() || mTemperatureMedium.get()) && VerificationDetailsActivity.mPVData.assetCondition.temparatureImage.size == 0) {

            CommonUtils.showAlert(requireContext(),
                "Please add at least one temperature image")

            return
        }


        if ((mDamageMedium.get() || mDamageHigh.get()) && VerificationDetailsActivity.mPVData.assetCondition.damageImage.size == 0) {
            CommonUtils.showAlert(requireContext(),
                "Please add at least one damage image")
            return
        }


        if ((mLeakageHigh.get() || mLeakageMedium.get()) && VerificationDetailsActivity.mPVData.assetCondition.leakageImage.size == 0) {

            CommonUtils.showAlert(requireContext(),
                "Please add at least one leakage image")

            return
        }


        setData()

        findNavController().navigate(R.id.action_nav_location)

    }

    private fun setData() {

        if (mCorrosionHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.corrosion = "High"
        } else if (mCorrosionMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.corrosion = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition?.corrosion = "No"
        }

        if (mLeakageHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.leakage = "High"
        } else if (mLeakageMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.leakage = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition?.leakage = "No"
        }


        if (mDamageHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.damage = "High"
        } else if (mDamageMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.leakage = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition?.leakage = "No"
        }

        if (mNoiseHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.noise = "High"
        } else if (mNoiseMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.noise = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition?.noise = "No"
        }


        if (mVibrationHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.vibration = "High"
        } else if (mVibrationMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.vibration = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition?.vibration = "No"
        }

        if (mSmellHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.smell = "High"
        } else if (mSmellMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition?.smell = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition?.smell = "No"
        }

        if (mTemperatureHigh.get()) {
            VerificationDetailsActivity.mPVData.assetCondition.temparature = "High"
        } else if (mTemperatureMedium.get()) {
            VerificationDetailsActivity.mPVData.assetCondition.temparature = "Medium"
        } else {
            VerificationDetailsActivity.mPVData.assetCondition.temparature = "No"
        }

        if (mFunctional.get()) {
            VerificationDetailsActivity.mPVData.assetCondition.assetCondition = "Functional"
        }else if (mStandBy.get()) {
            VerificationDetailsActivity.mPVData.assetCondition.assetCondition = "standby"
        }else{
            VerificationDetailsActivity.mPVData.assetCondition.assetCondition = "Non Functional"
        }

        VerificationDetailsActivity.mPVData.assetCondition.rank = binding.rank.text.toString().trim()

    }

    override fun itemClicked(data: PVData) {
    }

    override fun optionChosen(action: String, from: String) {

        if (action.equals(CAMERATEXT)) {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        capturePhoto()
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.CAMERA
                )
                .check()
        }


        if (action.equals(GALLERYTEXT)) {
            openGallery()
        }

        mAction = from
        cameraorgallery = action

    }


    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        activityLauncher.launch(intent)
    }

    private fun capturePhoto() {

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        val capturedImage = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "iStock_$ts.jpg"
        )
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        uri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                requireContext(), BuildConfig.APPLICATION_ID + ".provider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        activityLauncher.launch(intent)
//        startActivityForResult(intent, CAPTURE_PHOTO)
    }


    fun initCustomTabClick() {

        binding.customTab.constrainPhysicalObservation.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainEquipmentInfo.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainPhotos.setOnClickListener {
//            requireActivity().onBackPressed()
        }

        binding.customTab.constrainLocation.setOnClickListener {
            continueClicked()
        }

        binding.customTab.constrainBom.setOnClickListener {
            showAlert()
        }
    }

    fun showAlert() {
        Toast.makeText(requireContext(), "Fill GIS Screen first", Toast.LENGTH_SHORT).show()
    }



    fun getContactBitmapFromURI(context: Context, uri: Uri?): Bitmap? {
        try {
            val input = context.contentResolver.openInputStream(uri!!) ?: return null
            return BitmapFactory.decodeStream(input)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

    fun saveBitmapIntoSDCardImage(context: Context?, finalBitmap: Bitmap): File? {

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        val cw = ContextWrapper(iStockApplication.appController)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val myDir = File(directory,"iStock_images" )

        myDir.mkdirs()
        val fname = "iStock_$ts" + ".png"
        val file = File(myDir, fname)

        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun observeViewModel() {
        fileUploadViewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                if (it.isNotEmpty()) {
                    when (mAction) {
                        captureCorrosion -> {
                            VerificationDetailsActivity.mPVData.assetCondition.corrosionImage.add(it)
                            loadCaptureCorrosion()
                        }

                        captureDamage -> {
                            VerificationDetailsActivity.mPVData.assetCondition.damageImage.add(it)
                            loadCaptureDamage()
                        }

                        captureLeakage -> {
                            VerificationDetailsActivity.mPVData.assetCondition.leakageImage.add(it)
                            loadCaptureLeakage()
                        }

                        captureTemprature -> {
                            VerificationDetailsActivity.mPVData.assetCondition.temparatureImage.add(it)
                            loadTemperatureList()
                        }
                    }
                    fileUploadViewModel.showSuccess.postValue("")
                }

            }
        })
        fileUploadViewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
        })
        fileUploadViewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) { View.VISIBLE }else { View.GONE }
            }
        })
    }

    class MyTextWatcher(val type: String) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            VerificationDetailsActivity.mPVData.remarks = p0.toString()
        }

    }


}