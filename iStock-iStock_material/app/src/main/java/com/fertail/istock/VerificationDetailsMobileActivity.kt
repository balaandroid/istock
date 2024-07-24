package com.fertail.istock

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.ObservableChar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.databinding.ActivityVerificationDetailsMobileBinding
import com.fertail.istock.googleMap.GoogleMapActivity
import com.fertail.istock.model.*
import com.fertail.istock.ui.LocationConverter
import com.fertail.istock.ui.ScannerActivity
import com.fertail.istock.ui.bottomsheet.*
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.ui.verification.BOMPageAdapter
import com.fertail.istock.ui.verification.FuntionalLocationFragment
import com.fertail.istock.ui.verification.adapter.AttributesAdapter
import com.fertail.istock.ui.verification.adapter.NumberAdapter
import com.fertail.istock.ui.verification.itemClicked
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.util.CustomSpinnerAdapter
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.util.SessionManager
import com.fertail.istock.util.UriWithDate
import com.fertail.istock.view_model.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.journeyapps.barcodescanner.CaptureActivity
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import jxl.Workbook
import jxl.format.Colour
import jxl.write.Label
import jxl.write.WritableCell
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class VerificationDetailsMobileActivity : BaseActivity(), actionSelected, itemClicked {
    lateinit var binding: ActivityVerificationDetailsMobileBinding
    var data: GetAllMasterDataResponse? = null

    var dropDownVisibility = ObservableChar('1')
    var observableCorrosion = ObservableChar('n')
    var observableDamage = ObservableChar('n')
    var observableLeakage = ObservableChar('n')
    var observableVibration = ObservableChar('n')
    var observableTemperature = ObservableChar('n')
    var observableSmell = ObservableChar('n')
    var observableNoise = ObservableChar('n')

    var mFunctional = ObservableChar('n')
    lateinit var context: Context

    lateinit var locationManager: LocationManager
    var locationByGps: Location? = null
    var locationByNetwork: Location? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var uri: Uri? = null
    private val uriArrayList=ArrayList<Uri>()
    private lateinit var session: SessionManager


    val options = arrayOf("Yes", "No")
    val assetConditionOption = arrayOf("Working", "Dismantled", "Demolished", "Under Maintenance", "Idle")

    lateinit var viewModel: GetAllMasterViewModel
    lateinit var fileUploadViewModel: FileUploadViewModel
    val siteViewModel: GetStatusViewModel by viewModels()
    val locationViewModel: LocationHierarchyViewModel by viewModels()
    val classificationViewModel: ClassificationHierarchyViewModel by viewModels()
    private val masterViewModel: GetAllMasterViewModel by viewModels()


    var mAction: String = ""
    var mCameraOrGallery: String = ""
    val imageDeta = ImageClass()

    val nounList: ArrayList<NounTable> = ArrayList()
    private val getNounViewModel: GetNounViewModel by viewModels()

    private val videoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode != RESULT_OK) {
                val videoUri: Uri? = it?.data?.data
                if (videoUri != null) {
//                    val bitmap = getVideoThumbnail(this, videoUri, 1000, 1000)
                    val mimeType: String = contentResolver.getType(videoUri)!!
                    val file = saveVideoToAppScopeStorage(this, videoUri, mimeType)

                }

            }

        }
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
                    imageDeta.uri)?.let { it1 ->

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
                                        fileUploadViewModel.uploadFile(
                                            it.name,
                                            it,
                                            context
                                        )


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

    private fun saveImageData(absolutePath: String) {
        when (mAction) {
            ASSETS -> {
                mPVData.assetImages.assetImage.add(
                    absolutePath
                )
            }

            ASSETSMAXIMO -> {
                mPVData.assetImages.matImgs.add(
                    absolutePath
                )
            }

            NAMEPLATE -> {
                mPVData.assetImages.namePlateImge!!.add(
                    absolutePath
                )

                recognizeText(
                    InputImage.fromFilePath(
                        this,
                        imageDeta.uri!!
                    )
                )
            }

            NAMEPLATE1 -> {
                mPVData.assetImages.namePlateImgeTwo.add(
                    absolutePath
                )

                recognizeText(
                    InputImage.fromFilePath(
                        this,
                        imageDeta.uri!!
                    )
                )

            }

            addOldTag -> {
                mPVData.assetImages.oldTagImage!!.add(
                    absolutePath
                )
            }

            addnewTag -> {
                mPVData.assetImages.newTagImage.add(
                    absolutePath
                )
            }

            captureCorrosion -> {
                mPVData.assetCondition.corrosionImage.add(
                    absolutePath
                )
            }

            captureDamage -> {
                mPVData.assetCondition.damageImage.add(
                    absolutePath
                )
            }

            captureLeakage -> {
                mPVData.assetCondition.leakageImage.add(
                    absolutePath
                )
            }

            captureTemprature -> {
                mPVData.assetCondition.temparatureImage.add(
                    absolutePath
                )
            }
        }

        loadRecyclerView(mAction)
    }

    var isneedtoSave = true

    companion object {
        var mPVData = PVData()
        fun start(caller: Context, data: PVData) {
            val intent = Intent(caller, VerificationDetailsMobileActivity::class.java)
            mPVData = data
            caller.startActivity(intent)
        }
        val headers = listOf(
            "Description", "Far Id", "Site Id", "Asset Number", " Tar Asset Conditions", "Tar Description", "Parent", "Location",
            "Location Hierarchy", "Classification Hierarchy", "Status", "Asset Type", "Report Group", "Serial Number", "Model Number", "Model Year",
            "Manufacturer", "Asset Images", "AssetMaxim Image", "NamePlate1 Image", "NamePlate1 Text", "NamePlate2 Image",
            "NamePlate2 Text", "PV Remark", "Rework Remark", "Latitude", "Longitude", "OldTag Image",
            "OldTag Barcode", "NewTag Image","NewTag Barcode","Asset Availability","Present Location",
            "Existing Barcode","Barcode Number","Additional Notes","Owned By","Operated By","Maintained By",
            "Corrosion","Damage","Leakage","Vibration","Temperature","Smell","Noise","Remark","Corrosion Image",
            "Damage Image","Leakage Image","Temperature Image"

        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationDetailsMobileBinding.inflate(layoutInflater)
        EventBus.getDefault().register(this)
        viewModel = ViewModelProvider(this)[GetAllMasterViewModel::class.java]
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        session= SessionManager(this)

        context = this

        setContentView(binding.root)
        setViewClick()
        isLocationEnabled()
        observeViewModel()
        initViewPager()
        initRecyclerView()
        loadRecyclerView(ASSETS)
        loadRecyclerView(ASSETSMAXIMO)
        loadRecyclerView(NAMEPLATE)
        loadRecyclerView(NAMEPLATE1)
        loadRecyclerView(addOldTag)
        loadRecyclerView(addnewTag)
        loadRecyclerView(captureCorrosion)
        loadRecyclerView(captureDamage)
        loadRecyclerView(captureLeakage)
        loadRecyclerView(captureTemprature)
        initSpinner()
        updateData()

        binding.viewModel = this
    }

    var idAssetAvailabilitySpinnerSelected = false
    var idExistingBarCodeSpinnerSelected = false
    var idExistingAssetSpinnerSelected = false




    private fun initSpinner() {
//        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
//        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)

        val adapter1: CustomSpinnerAdapter = CustomSpinnerAdapter(this, options.toMutableList())
        val adapter2: CustomSpinnerAdapter = CustomSpinnerAdapter(this, options.toMutableList())
        val adapter3: CustomSpinnerAdapter = CustomSpinnerAdapter(this, assetConditionOption.toMutableList())


        binding.idAssetAvailabilitySpinner.adapter = adapter1;
        binding.idExistingBarCodeSpinner.adapter = adapter2;
        binding.idAssetConditionsSpinner.adapter = adapter3


        binding.idAssetAvailability.setOnClickListener {
            binding.idAssetAvailabilitySpinner.post {
                idAssetAvailabilitySpinnerSelected = true
                binding.idAssetAvailabilitySpinner.performClick()
            }
        }

        binding.idExistingBarCode.setOnClickListener {
            binding.idExistingBarCodeSpinner.post {
                idExistingBarCodeSpinnerSelected = true
                binding.idExistingBarCodeSpinner.performClick()
            }
        }
        binding.IdAsscond.setOnClickListener {
            binding.idAssetConditionsSpinner.post {
                idExistingAssetSpinnerSelected = true
                binding.idAssetConditionsSpinner.performClick()
            }
        }

        binding.idAssetAvailabilitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long) {

                    if (idAssetAvailabilitySpinnerSelected){
                        mPVData.availabilityofAsset = if (position == 0){"Yes"}else{"No"}
                        updateData()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    idAssetAvailabilitySpinnerSelected = false
                }

            }

        binding.idExistingBarCodeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long) {
                    if (idExistingBarCodeSpinnerSelected){
                        mPVData.existingBarCode = if (position == 0){"Yes"}else{"No"}
                        updateData()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    idExistingBarCodeSpinnerSelected = false
                }

            }

        binding.idAssetConditionsSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (idExistingAssetSpinnerSelected){
                        mPVData.assCondition = assetConditionOption.get(position)
                        updateData()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    idExistingAssetSpinnerSelected = false
                }

            }
    }



    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            Toast.makeText(this,"You are online!",Toast.LENGTH_SHORT).show()
        } else {
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.internet_alert_color))
            Toast.makeText(this,"You are offline!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        binding.description.text = mPVData.equipmentDesc ?: ""
        binding.descriptionedt.setText(mPVData.equipmentDesc ?: "")
        binding.idRemark.setText(mPVData.cRemarks ?: "")

        binding.uniqueid.text = "Asset ID: " + mPVData.assetNo
        binding.idSiteId.setText(mPVData.siteId)
        binding.idFarID.text = mPVData.fixedAssetNo
        binding.idAssetNum.setText(mPVData.pvassetNo ?: mPVData.assetNo)
        binding.idDescriptionTar.setText(mPVData.equipmentDesc)
        binding.idMake.setText(mPVData.manufacture)
        binding.idParent.setText(mPVData.parent)
        binding.idLocation.setText(mPVData.location)
        binding.idLocationHierarchy.text = mPVData.locationHierarchy
        binding.idClasificationHierarchyDes.text = mPVData.classificationHierarchyDesc
        binding.IdStatus.text = mPVData.status
        binding.IdAssetType.setText(mPVData.assetType)
        binding.IdReportGroup.setText(mPVData.reportGroup)
        binding.IdSerialNum.setText(mPVData.serialNo)
        binding.idModelNum.setText(mPVData.modelNo)
        binding.idModelYear.setText(mPVData.modelYear)
        binding.IdAsscond.text = mPVData.assCondition

        if(mPVData.availabilityofAsset.isNullOrEmpty()){
            mPVData.availabilityofAsset = "Yes"
        }

        if(mPVData.existingBarCode.isNullOrEmpty()){
            mPVData.existingBarCode = "Yes"
        }


        if (mPVData.assCondition.isNullOrEmpty()){
            mPVData.assCondition =  "Working"
        }


        binding.IdAsscond.text = mPVData.assCondition
        binding.idAssetAvailability.text = mPVData.availabilityofAsset
        binding.idCurrentLocation.setText(mPVData.presentLocation)
        binding.idExistingBarCode.text = mPVData.existingBarCode
        binding.idBarCodeNumber.setText(mPVData.barcodeNumber)
        binding.idNoOfContract.setText(mPVData.noOfContract)
        binding.idNoOfAssetmaximo.setText(mPVData.nosOfAsestMaximo)
        binding.idRemarkNew.setText(mPVData.remarks)
        binding.idMaximoAssetInfo.setText(mPVData.maximoAssetInfo)
        binding.idOwnedBy.setText(mPVData.mOwnedBy)
        binding.idOperatedBy.setText(mPVData.mOperatedBy)
        binding.idMaintainedBy.setText(mPVData.mMaintainer)
        binding.idNouneModifierRemark.setText(mPVData.mReworkRemarks)


        binding.oldTagId.text = mPVData.oldTagNo
        binding.newTagId.text = mPVData.newTagNo



        if (mPVData.isNewOne == true) {
            binding.description.visibility = View.GONE
            binding.descriptionedt.visibility = View.VISIBLE
        } else {
            binding.description.visibility = View.VISIBLE
            binding.descriptionedt.visibility = View.GONE
        }

        val assetMinorClass1 =
            data?.minorClasses?.filter { s -> (s.id.equals(mPVData.minorClass)) }

        if (!assetMinorClass1.isNullOrEmpty()) {
            binding.functiontextView.text = assetMinorClass1[0].minorClass
        }

        val tempAssetEqui =
            data?.equipmentClasses?.filter { s -> (s.id.equals(mPVData.equipmentClass)) }

        if (!tempAssetEqui.isNullOrEmpty()) {
            binding.equipmenttv.text = tempAssetEqui[0].equipmentClass
        }

        val assetRegion1 =
            data?.regions?.filter { s -> (s.id.equals(mPVData.region)) }

        if (!assetRegion1.isNullOrEmpty()) {
            binding.textView.setText(assetRegion1[0].region)
        }

        val assetArea1 =
            data?.areas?.filter { s -> (s.id.equals(mPVData.area)) }

        if (!assetArea1.isNullOrEmpty()) {
            binding.areatextView.setText(assetArea1[0].area)
        }

        val assetSubArea1 =
            data?.subAreas?.filter { s -> (s.id.equals(mPVData.subArea)) }

        if (!assetSubArea1.isNullOrEmpty()) {
            binding.subareatextView.setText(assetSubArea1[0].subArea)
        }

        val assetSubSystemIdentifier1 =
            data?.identifiers?.filter { s -> (s.id.equals(mPVData.identifier)) }

        if (!assetSubSystemIdentifier1.isNullOrEmpty()) {
            binding.subsystemtextView.setText(assetSubSystemIdentifier1[0].identifier)
        }

        val assetEquipmentType1 =
            data?.equipmentTypes?.filter { s -> (s.id.equals(mPVData.equipmentType)) }

        if (!assetEquipmentType1.isNullOrEmpty()) {
            binding.Equipment.setText(assetEquipmentType1[0].equipmentType)
        }

        if (data?.locations.isNullOrEmpty()) {
            val flocCode1 =
                data?.locations?.filter { s -> (s.id.equals(mPVData.function)) }

            if (!flocCode1.isNullOrEmpty()) {
                binding.locationtv.setText(flocCode1[0].location)
            }
        }


        val assetCity1 =
            data?.cities?.filter { s -> (s.id.equals(mPVData.city)) }

        if (!assetCity1.isNullOrEmpty()) {
            binding.citytextView.setText(assetCity1[0].city)
        }

        if (mPVData.assetCondition?.corrosion.equals("High")) {
            observableCorrosion.set('h')
        } else if (mPVData.assetCondition?.corrosion.equals("Medium")) {
            observableCorrosion.set('m')
        } else {
            observableCorrosion.set('n')
        }

        if (mPVData.assetCondition?.damage.equals("High")) {
            observableDamage.set('h')
        } else if (mPVData.assetCondition?.damage.equals("Medium")) {
            observableDamage.set('m')
        } else {
            observableDamage.set('n')
        }

        if (mPVData.assetCondition?.leakage.equals("High")) {
            observableLeakage.set('h')
        } else if (mPVData.assetCondition?.leakage.equals("Medium")) {
            observableLeakage.set('m')
        } else {
            observableLeakage.set('n')
        }


        if (mPVData.assetCondition?.vibration.equals("High")) {
            observableVibration.set('h')
        } else if (mPVData.assetCondition?.vibration.equals("Medium")) {
            observableVibration.set('m')
        } else {
            observableVibration.set('n')
        }

        if (mPVData.assetCondition?.temparature.equals("High")) {
            observableTemperature.set('h')
        } else if (mPVData.assetCondition?.temparature.equals("Medium")) {
            observableTemperature.set('m')
        } else {
            observableTemperature.set('n')
        }

        if (mPVData.assetCondition?.smell.equals("High")) {
            observableSmell.set('h')
        } else if (mPVData.assetCondition?.smell.equals("Medium")) {
            observableSmell.set('m')
        } else {
            observableSmell.set('n')
        }

        if (mPVData.assetCondition?.noise.equals("High")) {
            observableNoise.set('h')
        } else if (mPVData.assetCondition?.noise.equals("Medium")) {
            observableNoise.set('m')
        } else {
            observableNoise.set('n')
        }

//        if (mPVData.assetCondition.assetCondition.equals("Functional")) {
//            mFunctional.set('f')
//        } else if (mPVData.assetCondition?.assetCondition.equals(
//                "standby",
//                true
//            )
//        ) {
//            mFunctional.set('s')
//        } else {
//            mFunctional.set('n')
//        }



        if (mPVData.idleOperational?.equals("Idle") == true) {
            mFunctional.set('n')
        } else {
            mFunctional.set('f')
        }


        binding.latitudeValue.setText(mPVData.gIS?.lattitudeStart)
        binding.longitudeValue.setText(mPVData.gIS?.longitudeStart)

        binding.remark.setText(mPVData.assetConditionRemarks)
        binding.idModelNo.setText(mPVData.modelNo)
        binding.idNoune.setText(mPVData.noun)
        binding.idModifier.setText(mPVData.modifier)

        binding.idManufacturerName.setText(mPVData.vendorsuppliers.manufacture)
        binding.idParthNo.setText(mPVData.vendorsuppliers.partNo)
        binding.idModelNo.setText(mPVData.vendorsuppliers.modelNo)

        binding.latitudeValue.addTextChangedListener(MyTextWatcher("lat1"))
        binding.longitudeValue.addTextChangedListener(MyTextWatcher("lan1"))
        binding.descriptionedt.addTextChangedListener(MyTextWatcher("descriptionedt"))
        binding.idRemark.addTextChangedListener(MyTextWatcher("idRemark"))
        binding.remark.addTextChangedListener(MyTextWatcher("remark"))
//        binding.idEquipName.addTextChangedListener(MyTextWatcher("idEquipName"))
//        binding.idEquipModelNo.addTextChangedListener(MyTextWatcher("idEquipModelNo"))
//        binding.idEquipManufacture.addTextChangedListener(MyTextWatcher("idEquipManufacture"))
//        binding.idEquipTagNo.addTextChangedListener(MyTextWatcher("idEquipTagNo"))
//        binding.idSerialNo.addTextChangedListener(MyTextWatcher("idSerialNo"))
//        binding.idEquipAdditionalInfo.addTextChangedListener(MyTextWatcher("idEquipAdditionalInfo"))
        binding.idManufacturerName.addTextChangedListener(MyTextWatcher("idManufacturerName"))
        binding.idParthNo.addTextChangedListener(MyTextWatcher("idParthNo"))
        binding.idModelNo.addTextChangedListener(MyTextWatcher("idModelNo"))
        binding.idSiteId.addTextChangedListener(MyTextWatcher("idSiteId"))
        binding.idAssetNum.addTextChangedListener(MyTextWatcher("idAssetNum"))
        binding.idDescriptionTar.addTextChangedListener(MyTextWatcher("idDescriptionTar"))
        binding.idMake.addTextChangedListener(MyTextWatcher("idMake"))
        binding.idParent.addTextChangedListener(MyTextWatcher("idParent"))
        binding.idLocation.addTextChangedListener(MyTextWatcher("idLocation"))
        binding.IdReportGroup.addTextChangedListener(MyTextWatcher("IdAssetType"))
        binding.IdSerialNum.addTextChangedListener(MyTextWatcher("IdSerialNum"))
        binding.idModelNum.addTextChangedListener(MyTextWatcher("idModelNum"))
        binding.idModelYear.addTextChangedListener(MyTextWatcher("idModelYear"))
//        binding.IdAsscond.addTextChangedListener(MyTextWatcher("IdAsscond"))
        binding.idAssetAvailability.addTextChangedListener(MyTextWatcher("idAssetAvailability"))
        binding.idCurrentLocation.addTextChangedListener(MyTextWatcher("idCurrentLocation"))
        binding.idExistingBarCode.addTextChangedListener(MyTextWatcher("idExistingBarCode"))
        binding.idBarCodeNumber.addTextChangedListener(MyTextWatcher("idBarCodeNumber"))
        binding.idNoOfContract.addTextChangedListener(MyTextWatcher("idNoOfContract"))
        binding.idNoOfAssetmaximo.addTextChangedListener(MyTextWatcher("idNoOfAssetmaximo"))
        binding.idRemarkNew.addTextChangedListener(MyTextWatcher("idRemarkNew"))
        binding.idMaximoAssetInfo.addTextChangedListener(MyTextWatcher("idMaximoAssetInfo"))
        binding.idOwnedBy.addTextChangedListener(MyTextWatcher("idOwnedBy"))
        binding.idOperatedBy.addTextChangedListener(MyTextWatcher("idOperatedBy"))
        binding.idMaintainedBy.addTextChangedListener(MyTextWatcher("idMaintainedBy"))
        binding.idNouneModifierRemark.addTextChangedListener(MyTextWatcher("idNouneModifierRemark"))


        mPVData.assetImages.namePlateText!!.forEach {
            if (binding.namePlateOneTvValue.text.toString().isEmpty()) {
                binding.namePlateOneTvValue.text = it
            } else {
                binding.namePlateOneTvValue.text =
                    binding.namePlateOneTvValue.text.toString() + "\n" + it
            }
        }

        mPVData.assetImages.namePlateTextTwo!!.forEach {
            if (binding.namePlateTwoTvValue.text.toString().isEmpty()) {
                binding.namePlateTwoTvValue.text = it
            } else {
                binding.namePlateTwoTvValue.text =
                    binding.namePlateTwoTvValue.text.toString() + "\n" + it
            }
        }

//        loadRecyclerView()

    }

    val numberArray: ArrayList<NumberData> = ArrayList()
    var adapter: NumberAdapter? = null

    private fun initRecyclerView() {

        numberArray.clear()

        var itrater: Int = 0

        mPVData.bomModel.forEach {
            val data = NumberData()
            data.number = (itrater + 1).toString()

            numberArray.add(data)
            itrater += 1
        }

        if (!numberArray.isNullOrEmpty()) {
            numberArray[numberArray.size - 1].let {
                it.isSelected = true
            }

            val horizontalLayoutManagaer =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            horizontalLayoutManagaer.setReverseLayout(true);
            binding.rcyPagerCount.layoutManager = horizontalLayoutManagaer
            adapter = NumberAdapter(numberArray, this, this)
            binding.rcyPagerCount.adapter = adapter
        }

        updateRecyclerView()


    }


    private fun setViewClick() {


        binding.idClear.setOnClickListener {


            binding.oldTagId.text = ""
            mPVData.oldTagNo = ""
        }


        binding.idClearNewTag.setOnClickListener {

            binding.newTagId.text = ""
            mPVData.newTagNo = ""

        }


        binding.locationClear.setOnClickListener {
            binding.longitudeValue.setText("")
            binding.latitudeValue.setText("")
        }



        val commonTouchListener = View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                v.parent.requestDisallowInterceptTouchEvent(true)
            }
            v.onTouchEvent(event)
        }

        binding.idRemark.setOnTouchListener(commonTouchListener)
        binding.remark.setOnTouchListener(commonTouchListener)
        binding.idNouneModifierRemark.setOnTouchListener(commonTouchListener)
        binding.idRemarkNew.setOnTouchListener(commonTouchListener)



        binding.idFarID.setOnClickListener {
            if (iStockApplication.appPreference.KEY_All_Master.equals("")) {
                masterViewModel.getAllMaster()
            } else {
                var modalBottomSheet: ChooseMassterBottomsheet? = null

                modalBottomSheet = ChooseMassterBottomsheet(object : ItemSelectedForNoune {
                    override fun selectedItem(noun: String) {

                        val splittedArray = noun.split("##")

                        binding.idFarID.text = splittedArray[0]
                        mPVData.fixedAssetNo = splittedArray[0]

                        modalBottomSheet?.dismiss()
                    }
                })

                modalBottomSheet?.show(
                    supportFragmentManager,
                    FunctionalLocationChooseBottomsheet.TAG
                )
            }
        }


        binding.idLocationHierarchy.setOnClickListener {

            if (iStockApplication.appPreference.LocationHierarchy.equals("")) {
                binding.progressbar.visibility = View.VISIBLE
                locationViewModel.getAllLocationHierarchy()
            } else {
                var modalBottomSheet: LocationHierarchyBottomsheet? = null
                modalBottomSheet = LocationHierarchyBottomsheet(object : ItemSelectedForNoune {
                    override fun selectedItem(noun: String) {
                        mPVData.locationHierarchy = noun
                        binding.idLocationHierarchy.text = noun
                        modalBottomSheet?.dismiss()
                    }
                })

                modalBottomSheet?.show(
                    supportFragmentManager,
                    FunctionalLocationChooseBottomsheet.TAG
                )
            }

        }


        binding.idClasificationHierarchyDes.setOnClickListener {

            if (iStockApplication.appPreference.AssetTypeMaster.equals("")) {
                binding.progressbar.visibility = View.VISIBLE
                classificationViewModel.getAllLocationHierarchy()
            } else {
                var modalBottomSheet: ClassificationHierarchyBottomsheet? = null
                modalBottomSheet =
                    ClassificationHierarchyBottomsheet(object : ItemSelectedForNoune {
                        override fun selectedItem(noun: String) {
                            mPVData.classificationHierarchyDesc = noun
                            binding.idClasificationHierarchyDes.text = noun
                            modalBottomSheet?.dismiss()
                        }
                    })

                modalBottomSheet?.show(
                    supportFragmentManager,
                    FunctionalLocationChooseBottomsheet.TAG
                )
            }

        }


        binding.IdStatus.setOnClickListener {

            if (iStockApplication.appPreference.KEY_SITE_MAster.equals("")) {
                binding.progressbar.visibility = View.VISIBLE
                siteViewModel.getAllSiteMaster()
            } else {
                var modalBottomSheet: ChooseSiteBottomsheet? = null
                modalBottomSheet = ChooseSiteBottomsheet(object : ItemSelectedForNoune {
                    override fun selectedItem(noun: String) {
                        mPVData.status = noun
                        binding.IdStatus.text = noun
                        modalBottomSheet?.dismiss()
                    }
                })

                modalBottomSheet?.show(
                    supportFragmentManager,
                    FunctionalLocationChooseBottomsheet.TAG
                )
            }
        }

        binding.itemClick.setOnClickListener {

            val bomModel = BOMModel()
            bomModel.uniqueId = System.currentTimeMillis().toString()
            bomModel.equipmentId = mPVData.uniqueId
            mPVData.bomModel.add(bomModel)

//            mPVData.bomModel.add(BOMModel())
            initRecyclerView()
            bomPageAdapter.updateAdapter(mPVData.bomModel.size)
            binding.viewPager.currentItem = mPVData.bomModel.size - 1
        }


        binding.newTagScanner.setOnClickListener {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(context, object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                binding.newTagId.text = action
                                mPVData.newTagNo = action
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


        binding.scanImage.setOnClickListener {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(context, object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                binding.oldTagId.text = action
                                mPVData.oldTagNo = action
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



        binding.btnSave.setOnClickListener {
            saveDataToLocal()
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.firstDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '1') {
                dropDownVisibility.set('1')
            } else {
                dropDownVisibility.set('0')
            }

        }

        binding.secondDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '2') {
                dropDownVisibility.set('2')
            } else {
                dropDownVisibility.set('0')
            }

        }

        binding.thirdDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '3') {
                dropDownVisibility.set('3')
            } else {
                dropDownVisibility.set('0')
            }
        }

        binding.fourthDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '4') {
                dropDownVisibility.set('4')
            } else {
                dropDownVisibility.set('0')
            }
        }

        binding.fifthDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '5') {
                dropDownVisibility.set('5')
            } else {
                dropDownVisibility.set('0')
            }
        }

