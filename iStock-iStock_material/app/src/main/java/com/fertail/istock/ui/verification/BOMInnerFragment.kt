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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.*
import com.fertail.istock.databinding.FragmentBomBinding
import com.fertail.istock.databinding.FragmntBomInnerBinding
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.ScannerActivity
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.view_model.FileUploadViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class BOMInnerFragment(val position: Int) : BaseFragment(), actionSelected {

    private var _binding: FragmntBomInnerBinding? = null
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
                            lifecycleScope.launch {
                                Compressor.compress(requireContext(), it).let {
                                    if (NetworkUtils.isConnected(requireContext())) {
                                        fileUploadViewModel.uploadFile(
                                            it.name,
                                            it,
                                            requireContext()
                                        )

                                        if (mAction.equals(addnameplateImage)) {
                                            recognizeText(
                                                InputImage.fromFilePath(
                                                    requireContext(),
                                                    data.uri!!
                                                )
                                            )
                                        }

                                    } else {
                                        when (mAction) {
                                            addBOMImage -> {

                                                if (resources.getBoolean(R.bool.isTablet)) {
                                                    VerificationDetailsActivity.mPVData.bomModel[position].bOMImg =
                                                        it!!.absolutePath
                                                } else {
                                                    VerificationDetailsMobileActivity.mPVData.bomModel[position].bOMImg =
                                                        it!!.absolutePath
                                                }


                                                loadBOMImage()
                                            }

                                            addOldTagImage -> {

                                                if (resources.getBoolean(R.bool.isTablet)) {
                                                    VerificationDetailsActivity.mPVData.bomModel[position].oldTagImg =
                                                        it!!.absolutePath
                                                } else {
                                                    VerificationDetailsMobileActivity.mPVData.bomModel[position].oldTagImg =
                                                        it!!.absolutePath
                                                }

                                                loadOldTagImage()
                                            }

                                            addNewBarcodeImage -> {
                                                if (resources.getBoolean(R.bool.isTablet)) {
                                                    VerificationDetailsActivity.mPVData.bomModel[position].barCodeImg =
                                                        it!!.absolutePath
                                                } else {
                                                    VerificationDetailsMobileActivity.mPVData.bomModel[position].barCodeImg =
                                                        it!!.absolutePath
                                                }


                                                loadNewBarCodeImage()
                                            }

                                            addnameplateImage -> {

                                                if (resources.getBoolean(R.bool.isTablet)) {
                                                    VerificationDetailsActivity.mPVData.bomModel[position].namePlateImg =
                                                        it!!.absolutePath
                                                } else {
                                                    VerificationDetailsMobileActivity.mPVData.bomModel[position].namePlateImg =
                                                        it!!.absolutePath
                                                }
                                                loadnamePlateImage()
                                            }
                                            else -> {

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

    private fun loadBOMImage() {

        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyBOM.layoutManager = horizontalLayoutManagaer
        val tempArray: ArrayList<String> = ArrayList()

        if (resources.getBoolean(R.bool.isTablet)) {
            VerificationDetailsActivity.mPVData.bomModel[position].bOMImg?.let { tempArray.add(it) }
        } else {
            VerificationDetailsMobileActivity.mPVData.bomModel[position].bOMImg?.let {
                tempArray.add(
                    it
                )
            }
        }

        binding.rcyBOM.adapter = CustomAdapter(tempArray, requireActivity())
    }

    private fun loadnamePlateImage() {

        val tempArray: ArrayList<String> = ArrayList()

        if (resources.getBoolean(R.bool.isTablet)) {
            VerificationDetailsActivity.mPVData.bomModel[position].namePlateImg?.let {
                tempArray.add(it)
            }
        } else {
            VerificationDetailsMobileActivity.mPVData.bomModel[position].namePlateImg?.let {
                tempArray.add(it)
            }
        }

        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyNamePlate.layoutManager = horizontalLayoutManagaer
        binding.rcyNamePlate.adapter = CustomAdapter(tempArray, requireActivity())
    }

    private fun loadOldTagImage() {

        val tempArray: ArrayList<String> = ArrayList()

        if (resources.getBoolean(R.bool.isTablet)) {
            VerificationDetailsActivity.mPVData.bomModel[position].oldTagImg?.let {
                tempArray.add(it)
            }
        } else {
            VerificationDetailsMobileActivity.mPVData.bomModel[position].oldTagImg?.let {
                tempArray.add(it)
            }
        }

        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyOldTag.layoutManager = horizontalLayoutManagaer
        binding.rcyOldTag.adapter = CustomAdapter(tempArray, requireActivity())

    }

    private fun loadNewBarCodeImage() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        val tempArray: ArrayList<String> = ArrayList()

        if (resources.getBoolean(R.bool.isTablet)) {
            VerificationDetailsActivity.mPVData.bomModel[position].barCodeImg?.let {
                tempArray.add(it)
            }
        } else {
            VerificationDetailsMobileActivity.mPVData.bomModel[position].barCodeImg?.let {
                tempArray.add(it)
            }
        }

        binding.rcyAssets.layoutManager = horizontalLayoutManagaer
        binding.rcyAssets.adapter = CustomAdapter(tempArray, requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmntBomInnerBinding.inflate(inflater, container, false)
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]

        updateData()
        initViewClicks()
        loadBOMImage()
        loadNewBarCodeImage()
        loadOldTagImage()
        loadnamePlateImage()
        observeViewModel()

        return binding.root
    }

    private fun updateData() {

        val temData = if (resources.getBoolean(R.bool.isTablet)) {
            VerificationDetailsActivity.mPVData.bomModel[position]
        } else {
            VerificationDetailsMobileActivity.mPVData.bomModel[position]
        }
        binding.edtNewTagNo.setText(temData.tagNo)
        binding.bomuniqueValuetwoTv.setText(temData.bOMName)
        binding.qtyValue.setText(temData.qty)
        binding.uomValue.setText(temData.uOM)
        binding.namePlateThreeTvValue.setText(temData.namePlateText)

        binding.oldTagNoValue.setText(temData.oldTag)
        binding.newTagText.setText(temData.barcode)


    }

    private fun initViewClicks() {
        binding.addNewBarcodeImage.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(addNewBarcodeImage, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }


        binding.addOldTagImage.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(addOldTagImage, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.addBOMImage.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(addBOMImage, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.addnameplateImage.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(addnameplateImage, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.newTagScanner.setOnClickListener {

            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(requireContext(), object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                binding.newTagText.text = action

                                if (resources.getBoolean(R.bool.isTablet)) {
                                    VerificationDetailsActivity.mPVData.bomModel[position].barcode = action
                                } else {
                                    VerificationDetailsMobileActivity.mPVData.bomModel[position].barcode = action
                                }
                            }

                        })
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.CAMERA
                )
                .check()

        }
        binding.oldTagScanner.setOnClickListener {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(requireContext(), object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                binding.oldTagNoValue.text = action

                                if (resources.getBoolean(R.bool.isTablet)) {
                                    VerificationDetailsActivity.mPVData.bomModel[position].oldTag = action
                                } else {
                                    VerificationDetailsMobileActivity.mPVData.bomModel[position].oldTag = action
                                }
                            }

                        })
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.CAMERA
                )
                .check()
        }

        binding.edtNewTagNo.addTextChangedListener(
            MyTextWatcher(
                "edtNewTagNo",
                resources.getBoolean(R.bool.isTablet),
                position
            )
        )
        binding.bomuniqueValuetwoTv.addTextChangedListener(
            MyTextWatcher(
                "bomuniqueValuetwoTv",
                resources.getBoolean(R.bool.isTablet),
                position
            )
        )
        binding.qtyValue.addTextChangedListener(
            MyTextWatcher(
                "qtyValue",
                resources.getBoolean(R.bool.isTablet),
                position
            )
        )
        binding.uomValue.addTextChangedListener(
            MyTextWatcher(
                "uomValue",
                resources.getBoolean(R.bool.isTablet),
                position
            )
        )
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
        val myDir = File(directory, "iStock_images")

        myDir.mkdirs()
        val fname = "iStock_$ts" + ".png"
        val file = File(myDir, fname)

//        VerificationDetailsActivity.mPVData.assetImages.newTagImage.add(file.absolutePath)

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

    class MyTextWatcher(val type: String, val boolean: Boolean, val position: Int) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }


        override fun afterTextChanged(p0: Editable?) {

            if (type.equals("edtNewTagNo")) {

                if (boolean) {
                    VerificationDetailsActivity.mPVData.bomModel[position].tagNo = p0.toString()
                } else {
                    VerificationDetailsMobileActivity.mPVData.bomModel[position].tagNo =
                        p0.toString()
                }
            }

            if (type.equals("bomuniqueValuetwoTv")) {

                if (boolean) {
                    VerificationDetailsActivity.mPVData.bomModel[position].bOMName = p0.toString()
                } else {
                    VerificationDetailsMobileActivity.mPVData.bomModel[position].bOMName =
                        p0.toString()
                }
            }

            if (type.equals("qtyValue")) {
                if (boolean) {
                    VerificationDetailsActivity.mPVData.bomModel[position].qty = p0.toString()
                } else {
                    VerificationDetailsMobileActivity.mPVData.bomModel[position].qty = p0.toString()
                }
            }

            if (type.equals("uomValue")) {
                if (boolean) {
                    VerificationDetailsActivity.mPVData.bomModel[position].uOM = p0.toString()
                } else {
                    VerificationDetailsMobileActivity.mPVData.bomModel[position].uOM = p0.toString()
                }
            }

        }

    }

    private fun observeViewModel() {
        fileUploadViewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                if (it.isNotEmpty()) {

                    when (mAction) {
                        addBOMImage -> {

                            if (resources.getBoolean(R.bool.isTablet)) {
                                VerificationDetailsActivity.mPVData.bomModel[position].bOMImg = it
                            } else {
                                VerificationDetailsMobileActivity.mPVData.bomModel[position].bOMImg =
                                    it
                            }


                            loadBOMImage()
                        }

                        addOldTagImage -> {

                            if (resources.getBoolean(R.bool.isTablet)) {
                                VerificationDetailsActivity.mPVData.bomModel[position].oldTagImg =
                                    it
                            } else {
                                VerificationDetailsMobileActivity.mPVData.bomModel[position].oldTagImg =
                                    it
                            }

                            loadOldTagImage()
                        }

                        addNewBarcodeImage -> {
                            if (resources.getBoolean(R.bool.isTablet)) {
                                VerificationDetailsActivity.mPVData.bomModel[position].barCodeImg =
                                    it
                            } else {
                                VerificationDetailsMobileActivity.mPVData.bomModel[position].barCodeImg =
                                    it
                            }


                            loadNewBarCodeImage()
                        }

                        addnameplateImage -> {

                            if (resources.getBoolean(R.bool.isTablet)) {
                                VerificationDetailsActivity.mPVData.bomModel[position].namePlateImg =
                                    it
                            } else {
                                VerificationDetailsMobileActivity.mPVData.bomModel[position].namePlateImg =
                                    it
                            }
                            loadnamePlateImage()
                        }
                        else -> {

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
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })
    }

    private fun recognizeText(image: InputImage) {

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                processTextBlock(visionText)
            }
            .addOnFailureListener { e ->
                Log.e("", "recognizeText addOnFailureListener==>> " + e.message)
            }
        // [END run_detector]
    }

    private fun processTextBlock(result: Text) {
        val resultText = result.text
        var tempText = ""

        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox

            Log.e("", "processTextBlock blockText==>> " + blockText)


            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox

                Log.e("", "processTextBlock lineText==>> " + lineText)

                if (tempText.isEmpty()) {
                    tempText = lineText
                } else {
                    tempText = tempText + "\n" + lineText
                }

                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox

                    Log.e("", "processTextBlock ==>> " + elementText)


                }
            }
        }

        if (mAction.equals(addnameplateImage)) {

            if (resources.getBoolean(R.bool.isTablet)) {
                VerificationDetailsActivity.mPVData.bomModel[position].namePlateText = tempText
            } else {
                VerificationDetailsMobileActivity.mPVData.bomModel[position].namePlateText =
                    tempText
            }

            binding.namePlateThreeTvValue.text =
                binding.namePlateThreeTvValue.text.toString().trim() + "\n" + tempText
        }
    }


}