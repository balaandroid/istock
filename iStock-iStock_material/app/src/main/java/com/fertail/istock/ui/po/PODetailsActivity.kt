package com.fertail.istock.ui.po

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.*
import com.fertail.istock.database.table.PoMainAndSubTable
import com.fertail.istock.database.table.PoSubTable
import com.fertail.istock.databinding.ActivityPoDetailsBinding
import com.fertail.istock.databinding.ActivityPoListBinding
import com.fertail.istock.databinding.PoListItemBinding
import com.fertail.istock.databinding.SubPoListItemBinding
import com.fertail.istock.model.NounTable
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.ui.verification.adapter.AttributesAdapter
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.util.GenericRecyclerAdapter
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.view_model.FileUploadViewModel
import com.fertail.istock.view_model.GetNounViewModel
import com.fertail.istock.view_model.PoViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

@AndroidEntryPoint
class PODetailsActivity : BaseActivity(), actionSelected {

    lateinit var binding: ActivityPoDetailsBinding
    var mAdapter = GenericRecyclerAdapter<PoSubTable>()
    private val poViewModel: PoViewModel by viewModels()
    lateinit var fileUploadViewModel: FileUploadViewModel
    private val getNounViewModel: GetNounViewModel by viewModels()
    val nounList: java.util.ArrayList<NounTable> = java.util.ArrayList()


    var mAction: String = ""
    var mCameraOrGallery: String = ""
    var capturedImage: File? = null
    private var uri: Uri? = null
    var list = ArrayList<PoSubTable>()
    val imageDeta = ImageClass()

    private val activityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                if (mCameraOrGallery.equals(GALLERYTEXT)) {
                    imageDeta.uri = it.data!!.data
                } else {
                    imageDeta.uri = uri
                }