//        binding.sixthDropDown.setOnClickListener {
//            if (dropDownVisibility.get() != '6') {
//                dropDownVisibility.set('6')
//            } else {
//                dropDownVisibility.set('0')
//            }
//        }

        binding.equipmentDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '7') {
                dropDownVisibility.set('7')
            } else {
                dropDownVisibility.set('0')
            }
        }

        binding.nownModifierDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '8') {
                dropDownVisibility.set('8')
            } else {
                dropDownVisibility.set('0')
            }
        }
        binding.tarDropDown.setOnClickListener {
            if (dropDownVisibility.get() != '9') {
                dropDownVisibility.set('9')
            } else {
                dropDownVisibility.set('0')
            }
        }
        binding.additionalTab.setOnClickListener {
            if (dropDownVisibility.get() != '6') {
                dropDownVisibility.set('6')
            } else {
                dropDownVisibility.set('0')
            }
        }

        binding.locationIcon.setOnClickListener {
            if (currentLocation != null) {
                // Update UI with location data
                binding.latitudeValue.setText(LocationConverter.getLongitudeAsDMS(currentLocation, 3))
                binding.longitudeValue.setText(LocationConverter.getLatitudeAsDMS(currentLocation, 3))
            } else {
                val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                binding.latitudeValue.setText(LocationConverter.getLongitudeAsDMS(location, 3))
                binding.longitudeValue.setText(LocationConverter.getLatitudeAsDMS(location, 3))
                Toast.makeText(this,"Location Updated Successfully",Toast.LENGTH_SHORT).show()

            }
        }


        binding.idNoune.setOnClickListener {
            val tempList: List<AttributesItem> =
                mPVData.characteristics.filter { s -> s.definition != null }


            if (tempList.isNotEmpty()) {

                CommonUtils.showAlertWithCancel(
                    this,
                    "Do you want to clear the list ?",
                    object : CommonInterface {
                        override fun btnPositiveSelected(dialog: DialogInterface) {
                            dialog.dismiss()
                            showBottomSheet()

                        }

                        override fun btnNegativeSelected(dialog: DialogInterface) {
                            dialog.dismiss()
                        }
                    })
            } else {
                showBottomSheet()
            }

        }



        binding.idModifier.setOnClickListener {

            if (mPVData.noun.isNullOrEmpty()) {
                Toast.makeText(this, "Please select noun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getNounViewModel.getModifier(mPVData.noun ?: "").let {


                val tempList: ArrayList<AttributesItem> = ArrayList()

                it.let {


                    it.forEach {

                        val test = tempList.filter { s -> (s.modifier.equals(it.modifier)) }

                        if (test.isEmpty()) {
                            tempList.add(it)
                        }
                    }

                }


                var modalBottomSheet: ChooseModifierBottomsheet? = null
                modalBottomSheet = ChooseModifierBottomsheet("Modifier", tempList, object : ItemSelectedForNoune {
                    override fun selectedItem(noun: String) {
                        mPVData.modifier = noun
                        binding.idModifier.setText(noun)
                        modalBottomSheet?.dismiss()

                        getNounViewModel.getAttributes(
                            mPVData.noun ?: "",
                            mPVData.modifier ?: ""
                        )

                    }
                })

                modalBottomSheet?.show(
                    supportFragmentManager,
                    FunctionalLocationChooseBottomsheet.TAG
                )
            }
        }


    }



    private fun showBottomSheet() {
        nounList.let {

            var modalBottomSheet: ChooseNounBottomsheet? = null
            modalBottomSheet = ChooseNounBottomsheet("Noun", it, object : ItemSelectedForNoune {
                override fun selectedItem(noun: String) {
                    mPVData.noun = noun
//                        binding.idNoune.setText(noun)

                    mPVData.modifier = ""
                    mPVData.characteristics.clear()

                    updateData()

                    modalBottomSheet?.dismiss()
                }
            })

            modalBottomSheet?.show(
                supportFragmentManager,
                FunctionalLocationChooseBottomsheet.TAG
            )
        }

    }





    private fun saveDataToLocal() {
        if (mPVData.assetImages.assetImage.isNullOrEmpty()) {
            CommonUtils.showAlert(this, "Please add at least one asset image")
            return
        }


        if (!observableCorrosion.get()
                .equals('n') && mPVData.assetCondition.corrosionImage.size == 0
        ) {
            CommonUtils.showAlert(
                this,
                "Please add at least one Corrosion image"
            )
            return
        }

        if ((!observableTemperature.get()
                .equals('n')) && mPVData.assetCondition.temparatureImage.size == 0
        ) {

            CommonUtils.showAlert(
                this,
                "Please add at least one temperature image"
            )

            return
        }


        if ((!observableDamage.get().equals('n')) && mPVData.assetCondition.damageImage.size == 0) {
            CommonUtils.showAlert(
                this,
                "Please add at least one damage image"
            )
            return
        }


        if ((!observableLeakage.get()
                .equals('n')) && mPVData.assetCondition.leakageImage.size == 0
        ) {

            CommonUtils.showAlert(
                this,
                "Please add at least one leakage image"
            )

            return
        }

//        if (mPVData.gIS.lattitudeStart.isNullOrEmpty() || mPVData.gIS.longitudeStart.isNullOrEmpty()) {
//            CommonUtils.showAlert(
//                this,
//                "Please Enter location information"
//            )
//            return
//        }

        CommonUtils.showAlertWithCancel(
            this,
            "Item Saved successfully in local database",
            object : CommonInterface {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun btnPositiveSelected(dialog: DialogInterface) {

                    val gson = Gson()
                    val myType = object : TypeToken<UserDetailsResponse>() {}.type
                    val userDetailsResponse = gson.fromJson<UserDetailsResponse>(
                        iStockApplication.appPreference.KEY_USER_DETAILS, myType)


                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    val currentDate = sdf.format(Date())
                    System.out.println(" C DATE is  " + currentDate)

                    mPVData.isSaved = true
                    mPVData.mVerificateionDate = currentDate
                    mPVData.mVerifiedBy = userDetailsResponse.userid

                    var i = 0
                    var id = -1
                    for (x in iStockApplication.pvDataModel.data) {

                        if (x.assetNo.equals(mPVData.assetNo)) {
                            id = i
                            break
                        }

                        i += 1
                    }

                    if (id != -1) {
                        iStockApplication.pvDataModel.data.removeAt(id)
                    }

                    iStockApplication.saveData(iStockApplication.pvDataModel.data)
                    iStockApplication.saveCompletedItem(mPVData)
                    createExcelSheetEquipment(mPVData)
                    isneedtoSave = false
                    dialog.dismiss()
                    finish()
//                    onBackPressed()
                }

                override fun btnNegativeSelected(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createExcelSheetEquipment(vararg mPVData: PVData) {
        try {
            val today = LocalDate.now()
            val fileName = "Equipment${today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}.xls"

            val filePath = fileName
            val file = File(this.getExternalFilesDir(null), filePath)

            val workbook: WritableWorkbook
            val sheet: WritableSheet

            if (file.exists()) {
                workbook = Workbook.createWorkbook(file, Workbook.getWorkbook(file))
                sheet = workbook.getSheet(0)
            } else {
                // Create new workbook and sheet
                workbook = Workbook.createWorkbook(file)
                sheet = workbook.createSheet("Equipment Sheet", 0)

                val headerFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
                val headerFormat = WritableCellFormat(headerFont)
                headerFormat.setBackground(Colour.GRAY_25)

                for ((index, header) in headers.withIndex()) {
                    sheet.addCell(Label(index, 0, header, headerFormat))
                }
            }

            val dataFont = WritableFont(WritableFont.ARIAL, 10)
            val dataFormat = WritableCellFormat(dataFont)


            for (data in mPVData) {
                var row = -1

                for (rowIndex in 1 until sheet.rows) {
                    val cell: WritableCell = sheet.getWritableCell(0, rowIndex)
                    if (cell.contents == data.itemcode) {
                        row = rowIndex
                        break
                    }
                }

                // If no matching row is found, add a new row
                if (row == -1) {
                    row = sheet.rows
                }

                if (data.characteristics.isNotEmpty()) {
                    for (data1 in data.characteristics) {
                        sheet.addCell(Label(0, row, data.equipmentDesc, dataFormat))
                        sheet.addCell(Label(1, row, data.fixedAssetNo, dataFormat))
                        sheet.addCell(Label(2, row, data.siteId, dataFormat))
                        sheet.addCell(Label(3, row, data.assetNo, dataFormat))
                        sheet.addCell(Label(4, row, data.assCondition, dataFormat))
                        sheet.addCell(Label(5, row, data.equipmentDesc, dataFormat))
                        sheet.addCell(Label(6, row, data.parent, dataFormat))
                        sheet.addCell(Label(7, row, data.location, dataFormat))
                        sheet.addCell(Label(8, row, data.locationHierarchy, dataFormat))
                        sheet.addCell(Label(9, row, data.classificationHierarchyDesc, dataFormat))
                        sheet.addCell(Label(10, row, data.status, dataFormat))
                        sheet.addCell(Label(11, row, data.assetType, dataFormat))
                        sheet.addCell(Label(12, row, data.reportGroup, dataFormat))
                        sheet.addCell(Label(13, row, data.serialNo, dataFormat))
                        sheet.addCell(Label(14, row, data.modelNo, dataFormat))
                        sheet.addCell(Label(15, row, data.modelYear, dataFormat))
                        sheet.addCell(Label(16, row, data.manufacture, dataFormat))
                        sheet.addCell(Label(17, row, data.assetImages.assetImage.toString(), dataFormat))
                        sheet.addCell(Label(18, row, data.assetImages.matImgs.toString(), dataFormat))
                        sheet.addCell(Label(19, row, data.assetImages.namePlateImge.toString(), dataFormat))
                        val step1 = data.assetImages.namePlateText.toString().replace("\n", " ")
                        val normalText = step1.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")
                        sheet.addCell(Label(20, row,normalText, dataFormat))
                        sheet.addCell(Label(21, row, data.assetImages.namePlateImgeTwo.toString(), dataFormat))
                        val step2 = data.assetImages.namePlateTextTwo.toString().replace("\n", " ")
                        val normalText2 = step2.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")
                        sheet.addCell(Label(22, row, normalText2, dataFormat))
                        sheet.addCell(Label(23, row, data.remarks, dataFormat))
                        sheet.addCell(Label(24, row, data.mReworkRemarks, dataFormat))
                        sheet.addCell(Label(25, row, data.gIS.longitudeStart, dataFormat))
                        sheet.addCell(Label(26, row, data.gIS.lattitudeStart, dataFormat))
                        sheet.addCell(Label(27, row, data.assetImages.oldTagImage.toString(), dataFormat))
                        sheet.addCell(Label(28, row, data.oldTagNo, dataFormat))
                        sheet.addCell(Label(29, row, data.assetImages.newTagImage.toString(), dataFormat))
                        sheet.addCell(Label(30, row, data .newTagNo, dataFormat))
                        sheet.addCell(Label(31, row, data.availabilityofAsset, dataFormat))
                        sheet.addCell(Label(32, row, data.presentLocation, dataFormat))
                        sheet.addCell(Label(33, row, data.existingBarCode, dataFormat))
                        sheet.addCell(Label(34, row, data.barcodeNumber, dataFormat))
                        sheet.addCell(Label(35, row, data.remarks, dataFormat))
                        sheet.addCell(Label(36, row, data.mOwnedBy, dataFormat))
                        sheet.addCell(Label(37, row, data.mOperatedBy, dataFormat))
                        sheet.addCell(Label(38, row, data.mMaintainer, dataFormat))
                        sheet.addCell(Label(39, row, data.assetCondition.corrosion, dataFormat))
                        sheet.addCell(Label(40, row, data.assetCondition.damage, dataFormat))
                        sheet.addCell(Label(41, row, data.assetCondition.leakage, dataFormat))
                        sheet.addCell(Label(42, row, data.assetCondition.vibration, dataFormat))
                        sheet.addCell(Label(43, row, data.assetCondition.temparature, dataFormat))
                        sheet.addCell(Label(44, row, data.assetCondition.smell, dataFormat))
                        sheet.addCell(Label(45, row, data.assetCondition.noise, dataFormat))
                        sheet.addCell(Label(46, row, data.assetConditionRemarks, dataFormat))
                        sheet.addCell(Label(47, row, data.assetCondition.corrosionImage.toString(), dataFormat))
                        sheet.addCell(Label(48, row, data.assetCondition.damageImage.toString(), dataFormat))
                        sheet.addCell(Label(49, row, data.assetCondition.leakageImage.toString(), dataFormat))
                        sheet.addCell(Label(50, row, data.assetCondition.temparatureImage.toString(), dataFormat))
                        row++
                    }
                }else{
                    sheet.addCell(Label(0, row, data.equipmentDesc, dataFormat))
                    sheet.addCell(Label(1, row, data.fixedAssetNo, dataFormat))
                    sheet.addCell(Label(2, row, data.siteId, dataFormat))
                    sheet.addCell(Label(3, row, data.assetNo, dataFormat))
                    sheet.addCell(Label(4, row, data.assCondition, dataFormat))
                    sheet.addCell(Label(5, row, data.equipmentDesc, dataFormat))
                    sheet.addCell(Label(6, row, data.parent, dataFormat))
                    sheet.addCell(Label(7, row, data.location, dataFormat))
                    sheet.addCell(Label(8, row, data.locationHierarchy, dataFormat))
                    sheet.addCell(Label(9, row, data.classificationHierarchyDesc, dataFormat))
                    sheet.addCell(Label(10, row, data.status, dataFormat))
                    sheet.addCell(Label(11, row, data.assetType, dataFormat))
                    sheet.addCell(Label(12, row, data.reportGroup, dataFormat))
                    sheet.addCell(Label(13, row, data.serialNo, dataFormat))
                    sheet.addCell(Label(14, row, data.modelNo, dataFormat))
                    sheet.addCell(Label(15, row, data.modelYear, dataFormat))
                    sheet.addCell(Label(16, row, data.manufacture, dataFormat))
                    sheet.addCell(Label(17, row, data.assetImages.assetImage.toString(), dataFormat))
                    sheet.addCell(Label(18, row, data.assetImages.matImgs.toString(), dataFormat))
                    sheet.addCell(Label(19, row, data.assetImages.namePlateImge.toString(), dataFormat))
                    val step1 = data.assetImages.namePlateText.toString().replace("\n", " ")
                    val normalText = step1.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")
                    sheet.addCell(Label(20, row,normalText, dataFormat))
                    sheet.addCell(Label(21, row, data.assetImages.namePlateImgeTwo.toString(), dataFormat))
                    val step2 = data.assetImages.namePlateTextTwo.toString().replace("\n", " ")
                    val normalText2 = step2.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")
                    sheet.addCell(Label(22, row, normalText2, dataFormat))
                    sheet.addCell(Label(23, row, data.remarks, dataFormat))
                    sheet.addCell(Label(24, row, data.mReworkRemarks, dataFormat))
                    sheet.addCell(Label(25, row, data.gIS.longitudeStart, dataFormat))
                    sheet.addCell(Label(26, row, data.gIS.lattitudeStart, dataFormat))
                    sheet.addCell(Label(27, row, data.assetImages.oldTagImage.toString(), dataFormat))
                    sheet.addCell(Label(28, row, data.oldTagNo, dataFormat))
                    sheet.addCell(Label(29, row, data.assetImages.newTagImage.toString(), dataFormat))
                    sheet.addCell(Label(30, row, data.newTagNo, dataFormat))
                    sheet.addCell(Label(31, row, data.availabilityofAsset, dataFormat))
                    sheet.addCell(Label(32, row, data.presentLocation, dataFormat))
                    sheet.addCell(Label(33, row, data.existingBarCode, dataFormat))
                    sheet.addCell(Label(34, row, data.barcodeNumber, dataFormat))
                    sheet.addCell(Label(35, row, data.remarks, dataFormat))
                    sheet.addCell(Label(36, row, data.mOwnedBy, dataFormat))
                    sheet.addCell(Label(37, row, data.mOperatedBy, dataFormat))
                    sheet.addCell(Label(38, row, data.mMaintainer, dataFormat))
                    sheet.addCell(Label(39, row, data.assetCondition.corrosion, dataFormat))
                    sheet.addCell(Label(40, row, data.assetCondition.damage, dataFormat))
                    sheet.addCell(Label(41, row, data.assetCondition.leakage, dataFormat))
                    sheet.addCell(Label(42, row, data.assetCondition.vibration, dataFormat))
                    sheet.addCell(Label(43, row, data.assetCondition.temparature, dataFormat))
                    sheet.addCell(Label(44, row, data.assetCondition.smell, dataFormat))
                    sheet.addCell(Label(45, row, data.assetCondition.noise, dataFormat))
                    sheet.addCell(Label(46, row, data.assetConditionRemarks, dataFormat))
                    sheet.addCell(Label(47, row, data.assetCondition.corrosionImage.toString(), dataFormat))
                    sheet.addCell(Label(48, row, data.assetCondition.damageImage.toString(), dataFormat))
                    sheet.addCell(Label(49, row, data.assetCondition.leakageImage.toString(), dataFormat))
                    sheet.addCell(Label(50, row, data.assetCondition.temparatureImage.toString(), dataFormat))
                    row++
                }
            }

            for (col in headers.indices) {
                sheet.setColumnView(col, getPreferredColumnWidth(sheet, col))
            }

            val contentUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)
            if (!uriArrayList.contains(contentUri)) {
                uriArrayList.add(contentUri)
            }

//            val uris = session.getUriArrayListEquipment("excel_equipment")
//            if (uris != null) {
//                if (!uris.contains(contentUri)) {
//                    uris.add(contentUri)
//                    session.setUriArrayListEquipment("excel_equipment", uris)
//                }
//            } else {
//                val newList = mutableListOf(contentUri)
//                session.setUriArrayListEquipment("excel_equipment", newList)
//            }


            val date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val uris = session.getUriArrayListEquipmentNew("excel_equipment")

            if (uris != null) {
                if (uris.none { it.uri == contentUri.toString() }) {
                    uris.add(UriWithDate(contentUri.toString(), date))
                    session.setUriArrayListEquipmentNew("excel_equipment", uris)
                }
            }else{
                val newList = mutableListOf(UriWithDate(contentUri.toString(), date))
                session.setUriArrayListEquipmentNew("excel_equipment", newList)
            }

            workbook.write()
            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getPreferredColumnWidth(sheet: jxl.Sheet, col: Int): Int {
        var maxWidth = 0
        val numRows = sheet.rows
        for (row in 0 until numRows) {
            val cell = sheet.getCell(col, row)
            val contentWidth = cell.contents.length * 256  // 1 character = 256 units
            maxWidth = maxOf(maxWidth, contentWidth)
        }
        return (maxWidth / 256) + 1  // Convert back from units to characters and add a little extra padding
    }

    fun chooseGalleryOrCameraBottomSheet(open: String) {
        val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(open, this)
        bottomSheetFilterDialogFragment.show(
            supportFragmentManager,
            "bottomSheetFrag"
        )
    }

    fun openBottomSheet(open: String) {

        VerificationDetailsActivity.mPVData = mPVData

        var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
        modalBottomSheet =
            data?.let {
                FunctionalLocationChooseBottomsheet(open, it, object :
                    FuntionalLocationFragment.itemSelected {
                    override fun selectedItem(id: String, code: String, name: String) {
                        modalBottomSheet?.dismiss()

                        when (open) {
                            assetRegion -> {
                                mPVData.region = id
                                binding.textView.setText(name)
                            }

                            assetCity -> {
                                mPVData.city = id
                                binding.citytextView.setText(name)
                            }

                            assetArea -> {
                                mPVData.area = id
                                binding.areatextView.setText(name)
                            }

                            assetSubArea -> {
                                mPVData.subArea = id
                                binding.subareatextView.setText(name)
                            }

                            assetMinorClass -> {
                                mPVData.minorClass = id
                                binding.functiontextView.setText(name)
                            }

                            assetSubSystemIdentifier -> {
                                mPVData.identifier = id
                                binding.subsystemtextView.setText(name)
                            }

                            flocCode -> {
                                binding.locationtv.setText(name)
                                mPVData.functionalLoc = id
                            }

                            assetEquipmentType -> {
                                mPVData.equipmentType = id
                                binding.Equipment.setText(name)
                            }

                            assetEquipmentClassFunctional -> {
                                mPVData.equipmentClass = id
                                binding.equipmenttv.setText(name)
                            }
                        }

                    }
                })
            }

        modalBottomSheet?.show(supportFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
    }

    @SuppressLint("MissingPermission")
    fun requestlocation() {

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                locationListenerGPS
            )
        }
        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                locationListenerGPS
            )
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }
        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }
    }



    private var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            val latitude = location.latitude
            val longitude = location.longitude
            val msg = "New Latitude: " + latitude + "New Longitude: " + longitude
//            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}


    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(this)){
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        }else{
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.internet_alert_color))
        }
        requestlocation()
    }

    private fun isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle("Enable Location")
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.")
            alertDialog.setPositiveButton("Location Settings",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                })
            alertDialog.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            val alert: AlertDialog = alertDialog.create()
            alert.show()
        } else {
//            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
//            alertDialog.setTitle("Confirm Location")
//            alertDialog.setMessage("Your Location is enabled, please enjoy")
//            alertDialog.setNegativeButton("Back to interface",
//                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
//            val alert: AlertDialog = alertDialog.create()
//            alert.show()
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListenerGPS)
    }


    override fun onBackPressed() {

        if (isneedtoSave) {

            iStockApplication.saveNonCompletedItem(mPVData)
        }

        super.onBackPressed()
    }

    class MyTextWatcher(val type: String) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            if (type.equals("lat1")) {
                mPVData.gIS?.lattitudeStart = p0.toString()
            }


            if (type.equals("lan1")) {
                mPVData.gIS?.longitudeStart = p0.toString()
            }


            if (type.equals("lat2")) {
                mPVData.gIS?.lattitudeEnd = p0.toString()
            }

            if (type.equals("lan2")) {
                mPVData.gIS?.longitudeEnd = p0.toString()
            }

            if (type.equals("descriptionedt")) {
                mPVData.equipmentDesc = p0.toString()
            }

            if (type.equals("idRemark")) {
                mPVData.cRemarks = p0.toString()
            }

            if (type.equals("remark")) {
                mPVData.assetConditionRemarks = p0.toString()
            }

            if (type.equals("idModelNo")) {
                mPVData.modelNo = p0.toString()
            }

            if (type.equals("idEquipName")) {
                mPVData.equipment.name = p0.toString()
            }

            if (type.equals("idSiteId")) {
                mPVData.siteId = p0.toString()
            }

            if (type.equals("idAssetNum")) {
                mPVData.pvassetNo = p0.toString()
            }

            if (type.equals("idDescriptionTar")) {
                mPVData.equipmentDesc = p0.toString()
            }
            if (type.equals("idMake")) {
                mPVData.manufacture = p0.toString()
            }

            if (type.equals("idParent")) {
                mPVData.parent = p0.toString()
            }
            if (type.equals("idLocation")) {
                mPVData.location = p0.toString()
            }

            if (type.equals("IdAssetType")) {
                mPVData.assetType = p0.toString()
            }

            if (type.equals("IdReportGroup")) {
                mPVData.reportGroup = p0.toString()
            }

            if (type.equals("IdSerialNum")) {
                mPVData.serialNo = p0.toString()
            }

            if (type.equals("idModelNum")) {
                mPVData.modelNo = p0.toString()
            }

            if (type.equals("idModelYear")) {
                mPVData.modelYear = p0.toString()
            }

