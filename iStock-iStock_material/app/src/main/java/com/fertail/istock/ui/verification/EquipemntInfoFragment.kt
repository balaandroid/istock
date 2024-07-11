package com.fertail.istock.ui.verification

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.BuildConfig
import com.fertail.istock.R
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.FragmentEpuipmentInfoBinding
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.AssetCondition
import com.fertail.istock.model.AssetImages
import com.fertail.istock.model.GIS
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.ScannerActivity
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.util.CommonUtils
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
import java.io.IOException


class EquipemntInfoFragment : BaseFragment(), actionSelected {
    private var _binding: FragmentEpuipmentInfoBinding? = null
    private val binding get() = _binding!!

    private var uri: Uri? = null

    var mAction: String = ""
    var mCameraOrGallery: String = ""

    lateinit var fileUploadViewModel: FileUploadViewModel

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                val data = ImageClass()
                if (mCameraOrGallery.equals(GALLERYTEXT)) {
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
                                    CommonUtils.showLog("Compressor===>>" + it.name)
                                    CommonUtils.showLog("Compressor===>>" + it.path)
                                    CommonUtils.showLog("Compressor===>>" + it.absolutePath)

                                    if (NetworkUtils.isConnected(requireContext())) {
                                        if (it != null) {
                                            fileUploadViewModel.uploadFile(
                                                it.name,
                                                it,
                                                requireContext()
                                            )
                                        }

                                        if (mAction.equals(NAMEPLATE)) {
                                            recognizeText(
                                                InputImage.fromFilePath(
                                                    requireContext(),
                                                    data.uri!!
                                                )
                                            )
                                        } else if (mAction.equals(NAMEPLATE1)) {
                                            recognizeText(
                                                InputImage.fromFilePath(
                                                    requireContext(),
                                                    data.uri!!
                                                )
                                            )
                                        }

                                    } else {
                                        if (mAction.equals(NAMEPLATE)) {
                                            VerificationDetailsActivity.mPVData.assetImages.namePlateImge!!.add(
                                                it!!.absolutePath
                                            )
                                            loadNumberPlateImag()
                                            recognizeText(
                                                InputImage.fromFilePath(
                                                    requireContext(),
                                                    data.uri!!
                                                )
                                            )
                                        } else if (mAction.equals(NAMEPLATE1)) {
                                            VerificationDetailsActivity.mPVData.assetImages.namePlateImgeTwo.add(
                                                it!!.absolutePath
                                            )
                                            loadNumberPlateImag1()
                                            recognizeText(
                                                InputImage.fromFilePath(
                                                    requireContext(),
                                                    data.uri!!
                                                )
                                            )
                                        } else if (mAction.equals(addOldTag)) {
                                            VerificationDetailsActivity.mPVData.assetImages.oldTagImage!!.add(
                                                it!!.absolutePath
                                            )
                                            loadaddOldTag()
                                        } else {
                                            VerificationDetailsActivity.mPVData.assetImages.assetImage.add(
                                                it!!.absolutePath
                                            )
                                            loadAssetsImage()
                                        }
                                    }

                                }
                            }
                        }
                    }

                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEpuipmentInfoBinding.inflate(inflater, container, false)

        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]

        initToolbor()

        observeViewModel()
        updateData()
        initViewClicks()
        loadAssetsImage()
        loadNumberPlateImag()
        loadNumberPlateImag1()
        loadaddOldTag()
        initCustomTabClick()
        binding.oldTagText.text = VerificationDetailsActivity.mPVData.uniqueId
        binding.ideqpno.text = VerificationDetailsActivity.mPVData.sAPEquipment ?: NoData
        binding.idDescriptionContent.text = VerificationDetailsActivity.mPVData.equipmentDesc
        binding.oldTagNoValue.text = VerificationDetailsActivity.mPVData.oldTagNo

        return binding.root
    }

    private fun updateData() {
        if (VerificationDetailsActivity.mPVData.assetImages == null) {
            VerificationDetailsActivity.mPVData.assetImages = AssetImages()
        }

        if (VerificationDetailsActivity.mPVData.assetCondition == null) {
            VerificationDetailsActivity.mPVData.assetCondition = AssetCondition()
        }

        if (VerificationDetailsActivity.mPVData.gIS == null) {
            VerificationDetailsActivity.mPVData.gIS = GIS()
        }
        VerificationDetailsActivity.mPVData.assetImages.namePlateText!!.forEach {
            if (binding.txtExtracted.text.toString().isEmpty()) {
                binding.txtExtracted.text = it
            } else {
                binding.txtExtracted.text = binding.txtExtracted.text.toString() + "\n" + it
            }
        }


        VerificationDetailsActivity.mPVData.assetImages.namePlateTextTwo!!.forEach {
            if (binding.txtExtracted1.text.toString().isEmpty()) {
                binding.txtExtracted1.text = it
            } else {
                binding.txtExtracted1.text = binding.txtExtracted1.text.toString() + "\n" + it
            }
        }

        binding.scannerText.text = VerificationDetailsActivity.mPVData.oldTagNo
    }

    fun loadAssetsImage() {
//        binding.rcyAssets.layoutManager = LinearLayoutManager(requireContext())

        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

//        binding.rcyAssets.layoutManager = LinearLayoutManager(
//                requireContext());

        binding.rcyAssets.layoutManager = horizontalLayoutManagaer

        binding.rcyAssets.adapter = CustomAdapter(
            VerificationDetailsActivity.mPVData.assetImages.assetImage,
            requireActivity()
        )
    }

    fun loadNumberPlateImag() {
        binding.rcyNamePlate.layoutManager = LinearLayoutManager(requireContext())
        binding.rcyNamePlate.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyNamePlate.adapter = CustomAdapter(
            VerificationDetailsActivity.mPVData.assetImages.namePlateImge!!,
            requireActivity()
        )
    }

    fun loadNumberPlateImag1() {
        binding.rcyNamePlate1.layoutManager = LinearLayoutManager(requireContext())
        binding.rcyNamePlate1.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyNamePlate1.adapter = CustomAdapter(
            VerificationDetailsActivity.mPVData.assetImages.namePlateImgeTwo,
            requireActivity()
        )
    }

    fun loadaddOldTag() {
        binding.rcyOldTag.layoutManager = LinearLayoutManager(requireContext())
        binding.rcyOldTag.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyOldTag.adapter = CustomAdapter(
            VerificationDetailsActivity.mPVData.assetImages.oldTagImage!!,
            requireActivity()
        )
    }


    private fun initViewClicks() {
        binding.addAssetImage.setOnClickListener {

            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(ASSETS, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )


//            CustomBottomSheetDialogFragment( ASSETS , this).apply {
//               show(requireActivity().supportFragmentManager, CustomBottomSheetDialogFragment.TAG)
//            }
        }
        binding.addnameplateImage.setOnClickListener {

            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(NAMEPLATE, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )

        }

        binding.addnameplateImage1.setOnClickListener {

            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(NAMEPLATE1, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )

        }


        binding.addOldTag.setOnClickListener {
            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(addOldTag, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        binding.oldTagScanner.setOnClickListener {

            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(requireContext(), object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                binding.scannerText.text = action
                                VerificationDetailsActivity.mPVData.oldTagNo = action
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

    }

    private fun initToolbor() {

        binding.toolbarEquipmentInfo.toolbarTitle.text = resources.getText(R.string.equipment_info)


        binding.toolbarEquipmentInfo.btnMenu.visibility = View.GONE
        binding.toolbarEquipmentInfo.btnBack.visibility = View.VISIBLE
        binding.toolbarEquipmentInfo.btnLogout.visibility = View.GONE
        binding.toolbarEquipmentInfo.btnSearch.visibility = View.GONE

        binding.toolbarEquipmentInfo.btnBack.setOnClickListener {
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
        binding.customTab.imgTick2.setBackgroundResource(R.drawable.tick_orange_two_icon)

    }

    private fun continueClicked() {
        if (VerificationDetailsActivity.mPVData.assetImages.assetImage.size > 0) {

//            VerificationDetailsActivity.mPVData.assetImages.namePlateTextONE = binding.txtExtracted.text.toString().trim()
//            VerificationDetailsActivity.mPVData.assetImages.namePlateTextTWO = binding.txtExtracted1.text.toString().trim()

            findNavController().navigate(R.id.action_nav_photos)
        } else {
            CommonUtils.showAlert(requireContext(), "Please add at least one asset image")
        }

    }

    private fun show(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

        galleryLauncher.launch(intent)
//        startActivityForResult(intent, CAPTURE_PHOTO)
    }

    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"

        galleryLauncher.launch(intent)
//        startActivityForResult(intent, CHOOSE_PHOTO)
    }


    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
//            imageView?.setImageBitmap(bitmap)
        } else {
            show("ImagePath is null")
        }
    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor =
            requireActivity().getContentResolver().query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }


    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(requireActivity(), uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion
                )
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(
                        "content://downloads/public_downloads"
                    ), java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = uri!!.path
        }
        renderImage(imagePath)
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


    private fun observeViewModel() {
        fileUploadViewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {

                if (it.isNotEmpty()) {
                    if (mAction.equals(NAMEPLATE)) {
                        VerificationDetailsActivity.mPVData.assetImages.namePlateImge!!.add(it)
                        loadNumberPlateImag()
                    } else if (mAction.equals(NAMEPLATE1)) {
                        VerificationDetailsActivity.mPVData.assetImages.namePlateImgeTwo.add(it)
                        loadNumberPlateImag1()
                    } else if (mAction.equals(addOldTag)) {
                        VerificationDetailsActivity.mPVData.assetImages.oldTagImage!!.add(it)
                        loadaddOldTag()
                    } else {
                        VerificationDetailsActivity.mPVData.assetImages.assetImage.add(it)
                        loadAssetsImage()
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

        if (mAction.equals(NAMEPLATE)) {
            VerificationDetailsActivity.mPVData.assetImages.namePlateText!!.add(tempText)
            binding.txtExtracted.text =
                binding.txtExtracted.text.toString().trim() + "\n" + tempText
        } else {
            VerificationDetailsActivity.mPVData.assetImages.namePlateTextTwo!!.add(tempText)
            binding.txtExtracted1.text =
                binding.txtExtracted1.text.toString().trim() + "\n" + tempText
        }

    }

    override fun optionChosen(action: String, from: String) {

        mCameraOrGallery = action

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
    }

    fun initCustomTabClick() {

        binding.customTab.constrainPhysicalObservation.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainEquipmentInfo.setOnClickListener {

        }

        binding.customTab.constrainPhotos.setOnClickListener {
            continueClicked()
        }

        binding.customTab.constrainLocation.setOnClickListener {
            showAlert()
        }

        binding.customTab.constrainBom.setOnClickListener {
            showAlert()
        }
    }

    fun showAlert() {
        Toast.makeText(requireContext(), "Fill Asset Conditions first", Toast.LENGTH_SHORT).show()
    }

    fun getContactBitmapFromURI(context: Context, uri: Uri?): Bitmap? {
        try {
            val input = context.contentResolver.openInputStream(uri!!) ?: return null
            return BitmapFactory.decodeStream(input)
        } catch (e: FileNotFoundException) {
        }
        return null
    }


    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(selectedImage.path!!)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
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


}