                getContactBitmapFromURI(
                    iStockApplication.appController!!,
                    imageDeta.uri
                )?.let { it1 ->

                    saveBitmapIntoSDCardImage(
                        iStockApplication.appController!!,
                        it1
                    ).let {

                        if (it != null) {
                            lifecycleScope.launch {
                                Compressor.compress(context, it).let {
                                    CommonUtils.showLog("Compressor===>>" + it.name)
                                    CommonUtils.showLog("Compressor===>>" + it.path)
                                    CommonUtils.showLog("Compressor===>>" + it.absolutePath)

                                    if (NetworkUtils.isConnected(context)) {
                                        if (it != null) {
                                            fileUploadViewModel.uploadFile(
                                                it.name,
                                                it,
                                                context
                                            )
                                        }


                                    } else {

                                        saveImageData(it.absolutePath)

                                    }

                                }
                            }
                        }
                    }

                }

            }
        }

    companion object {
        lateinit var data: PoSubTable
        fun start(caller: Context, eachItem: PoSubTable) {
            val intent = Intent(caller, PODetailsActivity::class.java)
            data = eachItem
            caller.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]

        observeViewModel()
        binding.uniqueid.text = "Item Code : " + data.po_id
        binding.poNumber.text = data.id

       initViewClicks()

        binding.descriptionedt.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "descriptionedt"
            )
        )
        binding.idEquipName.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idEquipName"
            )
        )
        binding.idEquipModelNo.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idEquipModelNo"
            )
        )

        binding.idEquipManufacture.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idEquipManufacture"
            )
        )
        binding.idEquipTagNo.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idEquipTagNo"
            )
        )

        binding.idSerialNo.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idSerialNo"
            )
        )


        binding.idEquipAdditionalInfo.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idEquipAdditionalInfo"
            )
        )

        binding.idManufacturerName.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idManufacturerName"
            )
        )


        binding.idParthNo.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idParthNo"
            )
        )
        binding.idModelNo.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "idModelNo"
            )
        )
     binding.noItemedt.addTextChangedListener(
            MyTextWatcher(
                poViewModel,
                "noItemedt"
            )
        )

        lifecycleScope.launch {
            getNounViewModel.getAllDictionary().collectLatest {
                nounList.clear()
                nounList.addAll(it)

                if (it.isEmpty()) {
                    getNounViewModel.getDictionary()
                }
            }
        }


        updateUI()


    }

    private fun initViewClicks() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.addAssetImage.setOnClickListener {
            chooseGalleryOrCameraBottomSheet("ASSETS")
        }
        binding.idAddNamePlate.setOnClickListener {
            chooseGalleryOrCameraBottomSheet("NAMEPLATE")
        }

        binding.nownModifierDropDown.setOnClickListener {
            binding.sixthDropDownContent.isVisible = false
            binding.nownModifierContainer.isVisible = true
            binding.equpmentContainer.isVisible = false
        }
        binding.equipmentDropDown.setOnClickListener {
            binding.sixthDropDownContent.isVisible = false
            binding.nownModifierContainer.isVisible = false
            binding.equpmentContainer.isVisible = true
        }

        binding.sixthDropDown.setOnClickListener {
            binding.sixthDropDownContent.isVisible = true
            binding.nownModifierContainer.isVisible = false
            binding.equpmentContainer.isVisible = false
        }

        binding.idEquipment.setOnClickListener {
            binding.idEquipment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_blue, 0, 0, 0);
            binding.idMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
            data.material = "N"
            poViewModel.addSubPoToTable(data)
        }

        binding.idMaterial.setOnClickListener {
            data.material = "Y"
            poViewModel.addSubPoToTable(data)
            binding.idMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_blue, 0, 0, 0);
            binding.idEquipment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            poViewModel.getSubPOById(data.id).let {
                var data = it

                loadRecyclerView(ASSETS)
                loadRecyclerView(NAMEPLATE)
                loadRecyclerView("NAMEPLATETEXT")

                binding.descriptionedt.setText("" + data.subPo.description)
                binding.idEquipName.setText("" + data.subPo.equipmentName)
                binding.idEquipModelNo.setText("" + data.subPo.equipmentModelNo)
                binding.idEquipManufacture.setText("" + data.subPo.equipmentManufacture)
                binding.idEquipTagNo.setText("" + data.subPo.equipmentTagNo)
                binding.idSerialNo.setText("" + data.subPo.equipmentSerialNo)
                binding.idEquipAdditionalInfo.setText("" + data.subPo.equipmentAdditionalInformation)
                binding.idManufacturerName.setText("" + data.subPo.vendorName)
                binding.idParthNo.setText("" + data.subPo.vendorPartNo)
                binding.idModelNo.setText("" + data.subPo.vendorModelNo)
                binding.noItemedt.setText("" + data.subPo.noOfItem)

                if (data.subPo.material.equals("Y")){
                    binding.idMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_blue, 0, 0, 0);
                    binding.idEquipment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
                }else{
                    binding.idEquipment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_blue, 0, 0, 0);
                    binding.idMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
                }


            }
        }
    }

    fun chooseGalleryOrCameraBottomSheet(open: String) {
        val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(open, this)
        bottomSheetFilterDialogFragment.show(
            supportFragmentManager,
            "bottomSheetFrag"
        )
    }

    override fun optionChosen(action: String, from: String) {
        mCameraOrGallery = action
        mAction = from

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
    }

    private fun observeViewModel() {


        fileUploadViewModel.showSuccess.observe(this, Observer { countries ->
            countries?.let {

                if (it.isNotEmpty()) {

                    saveImageData(it)

                    fileUploadViewModel.showSuccess.postValue("")
                }


            }
        })
        fileUploadViewModel.showError.observe(this, Observer { isError ->
            Toast.makeText(this, isError, Toast.LENGTH_SHORT).show()
        })
        fileUploadViewModel.showLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })

        getNounViewModel.showSuccessAttributes.observe(this, Observer { idModifier ->
            idModifier?.let {

                if (it.isNotEmpty()) {

//                    VerificationDetailsMobileActivity.mPVData.characteristics.clear()
//                    VerificationDetailsMobileActivity.mPVData.characteristics.addAll(it)

                    updateRecyclerView()

                    getNounViewModel.showSuccessAttributes.postValue(java.util.ArrayList())
                }


            }
        })




        getNounViewModel.showError.observe(this, Observer { isError ->
            Toast.makeText(this, isError, Toast.LENGTH_SHORT).show()
        })

        getNounViewModel.showLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })


    }

    private fun saveImageData(absolutePath: String) {
        when (mAction) {
            ASSETS -> {
                poViewModel.saveAssertImage(absolutePath, data.id)
            }

            NAMEPLATE -> {
                poViewModel.saveNamePlateImage(absolutePath, data.id)
                recognizeText(
                    InputImage.fromFilePath(
                        this,
                        imageDeta.uri!!
                    )
                )
            }
        }

        updateUI()

//        loadRecyclerView(mAction)
    }

    private fun updateRecyclerView() {

//        VerificationDetailsMobileActivity.mPVData.characteristics.let {
//            binding.idAttribute.layoutManager = LinearLayoutManager(this)
//            binding.idAttribute.adapter = AttributesAdapter(it, "mobile")
//        }

    }

    fun loadRecyclerView(action: String) {

        val horizontalLayoutManagaer =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        when (action) {
            ASSETS -> {
                binding.rcyAssets.layoutManager = horizontalLayoutManagaer

                lifecycleScope.launch {
                    poViewModel.getAssertImageId(data.id).let {

                        var listItem = ArrayList<String>();

                        it.forEach {
                            it.image_url?.let { it1 -> listItem.add(it1) }
                        }



                        binding.rcyAssets.adapter = CustomAdapter(
                            listItem,
                            this@PODetailsActivity
                        )
                    }
                }


            }

            NAMEPLATE -> {
                binding.rcyNamePlate.layoutManager = horizontalLayoutManagaer
                lifecycleScope.launch {
                    poViewModel.getNamePlateImageId(data.id).let {

                        var listItem = ArrayList<String>();

                        it.forEach {
                            it.image_url?.let { it1 -> listItem.add(it1) }
                        }

                        binding.rcyNamePlate.adapter = CustomAdapter(
                            listItem,
                            this@PODetailsActivity
                        )
                    }
                }

            }

            "NAMEPLATETEXT" -> {
                lifecycleScope.launch {
                    poViewModel.getNamePlateTextId(data.id).let {

                        it.forEach {
                            if (binding.namePlateTwoTvValue.text.toString().isEmpty()) {
                                binding.namePlateTwoTvValue.text = it.text
                            } else {
                                binding.namePlateTwoTvValue.text =
                                    binding.namePlateTwoTvValue.text.toString() + "\n" + it.text
                            }
                        }
                    }
                }
            }
        }
    }

    private fun capturePhoto() {

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        capturedImage = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "iStock_$ts.jpg"
        )
        if (capturedImage!!.exists()) {
            capturedImage!!.delete()
        }
        capturedImage!!.createNewFile()
        uri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".provider",
                capturedImage!!
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        activityLauncher.launch(intent)
//        startActivityForResult(intent, CAPTURE_PHOTO)
    }


    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"

        activityLauncher.launch(intent)