//            if (type.equals("IdAsscond")) {
//                mPVData.assCondition = p0.toString()
//            }

            if (type.equals("idAssetAvailability")) {
                mPVData.availabilityofAsset = p0.toString()
            }

            if (type.equals("idExistingBarCode")) {
                mPVData.existingBarCode = p0.toString()
            }
            if (type.equals("idNoOfAssetmaximo")) {
                mPVData.nosOfAsestMaximo = p0.toString()
            }
            if (type.equals("idMaximoAssetInfo")) {
                mPVData.maximoAssetInfo = p0.toString()
            }

            if (type.equals("idMaintainedBy")) {
                mPVData.mMaintainer = p0.toString()
            }

            if (type.equals("idNouneModifierRemark")) {
                mPVData.mReworkRemarks = p0.toString()
            }
            if (type.equals("idOperatedBy")) {
                mPVData.mOperatedBy = p0.toString()
            }

            if (type.equals("idOwnedBy")) {
                mPVData.mOwnedBy = p0.toString()
            }

            if (type.equals("idRemarkNew")) {
                mPVData.remarks = p0.toString()
            }

            if (type.equals("idNoOfContract")) {
                mPVData.noOfContract = p0.toString()
            }

            if (type.equals("idBarCodeNumber")) {
                mPVData.barcodeNumber = p0.toString()
            }

            if (type.equals("idCurrentLocation")) {
                mPVData.presentLocation = p0.toString()
            }

            if (type.equals("idEquipModelNo")) {
                mPVData.equipment.modelno = p0.toString()
            }
            if (type.equals("idEquipManufacture")) {
                mPVData.equipment.manufacturer = p0.toString()
            }

            if (type.equals("idEquipTagNo")) {
                mPVData.equipment.tagno = p0.toString()
            }

            if (type.equals("idSerialNo")) {
                mPVData.equipment.serialno = p0.toString()
            }

            if (type.equals("idEquipAdditionalInfo")) {
                mPVData.equipment.additionalinfo = p0.toString()
            }

            if (type.equals("idManufacturerName")) {
                mPVData.vendorsuppliers.manufacture = p0.toString()
            }
            if (type.equals("idParthNo")) {
                mPVData.vendorsuppliers.partNo = p0.toString()
            }

            if (type.equals("idModelNo")) {
                mPVData.vendorsuppliers.modelNo = p0.toString()
            }

        }

    }

    fun functionalClick(condition: Char) {
        when (condition) {
            'f' -> {
                mFunctional.set('f')
            }

            'n' -> {
                mFunctional.set('n')
            }

            's' -> {
                mFunctional.set('s')
            }
        }

        setData()
    }

    fun assetConditionClick(condition: String) {

        when (condition) {
            "ch" -> {
                observableCorrosion.set('h')
            }

            "cm" -> {
                observableCorrosion.set('m')
            }

            "cn" -> {
                observableCorrosion.set('n')
            }

            "dh" -> {
                observableDamage.set('h')
            }

            "dm" -> {
                observableDamage.set('m')
            }

            "dn" -> {
                observableDamage.set('n')
            }

            "lh" -> {
                observableLeakage.set('h')
            }

            "lm" -> {
                observableLeakage.set('m')
            }

            "ln" -> {
                observableLeakage.set('n')
            }

            "vh" -> {
                observableVibration.set('h')
            }

            "vm" -> {
                observableVibration.set('m')
            }

            "vn" -> {
                observableVibration.set('n')
            }

            "th" -> {
                observableTemperature.set('h')
            }

            "tm" -> {
                observableTemperature.set('m')
            }

            "tn" -> {
                observableTemperature.set('n')
            }

            "sh" -> {
                observableSmell.set('h')
            }

            "sm" -> {
                observableSmell.set('m')
            }

            "sn" -> {
                observableSmell.set('n')
            }

            "nh" -> {
                observableNoise.set('h')
            }

            "nm" -> {
                observableNoise.set('m')
            }

            "nn" -> {
                observableNoise.set('n')
            }

        }

        calculateRank()
    }

    private fun setData() {
        if (observableCorrosion.get().equals('h')) {
            mPVData.assetCondition?.corrosion = "High"
        } else if (observableCorrosion.get().equals('m')) {
            mPVData.assetCondition?.corrosion = "Medium"
        } else {
            mPVData.assetCondition?.corrosion = "No"
        }

        if (observableLeakage.get().equals('h')) {
            mPVData.assetCondition?.leakage = "High"
        } else if (observableLeakage.get().equals('m')) {
            mPVData.assetCondition?.leakage = "Medium"
        } else {
            mPVData.assetCondition?.leakage = "No"
        }


        if (observableDamage.get().equals('h')) {
            mPVData.assetCondition?.damage = "High"
        } else if (observableDamage.get().equals('m')) {
            mPVData.assetCondition?.damage = "Medium"
        } else {
            mPVData.assetCondition?.damage = "No"
        }

        if (observableNoise.get().equals('h')) {
            mPVData.assetCondition?.noise = "High"
        } else if (observableNoise.get().equals('m')) {
            mPVData.assetCondition?.noise = "Medium"
        } else {
            mPVData.assetCondition?.noise = "No"
        }


        if (observableVibration.get().equals('h')) {
            mPVData.assetCondition?.vibration = "High"
        } else if (observableVibration.get().equals('m')) {
            mPVData.assetCondition?.vibration = "Medium"
        } else {
            mPVData.assetCondition?.vibration = "No"
        }

        if (observableSmell.get().equals('h')) {
            mPVData.assetCondition.smell = "High"
        } else if (observableSmell.get().equals('m')) {
            mPVData.assetCondition.smell = "Medium"
        } else {
            mPVData.assetCondition.smell = "No"
        }

        if (observableTemperature.get().equals('h')) {
            mPVData.assetCondition.temparature = "High"
        } else if (observableTemperature.get().equals('m')) {
            mPVData.assetCondition.temparature = "Medium"
        } else {
            mPVData.assetCondition.temparature = "No"
        }

//        if (mFunctional.get().equals('f')) {
//            mPVData.assetCondition.assetCondition = "Functional"
//        } else if (mFunctional.get().equals('s')) {
//            mPVData.assetCondition.assetCondition = "standby"
//        } else {
//            mPVData.assetCondition.assetCondition = "Non Functional"
//        }


        if (mFunctional.get().equals('f')) {
            mPVData.idleOperational = "Operational"
        } else if (mFunctional.get().equals('n')) {
            mPVData.idleOperational = "Idle"
        }
    }


    private fun calculateRank() {

        if (observableCorrosion.get().equals('h') || observableDamage.get().equals('h')
            || observableLeakage.get().equals('h') || observableVibration.get()
                .equals('h') || observableTemperature.get().equals('h')
            || observableSmell.get().equals('h') || observableSmell.get().equals('h')
        ) {

            mPVData.assetCondition.rank = "5"
            setData()
            return
        }

        if (observableCorrosion.get().equals('n') && observableDamage.get().equals('n')
            && observableLeakage.get().equals('n') && observableVibration.get().equals('n')
            && observableTemperature.get().equals('n') && observableSmell.get().equals('n')
            && observableSmell.get().equals('n')
        ) {
            mPVData.assetCondition.rank = "1"
            setData()
            return
        }

        var i = 0

        if (observableCorrosion.get().equals('m')) {
            i += 1
        }

        if (observableDamage.get().equals('m')) {
            i += 1
        }

        if (observableLeakage.get().equals('m')) {
            i += 1
        }
        if (observableVibration.get().equals('m')) {
            i += 1
        }

        if (observableTemperature.get().equals('m')) {
            i += 1
        }
        if (observableSmell.get().equals('m')) {
            i += 1
        }
        if (observableSmell.get().equals('m')) {
            i += 1
        }

        if (i == 1) {
            mPVData.assetCondition.rank = "2"
        } else if (i in 2..3) {
            mPVData.assetCondition.rank = "3"
        } else if (i > 3) {
            mPVData.assetCondition.rank = "4"
        }

        setData()
    }


    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"

        activityLauncher.launch(intent)