//        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    private fun getContactBitmapFromURI(context: Context, uri: Uri?): Bitmap? {
        try {
//            val input = context.contentResolver.openInputStream(uri!!) ?: return null
//            val bitmap = BitmapFactory.decodeStream(input)
            if (uri != null) {
                if (mCameraOrGallery.equals(GALLERYTEXT)) {
                    val input = context.contentResolver.openInputStream(uri) ?: return null
                    val bitmap = BitmapFactory.decodeStream(input)
                    return bitmap
                }

                return CommonUtils.decodeFile(capturedImage!!.absolutePath)
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveBitmapIntoSDCardImage(context: Context?, finalBitmap: Bitmap): File? {

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        val cw = ContextWrapper(iStockApplication.appController)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val myDir = File(directory, "iStock_images")

        myDir.mkdirs()
        val fname = data.po_id + "_$ts" + ".png"
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

        if (mAction.equals(NAMEPLATE)) {
            poViewModel.saveNamePlateText(tempText, data.id)
            binding.namePlateTwoTvValue.text =
                binding.namePlateTwoTvValue.text.toString().trim() + "\n" + tempText
        }

    }

    class MyTextWatcher(val poViewModel: PoViewModel, val type: String) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            if (type.equals("descriptionedt")) {
                data.description = p0.toString()
            }

            if (type.equals("idEquipName")) {
                data.equipmentName = p0.toString()
            }

            if (type.equals("idEquipModelNo")) {
                data.equipmentModelNo = p0.toString()
            }
            if (type.equals("idEquipManufacture")) {
                data.equipmentManufacture = p0.toString()
            }
            if (type.equals("idSerialNo")) {
                data.equipmentSerialNo = p0.toString()
            }

            if (type.equals("idEquipTagNo")) {
                data.equipmentTagNo = p0.toString()
            }
            if (type.equals("idEquipAdditionalInfo")) {
                data.equipmentAdditionalInformation = p0.toString()
            }

            if (type.equals("idManufacturerName")) {
                data.vendorName = p0.toString()
            }

            if (type.equals("idParthNo")) {
                data.vendorPartNo = p0.toString()
            }

            if (type.equals("idModelNo")) {
                data.vendorModelNo = p0.toString()
            }

            if (type.equals("noItemedt")) {
                data.noOfItem = p0.toString()
            }

            poViewModel.addSubPoToTable(data)
        }

    }


}