//        startActivityForResult(intent, CHOOSE_PHOTO)
    }


    var capturedImage: File? = null

    val REQUEST_IMAGE_CAPTURE_VIDEO = 100

    private fun dispatchTakeVideoIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        try {
            videoLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    fun getVideoThumbnail(
        context: Context,
        videoUri: Uri,
        imageWidth: Int,
        imageHeight: Int
    ): Bitmap? {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT < 27) {
            val picturePath = getPath(context, videoUri!!)
            Log.i("TAG", "picturePath $picturePath")
            if (picturePath == null) {
                val mMMR = MediaMetadataRetriever()
                mMMR.setDataSource(context, videoUri)
                bitmap = mMMR.frameAtTime
            } else {
                bitmap = ThumbnailUtils.createVideoThumbnail(
                    picturePath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
            }
        } else {
            val mMMR = MediaMetadataRetriever()
            mMMR.setDataSource(context, videoUri)
            bitmap = mMMR.getScaledFrameAtTime(
                -1,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                imageWidth,
                imageHeight
            )
        }

        return bitmap
    }

    fun saveVideoToAppScopeStorage(context: Context, videoUri: Uri?, mimeType: String?): File? {
        if (videoUri == null || mimeType == null) {
            return null
        }

        val fileName = "capturedVideo.${mimeType.substring(mimeType.indexOf("/") + 1)}"

        val inputStream = context.contentResolver.openInputStream(videoUri)
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), fileName)
        file.deleteOnExit()
        file.createNewFile()
        val out = FileOutputStream(file)
        val bos = BufferedOutputStream(out)

        val buf = ByteArray(1024)
        inputStream?.read(buf)
        do {
            bos.write(buf)
        } while (inputStream?.read(buf) !== -1)

        //out.close()
        bos.close()
        inputStream.close()

        return file
    }

    fun getPath(context: Context, uri: Uri): String? {

        // check here to KITKAT or new version
        //val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (/*isKitKat && */DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    context, contentUri, selection,
                    selectionArgs
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is <span id="IL_AD2" class="IL_AD">useful</span> for MediaStore Uris, and other file-based
     * ContentProviders.
     *
     * @param context
     * The context.
     * @param uri
     * The Uri to query.
     * @param selection
     * (Optional) Filter used in the query.
     * @param selectionArgs
     * (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.getContentResolver().query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
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
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", capturedImage!!)
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        activityLauncher.launch(intent)
//        startActivityForResult(intent, CAPTURE_PHOTO)
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

        if (action.equals(CAPTUREVIDEO)) {

            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        dispatchTakeVideoIntent()
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
        val fname = mPVData.assetNo + "_$ts" + ".png" // need
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
            mPVData.assetImages.namePlateText!!.add(tempText)
            binding.namePlateOneTvValue.text =
                binding.namePlateOneTvValue.text.toString().trim() + "\n" + tempText
        } else if (mAction.equals(NAMEPLATE1)) {
            mPVData.assetImages.namePlateTextTwo!!.add(tempText)
            binding.namePlateTwoTvValue.text =
                binding.namePlateTwoTvValue.text.toString().trim() + "\n" + tempText
        }

    }

    lateinit var bomPageAdapter: BOMPageAdapter


    private fun initViewPager() {

        if (mPVData.bomModel.size == 0) {
            val bomModel = BOMModel()
            bomModel.uniqueId = System.currentTimeMillis().toString()
            bomModel.equipmentId = mPVData.uniqueId
            mPVData.bomModel.add(bomModel)
        }

        bomPageAdapter =
            BOMPageAdapter(supportFragmentManager, mPVData.bomModel.size)

        binding.viewPager.adapter = bomPageAdapter
    }


    private fun observeViewModel() {

        siteViewModel.showError.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            binding.progressbar.visibility = View.GONE
        })


        siteViewModel.showSuccess.observe(this) {
            binding.progressbar.visibility = View.GONE
            binding.IdStatus.performClick()
        }

        locationViewModel.showError.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            binding.progressbar.visibility = View.GONE
        })


        locationViewModel.showSuccess.observe(this) {
            binding.progressbar.visibility = View.GONE
            binding.idLocationHierarchy.performClick()
        }

        classificationViewModel.showError.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            binding.progressbar.visibility = View.GONE
        })


        classificationViewModel.showSuccess.observe(this) {
            binding.progressbar.visibility = View.GONE
            binding.idClasificationHierarchyDes.performClick()
        }

        lifecycleScope.launch {
            getNounViewModel.getAllDictionary().collectLatest {
                nounList.clear()
                nounList.addAll(it)

                if (it.isEmpty()) {
                    getNounViewModel.getDictionary()
                }
            }




        }

        viewModel.showSuccess.observe(this, Observer { countries ->
            countries?.let {
//                data = it
                updateData()
            }
        })
        viewModel.showError.observe(this, Observer { isError ->
            Toast.makeText(this, isError, Toast.LENGTH_SHORT).show()
            updateData()
        })

        viewModel.showLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })

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

                    mPVData.characteristics.clear()
                    mPVData.characteristics.addAll(it)

                    updateRecyclerView()

                    getNounViewModel.showSuccessAttributes.postValue(ArrayList())
                }


            }
        })


        getNounViewModel.showError.observe(this, Observer { isError ->
            Toast.makeText(this, isError, Toast.LENGTH_SHORT).show()
        })

        getNounViewModel.showLoading.observe(this, Observer { isLoading ->

            if (isLoading){
                binding.progressbar.visibility = View.VISIBLE
            }else{

                if (iStockApplication.appPreference.LocationHierarchy.equals("")) {
                    getNounViewModel.getAllLocationHierarchy()
                }else if (iStockApplication.appPreference.AssetTypeMaster.equals("")) {
                    getNounViewModel.getAllClasificationHierarchy()
                }else if (iStockApplication.appPreference.KEY_All_Master.equals("")) {
                    getNounViewModel.getAllMasterDetail()
                }else{
                    binding.progressbar.visibility = View.GONE
                }

            }

        })

    }


    private fun updateRecyclerView() {

        mPVData.characteristics?.let {
            binding.idAttribute.layoutManager = LinearLayoutManager(this)
            binding.idAttribute.adapter = AttributesAdapter(it, "mobile")
        }

    }


    fun loadRecyclerView(action: String) {

        val horizontalLayoutManagaer =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        when (action) {
            ASSETS -> {
                binding.rcyAssets.layoutManager = horizontalLayoutManagaer
                binding.rcyAssets.adapter = CustomAdapter(
                    mPVData.assetImages.assetImage,
                    this
                )

            }

            ASSETSMAXIMO -> {

                binding.rcyAssets1.layoutManager = horizontalLayoutManagaer
                binding.rcyAssets1.adapter = CustomAdapter(
                    mPVData.assetImages.matImgs,
                    this
                )

            }

            NAMEPLATE -> {
                binding.rcyNamePlate.layoutManager = horizontalLayoutManagaer
                binding.rcyNamePlate.adapter = CustomAdapter(
                    mPVData.assetImages.namePlateImge!!,
                    this
                )

            }

            NAMEPLATE1 -> {
                binding.rcyNamePlate1.layoutManager = horizontalLayoutManagaer
                binding.rcyNamePlate1.adapter = CustomAdapter(
                    mPVData.assetImages.namePlateImgeTwo,
                    this
                )

            }

            addOldTag -> {
                binding.rcyOldTag.layoutManager = horizontalLayoutManagaer
                binding.rcyOldTag.adapter = CustomAdapter(
                    mPVData.assetImages.oldTagImage!!,
                    this
                )

            }

            addnewTag -> {
                binding.rcynewTag.layoutManager = horizontalLayoutManagaer
                binding.rcynewTag.adapter = CustomAdapter(
                    mPVData.assetImages.newTagImage,
                    this
                )

            }

            captureCorrosion -> {
                binding.rcyCorrosion.layoutManager = horizontalLayoutManagaer
                binding.rcyCorrosion.adapter = CustomAdapter(
                    mPVData.assetCondition.corrosionImage,
                    this
                )

            }

            captureDamage -> {
                binding.rcyDamage.layoutManager = horizontalLayoutManagaer
                binding.rcyDamage.adapter = CustomAdapter(
                    mPVData.assetCondition.damageImage,
                    this
                )

            }

            captureLeakage -> {
                binding.rcyLeakage.layoutManager = horizontalLayoutManagaer
                binding.rcyLeakage.adapter = CustomAdapter(
                    mPVData.assetCondition.leakageImage,
                    this
                )

            }

            captureTemprature -> {
                binding.rcyTemparature.layoutManager = horizontalLayoutManagaer
                binding.rcyTemparature.adapter = CustomAdapter(
                    mPVData.assetCondition.temparatureImage,
                    this
                )

            }
        }
    }

    override fun clickedItem(pos: Int) {
        binding.viewPager.currentItem = pos
    }


}