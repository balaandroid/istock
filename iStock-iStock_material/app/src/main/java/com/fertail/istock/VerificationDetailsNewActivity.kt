package com.fertail.istock

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.databinding.ActivityVerificationDetailsNewBinding
import com.fertail.istock.databinding.ItemPhysicslObsBinding
import com.fertail.istock.iStockApplication.Companion.getWorkbookUriList
import com.fertail.istock.iStockApplication.Companion.saveWorkbookUriList
import com.fertail.istock.model.*
import com.fertail.istock.ui.bottomsheet.ChooseModifierBottomsheet
import com.fertail.istock.ui.bottomsheet.ChooseNounBottomsheet
import com.fertail.istock.ui.bottomsheet.FunctionalLocationChooseBottomsheet
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.ui.verification.FuntionalLocationFragment
import com.fertail.istock.ui.verification.adapter.AttributesAdapter
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.util.GenericRecyclerAdapter
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.util.SessionManager
import com.fertail.istock.view_model.FileUploadViewModel
import com.fertail.istock.view_model.GetAllMasterViewModel
import com.fertail.istock.view_model.GetNounViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class VerificationDetailsNewActivity : BaseActivity(), actionSelected {
    lateinit var binding: ActivityVerificationDetailsNewBinding
    var isneedtoSave = true
    var cal = Calendar.getInstance()
    var selectedCalenderType = ""
    var mAction: String = ""
    private var uri: Uri? = null
    var cameraorgallery: String = ""
    lateinit var fileUploadViewModel: FileUploadViewModel

    private lateinit var session: SessionManager

    var masterData: GetAllMasterDataResponse? = null

    lateinit var viewModel: GetAllMasterViewModel

    private val getNounViewModel: GetNounViewModel by viewModels()

    val nounList: ArrayList<NounTable> = ArrayList()

    private val uriArrayList=ArrayList<Uri>()


    private val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    }

    private val activityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = ImageClass()
                if (cameraorgallery == GALLERYTEXT) {
                    data.uri = it.data!!.data
                } else {
                    data.uri = uri
                }

                getContactBitmapFromURI(iStockApplication.appController!!, data.uri)?.let { it1 ->
                    saveBitmapIntoSDCardImage(iStockApplication.appController!!, it1).let {
                        if (it != null) {
                            lifecycleScope.launch {
                                Compressor.compress(this@VerificationDetailsNewActivity, it).let {
                                    if (NetworkUtils.isConnected(this@VerificationDetailsNewActivity)) {
                                        fileUploadViewModel.uploadFile(
                                            it.name,
                                            it,
                                            this@VerificationDetailsNewActivity)

                                        if (mAction.equals(idAddNamePlate)) {
                                            recognizeText(InputImage.fromFilePath(
                                                    this@VerificationDetailsNewActivity, data.uri!!)
                                            )
                                        }
                                    } else {
                                        when (mAction) {
                                            addMaterialImage -> {
                                                //todo change new
//                                                mPVData.assetImages.matImgs.add(it.absolutePath)
                                                mPVData.assetImages.assetImage.add(it.absolutePath)
                                                loadMaterialImage()
                                            }

                                            idAddNamePlate -> {
                                                mPVData.assetImages.nameplateImgs.add(it.absolutePath)
                                                loadNamePlateImage()
                                                recognizeText(
                                                    InputImage.fromFilePath(
                                                        this@VerificationDetailsNewActivity,
                                                        data.uri!!))
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

    companion object {
         var mPVData = PVData()
        fun start(caller: Context, data: PVData) {
            val intent = Intent(caller, VerificationDetailsNewActivity::class.java)
            mPVData = data
            caller.startActivity(intent)
        }

        val headers = listOf(
            "Item Code", "Material Code", "Material Description", "Noun", "Modifier", "Attribute", "Value", "SAP Code",
            "Storage Location", "Storage Bin", "Sys Balance", "Uom", "Manufacturer Name", "Part Number", "Model Number", "Equip Name", "Equip Model No",
            "Equip Manufacture", "Equip Tag No", "Equip Serial No", "Additional information", "PHY Balance",
            "Bin Updation/Miss Placed", "Material Images", "Name Plate Images", "Name Plate Text", "Additional info", "Remark",
            "Physical Observation", "No.Of.Items"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationDetailsNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]
        viewModel = ViewModelProvider(this)[GetAllMasterViewModel::class.java]
        EventBus.getDefault().register(this)
        session = SessionManager(this)
        observeViewModel()
        initViewClicks()
        observeDatabase()

        if (iStockApplication.appPreference.KEY_All_Master.equals("")) {
            viewModel.getAllMaster()
        } else {
            val gson = Gson()
            val jsonString = iStockApplication.appPreference.KEY_All_Master // Assuming this is your JSON string
            val myType = object : TypeToken<GetAllMasterDataResponse>() {}.type

            try {
              masterData = gson.fromJson<GetAllMasterDataResponse>(jsonString, myType)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }

//            val gson = Gson()
//            val myType = object : TypeToken<GetAllMasterDataResponse>() {}.type
//            masterData = gson.fromJson<GetAllMasterDataResponse>(iStockApplication.appPreference.KEY_All_Master, myType)
            updateData()
        }
    }

    private fun observeDatabase() {
        lifecycleScope.launch {
            getNounViewModel.getAllDictionary().collectLatest {
                nounList.clear()
                nounList.addAll(it)
                if (it.isEmpty()) {
                    getNounViewModel.getDictionary()
                }
            }
        }
    }

    private fun initViewClicks() {

        binding.btnSave.setOnClickListener {
            saveDataToLocal()
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.addMaterialImage.setOnClickListener {
            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(addMaterialImage, this)
            bottomSheetFilterDialogFragment.show(supportFragmentManager, "bottomSheetFrag")
        }

        binding.idAddNamePlate.setOnClickListener {
            val bottomSheetFilterDialogFragment =
                CustomBottomSheetDialogFragment(idAddNamePlate, this)
            bottomSheetFilterDialogFragment.show(
                supportFragmentManager,
                "bottomSheetFrag"
            )

        }

        binding.idGRDate.setOnClickListener {

            selectedCalenderType = "idGRDate"

            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.idQtyAsOn.setOnClickListener {

            selectedCalenderType = "idQtyAsOn"

            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.idExpiredDate.setOnClickListener {

            selectedCalenderType = "idExpiredDate"

            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.firstDropDown.setOnClickListener {

            if (binding.physicalContainer.visibility == View.VISIBLE) {
                binding.physicalContainer.visibility = View.GONE
                binding.idFirstArrrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)

            } else {
                binding.physicalContainer.visibility = View.VISIBLE
                binding.idFirstArrrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_up_24)
            }

//            roatateView(binding.idFirstArrrow);

            binding.secondConatiner.visibility = View.GONE
            binding.thirdConatiner.visibility = View.GONE
            binding.idSecondArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idThirdArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)

        }

        binding.secondDropDown.setOnClickListener {

            if (binding.secondConatiner.visibility == View.VISIBLE) {
                binding.secondConatiner.visibility = View.GONE
                binding.idSecondArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)

            } else {
                binding.secondConatiner.visibility = View.VISIBLE
                binding.idSecondArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_up_24)

            }

            binding.physicalContainer.visibility = View.GONE
            binding.thirdConatiner.visibility = View.GONE
            binding.vendorContainer.visibility = View.GONE

            binding.idFirstArrrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idThirdArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idVendorArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
//            roatateView(binding.idSecondArrow);
        }

        binding.thirdDropDown.setOnClickListener {

            if (binding.thirdConatiner.visibility == View.VISIBLE) {
                binding.thirdConatiner.visibility = View.GONE
                binding.idThirdArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            } else {
                binding.thirdConatiner.visibility = View.VISIBLE
                binding.idThirdArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_up_24)
            }

            binding.physicalContainer.visibility = View.GONE
            binding.secondConatiner.visibility = View.GONE
            binding.vendorContainer.visibility = View.GONE

            binding.idSecondArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idFirstArrrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idVendorArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)

//            roatateView(binding.idThirdArrow);
        }


        binding.vendorDropDown.setOnClickListener {


            if (binding.vendorContainer.visibility == View.VISIBLE) {
                binding.vendorContainer.visibility = View.GONE
                binding.idVendorArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            } else {
                binding.vendorContainer.visibility = View.VISIBLE
                binding.idVendorArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_up_24)
            }

            binding.physicalContainer.visibility = View.GONE
            binding.secondConatiner.visibility = View.GONE
            binding.thirdConatiner.visibility = View.GONE

            binding.idSecondArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idFirstArrrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.idThirdArrow.setBackgroundResource(com.fertail.istock.R.drawable.ic_baseline_keyboard_arrow_down_24)


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


//            getNounViewModel.getAllMaster()
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

                modalBottomSheet =
                    ChooseModifierBottomsheet("Modifier", tempList, object : ItemSelectedForNoune {
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

            modalBottomSheet.show(supportFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
        }


    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(this)){
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        }else{
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.internet_alert_color))
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
        binding.uniqueid.text = "Item Code: " + mPVData.itemcode
        binding.sapCode.setText(mPVData.sapcode)
        binding.idStorageBin.setText(mPVData.storagebin)
        binding.idSysBal.setText(mPVData.sysbal)
//        binding.idQtyAsOn.setText(mPVData.quantity)
        binding.idUOM.setText(mPVData.uOM)
        binding.idPhyBal.setText(mPVData.pysbal)
        binding.idPhysicalObservation.text = mPVData.pysicalObservation
        binding.idNoOfItemPV.setText(mPVData.pysicalObservationQty)
        binding.idNoOfItemExpired.setText(mPVData.expiredQty)
        binding.idSelfLife.setText(mPVData.selfLife)
        binding.idBINUpdation.setText(mPVData.binUpdation)
        binding.idAdditionalInfo.setText(mPVData.additioninfo)
        binding.idRemark.setText(mPVData.cRemarks)
        binding.idDescription.setText(mPVData.materialDesc)
        binding.idDisc.setText(mPVData.equipmentDesc)
        binding.idMake.setText(mPVData.make)
        binding.idModelPartNo.setText(mPVData.model)


        binding.idStorageLocation.setText(mPVData.stockRemark)
        binding.idGRDate.text = mPVData.gRDate
        binding.idExpiredDate.text = mPVData.expiredDate
        binding.idQtyAsOn.text = mPVData.qtyasonDate

        binding.idNoune.text = mPVData.noun
        binding.idModifier.text = mPVData.modifier
        binding.idSAPCOde.text = mPVData.sapcode
        binding.idStorageLocationValue.text = mPVData.stroageLocation
        binding.idStorageBinValue.text = mPVData.stroageBin
        binding.idSYSValue.text = mPVData.sysbal
        binding.idUOMValue.text = mPVData.uOM
//        binding.idManufacturerName.setText(mPVData.vendorsuppliers.manufacture)
//        binding.idParthNo.setText(mPVData.vendorsuppliers.partNo)
//        binding.idModelNo.setText(mPVData.vendorsuppliers.modelNo)


//            binding.idEquipModelNo.setText(mPVData.equipment.modelno)
//            binding.idEquipManufacture.setText(mPVData.equipment.manufacturer)
//            binding.idEquipTagNo.setText(mPVData.equipment.tagno)
//            binding.idSerialNo.setText(mPVData.equipment.serialno)
//            binding.idEquipAdditionalInfo.setText(mPVData.equipment.additionalinfo)
//            binding.idEquipName.setText(mPVData.equipment.name)


        mPVData.vendorsuppliers?.let { vendorsuppliers ->
            binding.idManufacturerName.setText(vendorsuppliers.manufacture)
            binding.idParthNo.setText(vendorsuppliers.partNo)
            binding.idModelNo.setText(vendorsuppliers.modelNo)
        }

        mPVData.equipment?.let { equipment ->
            binding.idEquipModelNo.setText(equipment.modelno)
            binding.idEquipManufacture.setText(equipment.manufacturer)
            binding.idEquipTagNo.setText(equipment.tagno)
            binding.idSerialNo.setText(equipment.serialno)
            binding.idEquipAdditionalInfo.setText(equipment.additionalinfo)
            binding.idEquipName.setText(equipment.name)
        }


        loadMaterialImage()
        loadNamePlateImage()
        updateRecyclerView()

        mPVData.assetImages.namePlateText!!.forEach {
            if (binding.txtExtracted.text.toString().isEmpty()) {
                binding.txtExtracted.text = it
            } else {
                binding.txtExtracted.text = binding.txtExtracted.text.toString() + "\n" + it
            }
        }

        binding.sapCode.addTextChangedListener(MyTextWatcher("sapCode"))
        binding.idStorageBin.addTextChangedListener(MyTextWatcher("idStorageBin"))
        binding.idStorageLocation.addTextChangedListener(MyTextWatcher("idStorageLocation"))
        binding.idSysBal.addTextChangedListener(MyTextWatcher("idSysBal"))
        binding.idUOM.addTextChangedListener(MyTextWatcher("idUOM"))
        binding.idPhyBal.addTextChangedListener(MyTextWatcher("idPhyBal"))
        binding.idPhysicalObservation.addTextChangedListener(MyTextWatcher("idPhysicalObservation"))
        binding.idNoOfItemPV.addTextChangedListener(MyTextWatcher("idNoOfItemPV"))
        binding.idNoOfItemExpired.addTextChangedListener(MyTextWatcher("idNoOfItemExpired"))
        binding.idSelfLife.addTextChangedListener(MyTextWatcher("idSelfLife"))
        binding.idBINUpdation.addTextChangedListener(MyTextWatcher("idBINUpdation"))
        binding.idAdditionalInfo.addTextChangedListener(MyTextWatcher("idAdditionalInfo"))
        binding.idRemark.addTextChangedListener(MyTextWatcher("idRemark"))
        binding.idDescription.addTextChangedListener(MyTextWatcher("idDescription"))
        binding.idDisc.addTextChangedListener(MyTextWatcher("idDisc"))
        binding.idMake.addTextChangedListener(MyTextWatcher("idMake"))
        binding.idModelPartNo.addTextChangedListener(MyTextWatcher("idModelPartNo"))

        binding.idEquipModelNo.addTextChangedListener(MyTextWatcher("idEquipModelNo"))
        binding.idEquipManufacture.addTextChangedListener(MyTextWatcher("idEquipManufacture"))
        binding.idEquipTagNo.addTextChangedListener(MyTextWatcher("idEquipTagNo"))
        binding.idSerialNo.addTextChangedListener(MyTextWatcher("idSerialNo"))
        binding.idEquipAdditionalInfo.addTextChangedListener(MyTextWatcher("idEquipAdditionalInfo"))
        binding.idEquipName.addTextChangedListener(MyTextWatcher("idEquipName"))

        binding.idManufacturerName.addTextChangedListener(MyTextWatcher("idManufacturerName"))
        binding.idParthNo.addTextChangedListener(MyTextWatcher("idParthNo"))
        binding.idModelNo.addTextChangedListener(MyTextWatcher("idModelNo"))

//        binding.idExpiredDate.addTextChangedListener(MyTextWatcher("idExpiredDate"))
//        binding.idGRDate.addTextChangedListener(MyTextWatcher("idGRDate"))
    }


    class ItemTextWatcher(val eachItem: Observations, val s: String) : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            if (s.equals("idPhysicalObservation")) {
                eachItem.observation = p0.toString()
            } else {
                eachItem.qty = p0.toString()
            }

            mPVData.observations[eachItem.id] = eachItem

        }

    }

    class MyTextWatcher(val type: String) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (type.equals("idMake")) {
                mPVData.make = p0.toString()
            }

            if (type.equals("idModelPartNo")) {
                mPVData.model = p0.toString()
            }

            if (type.equals("sapCode")) {
                mPVData.sapcode = p0.toString()
            }
            if (type.equals("idStorageBin")) {
                mPVData.storagebin = p0.toString()
            }

            if (type.equals("idSysBal")) {
                mPVData.sysbal = p0.toString()
            }
            if (type.equals("idUOM")) {
                mPVData.uOM = p0.toString()
            }


            if (type.equals("idPhysicalObservation")) {
                mPVData.pysicalObservation = p0.toString()
            }

            if (type.equals("idNoOfItemPV")) {
                mPVData.pysicalObservationQty = p0.toString()
            }

            if (type.equals("idNoOfItemExpired")) {
                mPVData.expiredQty = p0.toString()
            }
            if (type.equals("idSelfLife")) {
                mPVData.selfLife = p0.toString()
            }

            if (type.equals("idBINUpdation")) {
                mPVData.binUpdation = p0.toString()
            }

            if (type.equals("idAdditionalInfo")) {
                mPVData.additioninfo = p0.toString()
            }
            if (type.equals("idRemark")) {
                mPVData.cRemarks = p0.toString()
            }
            if (type.equals("idDescription")) {
                mPVData.materialDesc = p0.toString()
            }

            if (type.equals("idDisc")) {
                mPVData.materialDesc = p0.toString()
            }

            if (type.equals("idStorageLocation")) {
                mPVData.stockRemark = p0.toString()
            }

            if (type.equals("idEquipAdditionalInfo")) {
                mPVData.equipment.additionalinfo = p0.toString()
            }

            if (type.equals("idEquipName")) {
                mPVData.equipment.name = p0.toString()
            }

            if (type.equals("idPhyBal")) {
                mPVData.pysbal = p0.toString()
            }

            if (type.equals("idSerialNo")) {
                mPVData.equipment.serialno = p0.toString()
            }

            if (type.equals("idEquipTagNo")) {
                mPVData.equipment.tagno = p0.toString()
            }

            if (type.equals("idEquipManufacture")) {
                mPVData.equipment.manufacturer = p0.toString()
            }

            if (type.equals("idEquipModelNo")) {
                mPVData.equipment.modelno = p0.toString()
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

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    fun loadMaterialImage() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcyMaterialImage.layoutManager = horizontalLayoutManagaer
        binding.rcyMaterialImage.adapter = CustomAdapter(mPVData.assetImages.assetImage, this)
    }

    fun loadNamePlateImage() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcyNamePlate.layoutManager = horizontalLayoutManagaer
        binding.rcyNamePlate.adapter = CustomAdapter(
            mPVData.assetImages.nameplateImgs,
            this
        )
    }


    override fun onBackPressed() {

        if (isneedtoSave) {
            iStockApplication.saveNonCompletedItem(mPVData)
        }

        super.onBackPressed()
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (selectedCalenderType.equals("idGRDate")) {
            binding.idGRDate.text = sdf.format(cal.time)
            mPVData.gRDate = sdf.format(cal.time)
            selectedCalenderType = ""
        }


        if (selectedCalenderType.equals("idQtyAsOn")) {
            binding.idQtyAsOn.text = sdf.format(cal.getTime())
            mPVData.qtyasonDate = sdf.format(cal.getTime())
            selectedCalenderType = ""
        }

        if (selectedCalenderType.equals("idExpiredDate")) {
            binding.idExpiredDate.text = sdf.format(cal.getTime())
            mPVData.expiredDate = sdf.format(cal.getTime())
            selectedCalenderType = ""
        }
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
                    Manifest.permission.CAMERA)
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
//        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    var capturedImage: File? = null

    private fun capturePhoto() {

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        capturedImage = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "iStock_$ts.jpg")
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
    }


    private fun getContactBitmapFromURI(context: Context, uri: Uri?): Bitmap? {
        try {
            if (uri != null) {
                if (cameraorgallery.equals(GALLERYTEXT)) {
                    val input = context.contentResolver.openInputStream(uri) ?: return null
                    val bitmap = BitmapFactory.decodeStream(input)
                    return bitmap
                }

                return CommonUtils.decodeFile(capturedImage!!.absolutePath)
            }

//
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
        val fname = mPVData.assetNo + "_$ts" + ".png"
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
        viewModel.showSuccess.observe(this, Observer { countries ->
            countries?.let {
//                masterData = it
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
                    when (mAction) {
                        addMaterialImage -> {
                            //todo change new
//                            mPVData.assetImages.matImgs.add(it)
                            mPVData.assetImages.assetImage.add(it)
                            loadMaterialImage()
                        }

                        idAddNamePlate -> {
                            mPVData.assetImages.nameplateImgs.add(it)
                            loadNamePlateImage()
                        }
                    }
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

        getNounViewModel.showSuccess.observe(this, Observer { countries ->
            countries?.let {
                if (it.isNotEmpty()) {

//                    var modalBottomSheet: ChooseNounBottomsheet? = null
//
//                    modalBottomSheet =  ChooseNounBottomsheet("Noun", it, object : ItemSelectedForNoune{
//                        override fun selectedItem(noun: String) {
//                            mPVData.noun = noun
//                            binding.idNoune.setText(noun)
//                            modalBottomSheet?.dismiss()
//                        }
//                    })
//
//                    modalBottomSheet?.show(supportFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
//
//                    getNounViewModel.showSuccess.postValue(ArrayList<NounItem>())


                }

            }
        })

        getNounViewModel.showSuccessModifier.observe(this, Observer { idModifier ->
            idModifier?.let {

//                if (it.isNotEmpty()){
//                    var modalBottomSheet: ChooseModifierBottomsheet? = null
//
//                    modalBottomSheet =  ChooseModifierBottomsheet("Noun", it, object : ItemSelectedForNoune{
//                        override fun selectedItem(noun: String) {
//                            mPVData.modifier = noun
//                            binding.idModifier.setText(noun)
//                            modalBottomSheet?.dismiss()
//
//
//
//                            getNounViewModel.getAttributes(mPVData.noun?:"", mPVData.modifier?:"")
//
//                        }
//                    })
//
//                    modalBottomSheet?.show(supportFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
//
//                    getNounViewModel.showSuccessModifier.postValue(ArrayList())
//                }
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
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })


    }

    var mAdapter = GenericRecyclerAdapter<Observations>()

    private fun updateRecyclerView() {

        mPVData.characteristics.let {
            binding.idAttribute.layoutManager = LinearLayoutManager(this)
            binding.idAttribute.adapter = AttributesAdapter(it, "")
        }

        updateObseData()
        
        binding.idCharctersticContainer.isVisible = mPVData.observations.isNotEmpty()

        mAdapter.listOfItems = mPVData.observations

        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as ItemPhysicslObsBinding

            if (eachItem.isLast == true) {
                view.remove.setBackgroundResource(R.drawable.add_image_new)
            } else {
                view.remove.setBackgroundResource(R.drawable.ic_baseline_remove_circle_24)
            }

            view.idNoOfItemExpired.setText(eachItem.qty)


            view.idPhysicalObservation.text = eachItem.observation

            view.idPhysicalObservation.setOnClickListener {
                if (masterData!=null){
                    var modalBottomSheet: FunctionalLocationChooseBottomsheet? = null
                    modalBottomSheet = FunctionalLocationChooseBottomsheet(PhysicalObs, masterData!!,
                        object : FuntionalLocationFragment.itemSelected {
                            override fun selectedItem(id: String, code: String, name: String) {
                                modalBottomSheet?.dismiss()
                                VerificationDetailsActivity.mPVData.equipmentType = id
                                eachItem.observation = name
                                mPVData.observations[eachItem.id] = eachItem

                                view.idPhysicalObservation.text = name
                            }
                        })

                    modalBottomSheet.show(
                        supportFragmentManager,
                        FunctionalLocationChooseBottomsheet.TAG)
                }else{

                    Toast.makeText(this,"No data Available here !!",Toast.LENGTH_SHORT).show()
                }

            }

            view.idNoOfItemExpired.addTextChangedListener(
                ItemTextWatcher(eachItem, "idNoOfItemExpired"))
//            view.idPhysicalObservation.addTextChangedListener(ItemTextWatcher(
//                eachItem,
//                "idPhysicalObse
//                rvation"
//            ))

            view.remove.setOnClickListener {
                if (eachItem.isLast) {
                    mPVData.observations.add(Observations())
                } else {
                    mPVData.observations.remove(eachItem)
                }

                updateObseData()
                mAdapter.listOfItems = mPVData.observations
            }

            view.root.setOnClickListener {
                //Click item value is eachItem
            }
        }

        mAdapter.expressionOnCreateViewHolder = { viewGroup ->
            ItemPhysicslObsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        //finally put the adapter to recyclerview
        binding.recyclerViewPhyObs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

    }


    private fun showDropdownDialog(items: Array<String>, onItemSelected: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setItems(items) { dialog, which ->
            onItemSelected(items[which])
        }
        builder.show()
    }

    private fun updateObseData() {
        if (mPVData.observations.isEmpty()) {
            val attributesItem = Observations()
            attributesItem.isLast = true
            attributesItem.id = 0
            mPVData.observations.add(attributesItem)
        } else {
            val size = mPVData.observations.size

            for ((index, value) in mPVData.observations.withIndex()) {
                value.id = index
                value.isLast = index == (size - 1)
            }
        }
    }

    private fun saveDataToLocal() {

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
                    System.out.println(" C DATE is  $currentDate")

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
                    createExcelSheet(mPVData)
                    isneedtoSave = false
                    dialog.dismiss()
                    finish()
                }

                override fun btnNegativeSelected(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            })

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createExcelSheet(vararg mPVData: PVData) {
        try {
            val today = LocalDate.now()
            val fileName = "material${today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}.xls"

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
                sheet = workbook.createSheet("Material Sheet", 0)

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
                        sheet.addCell(Label(0, row, data.itemcode, dataFormat))
                        sheet.addCell(Label(1, row, data.assetNo, dataFormat))
                        sheet.addCell(Label(2, row, data.equipmentDesc, dataFormat))
                        sheet.addCell(Label(3, row, data.noun, dataFormat))
                        sheet.addCell(Label(4, row, data.modifier, dataFormat))
                        sheet.addCell(Label(5, row, data1.characteristic, dataFormat))
                        sheet.addCell(Label(6, row, data1.definition, dataFormat))
                        sheet.addCell(Label(7, row, data.sapcode, dataFormat))
                        sheet.addCell(Label(8, row, data.stroageLocation, dataFormat))
                        sheet.addCell(Label(9, row, data.stroageBin, dataFormat))
                        sheet.addCell(Label(10, row, data.sysbal, dataFormat))
                        sheet.addCell(Label(11, row, data.uOM, dataFormat))
                        sheet.addCell(Label(12, row, data.vendorsuppliers.manufacture, dataFormat))
                        sheet.addCell(Label(13, row, data.vendorsuppliers.partNo, dataFormat))
                        sheet.addCell(Label(14, row, data.vendorsuppliers.modelNo, dataFormat))
                        sheet.addCell(Label(15, row, data.equipment.name, dataFormat))
                        sheet.addCell(Label(16, row, data.equipment.modelno, dataFormat))
                        sheet.addCell(Label(17, row, data.equipment.manufacturer, dataFormat))
                        sheet.addCell(Label(18, row, data.equipment.tagno, dataFormat))
                        sheet.addCell(Label(19, row, data.equipment.serialno, dataFormat))
                        sheet.addCell(Label(20, row, data.equipment.additionalinfo, dataFormat))
                        sheet.addCell(Label(21, row, data.pysbal, dataFormat))
                        sheet.addCell(Label(22, row, data.binUpdation, dataFormat))
                        sheet.addCell(Label(23, row, data.assetImages.assetImage.toString(), dataFormat))
                        sheet.addCell(Label(24, row, data.assetImages.nameplateImgs.toString(), dataFormat))
                        val step1 = data.assetImages.namePlateText.toString().replace("\n", " ")
                        val normalText = step1.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")

                        sheet.addCell(Label(25, row, normalText.trim(), dataFormat))
                        sheet.addCell(Label(26, row, data.additioninfo, dataFormat))
                        sheet.addCell(Label(27, row, data.cRemarks, dataFormat))

                        for (data2 in data.observations) {
                            sheet.addCell(Label(28, row, data2.observation, dataFormat))
                            sheet.addCell(Label(29, row, data2.qty, dataFormat))
                        }
                        row++
                    }
                } else {
                    sheet.addCell(Label(0, row, data.itemcode, dataFormat))
                    sheet.addCell(Label(1, row, data.assetNo, dataFormat))
                    sheet.addCell(Label(2, row, data.equipmentDesc, dataFormat))
                    sheet.addCell(Label(3, row, data.noun, dataFormat))
                    sheet.addCell(Label(4, row, data.modifier, dataFormat))
                    sheet.addCell(Label(5, row, "", dataFormat))
                    sheet.addCell(Label(6, row, "", dataFormat))
                    sheet.addCell(Label(7, row, data.sapcode, dataFormat))
                    sheet.addCell(Label(8, row, data.stroageLocation, dataFormat))
                    sheet.addCell(Label(9, row, data.stroageBin, dataFormat))
                    sheet.addCell(Label(10, row, data.sysbal, dataFormat))
                    sheet.addCell(Label(11, row, data.uOM, dataFormat))
                    sheet.addCell(Label(12, row, data.vendorsuppliers.manufacture, dataFormat))
                    sheet.addCell(Label(13, row, data.vendorsuppliers.partNo, dataFormat))
                    sheet.addCell(Label(14, row, data.vendorsuppliers.modelNo, dataFormat))
                    sheet.addCell(Label(15, row, data.equipment.name, dataFormat))
                    sheet.addCell(Label(16, row, data.equipment.modelno, dataFormat))
                    sheet.addCell(Label(17, row, data.equipment.manufacturer, dataFormat))
                    sheet.addCell(Label(18, row, data.equipment.tagno, dataFormat))
                    sheet.addCell(Label(19, row, data.equipment.serialno, dataFormat))
                    sheet.addCell(Label(20, row, data.equipment.additionalinfo, dataFormat))
                    sheet.addCell(Label(21, row, data.pysbal, dataFormat))
                    sheet.addCell(Label(22, row, data.binUpdation, dataFormat))
                    sheet.addCell(Label(23, row, data.assetImages.assetImage.toString(), dataFormat))
                    sheet.addCell(Label(24, row, data.assetImages.nameplateImgs.toString(), dataFormat))
                    val step1 = data.assetImages.namePlateText.toString().replace("\n", " ")
                    val normalText = step1.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")

                    sheet.addCell(Label(25, row, normalText.trim(), dataFormat))
                    sheet.addCell(Label(26, row, data.additioninfo, dataFormat))
                    sheet.addCell(Label(27, row, data.cRemarks, dataFormat))

                    for (data2 in data.observations) {
                        sheet.addCell(Label(28, row, data2.observation, dataFormat))
                        sheet.addCell(Label(29, row, data2.qty, dataFormat))
                    }
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

//            uriArrayList.add(contentUri)

            val uris = session.getUriArrayList("excel_material")
            if (uris != null) {
                if (!uris.contains(contentUri)) {
                    uris.add(contentUri)
                    session.setUriArrayList("excel_material", uris)
                }
            } else {
                val newList = mutableListOf(contentUri)
                session.setUriArrayList("excel_material", newList)
            }

            workbook.write()
            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



//    private fun createExcelSheet(vararg mPVData: PVData) {
//        val filePath = "data.xls"
//        val file = File(this.getExternalFilesDir(null), filePath)
//
//        try {
//            val workbook = Workbook.createWorkbook(file)
//            val sheet = workbook.createSheet("Material Sheet", 0)
//
//            val headerFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
//            val headerFormat = WritableCellFormat(headerFont)
//            headerFormat.setBackground(Colour.GRAY_25)
//
//            val dataFont = WritableFont(WritableFont.ARIAL, 10)
//            val dataFormat = WritableCellFormat(dataFont)
//
//            val headers = listOf("Item Code", "Material Code", "Material Description", "Noun","Modifier","Attribute","Value","SAP Code",
//                "Storage Location", "Storage Bin", "Sys Balance", "Uom","Manufacturer Name","Part Number","Model Number","Equip Name","Equip Model No",
//                "Equip Manufacture","Equip Tag No","Equip Serial No","Additional information","PHY Balance",
//                "Bin Updation/Miss Placed","Material Images","Name Plate Images","Name Plate Text","Additional info","Remark",
//                "Physical Observation","No.Of.Items")
//
//
//            for ((index, header) in headers.withIndex()) {
//                sheet.addCell(Label(index, 0, header, headerFormat))
//            }
//
//            var row = 1
//            for (data in mPVData) {
//                if(data.characteristics.isNotEmpty()){
//                    for (data1 in data.characteristics){
//                        sheet.addCell(Label(0, row, data.itemcode,dataFormat))
//                        sheet.addCell(Label(1, row, data.assetNo,dataFormat))
//                        sheet.addCell(Label(2, row,data.equipmentDesc,dataFormat))
//                        sheet.addCell(Label(3, row,data.noun,dataFormat))
//                        sheet.addCell(Label(4, row,data.modifier,dataFormat))
//                        sheet.addCell(Label(5, row,data1.characteristic,dataFormat))
//                        sheet.addCell(Label(6, row,data1.definition,dataFormat))
//                        sheet.addCell(Label(7, row,data.sapcode,dataFormat))
//                        sheet.addCell(Label(8, row,data.stroageLocation,dataFormat))
//                        sheet.addCell(Label(9, row,data.stroageBin,dataFormat))
//                        sheet.addCell(Label(10, row,data.sysbal,dataFormat))
//                        sheet.addCell(Label(11, row,data.uOM,dataFormat))
//                        sheet.addCell(Label(12, row,data.vendorsuppliers.manufacture,dataFormat))
//                        sheet.addCell(Label(13, row,data.vendorsuppliers.partNo,dataFormat))
//                        sheet.addCell(Label(14, row,data.vendorsuppliers.modelNo,dataFormat))
//                        sheet.addCell(Label(15, row,data.equipment.name,dataFormat))
//                        sheet.addCell(Label(16, row,data.equipment.modelno,dataFormat))
//                        sheet.addCell(Label(17, row,data.equipment.manufacturer,dataFormat))
//                        sheet.addCell(Label(18, row,data.equipment.tagno,dataFormat))
//                        sheet.addCell(Label(19, row,data.equipment.serialno,dataFormat))
//                        sheet.addCell(Label(20, row,data.equipment.additionalinfo,dataFormat))
//                        sheet.addCell(Label(21, row,data.pysbal,dataFormat))
//                        sheet.addCell(Label(22, row,data.binUpdation,dataFormat))
//                        sheet.addCell(Label(23, row, data.assetImages.assetImage.toString(),dataFormat))
//                        sheet.addCell(Label(24, row, data.assetImages.nameplateImgs.toString(),dataFormat))
//                        val step1 = data.assetImages.namePlateText.toString().replace("\n", " ")
//                        val normalText = step1.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")
//
//                        sheet.addCell(Label(25, row,normalText.trim(),dataFormat))
//                        sheet.addCell(Label(26, row, data.additioninfo,dataFormat))
//                        sheet.addCell(Label(27, row, data.cRemarks,dataFormat))
//
//                        for (data2 in data.observations){
//                            sheet.addCell(Label(28, row, data2.observation,dataFormat))
//                            sheet.addCell(Label(29, row, data2.qty,dataFormat))
//                        }
//                        row++
//                    }
//                }else{
//                    sheet.addCell(Label(0, row, data.itemcode,dataFormat))
//                    sheet.addCell(Label(1, row, data.assetNo,dataFormat))
//                    sheet.addCell(Label(2, row,data.equipmentDesc,dataFormat))
//                    sheet.addCell(Label(3, row,data.noun,dataFormat))
//                    sheet.addCell(Label(4, row,data.modifier,dataFormat))
//                    sheet.addCell(Label(5, row, "",dataFormat))
//                    sheet.addCell(Label(6, row, "",dataFormat))
//                    sheet.addCell(Label(7, row,data.sapcode,dataFormat))
//                    sheet.addCell(Label(8, row,data.stroageLocation,dataFormat))
//                    sheet.addCell(Label(9, row,data.stroageBin,dataFormat))
//                    sheet.addCell(Label(10, row,data.sysbal,dataFormat))
//                    sheet.addCell(Label(11, row,data.uOM,dataFormat))
//                    sheet.addCell(Label(12, row,data.vendorsuppliers.manufacture,dataFormat))
//                    sheet.addCell(Label(13, row,data.vendorsuppliers.partNo,dataFormat))
//                    sheet.addCell(Label(14, row,data.vendorsuppliers.modelNo,dataFormat))
//                    sheet.addCell(Label(15, row,data.equipment.name,dataFormat))
//                    sheet.addCell(Label(16, row,data.equipment.modelno,dataFormat))
//                    sheet.addCell(Label(17, row,data.equipment.manufacturer,dataFormat))
//                    sheet.addCell(Label(18, row,data.equipment.tagno,dataFormat))
//                    sheet.addCell(Label(19, row,data.equipment.serialno,dataFormat))
//                    sheet.addCell(Label(20, row,data.equipment.additionalinfo,dataFormat))
//                    sheet.addCell(Label(21, row,data.pysbal,dataFormat))
//                    sheet.addCell(Label(22, row,data.binUpdation,dataFormat))
//                    sheet.addCell(Label(23, row, data.assetImages.assetImage.toString(),dataFormat))
//                    sheet.addCell(Label(24, row, data.assetImages.nameplateImgs.toString(),dataFormat))
//                    val step1 = data.assetImages.namePlateText.toString().replace("\n", " ")
//                    val normalText = step1.replace(Regex("\\(.*?\\)|F[0-9]+|Caps Lock|Shift|Ctrd|Fn|\\["), "")
//
//                    sheet.addCell(Label(25, row,normalText.trim(),dataFormat))
//                    sheet.addCell(Label(26, row, data.additioninfo,dataFormat))
//                    sheet.addCell(Label(27, row, data.cRemarks,dataFormat))
//
//                    for (data2 in data.observations){
//                        sheet.addCell(Label(28, row, data2.observation,dataFormat))
//                        sheet.addCell(Label(29, row, data2.qty,dataFormat))
//                    }
//                    row++
//                }
//
//            }
//
//
//            for (col in headers.indices) {
//                sheet.setColumnView(col, getPreferredColumnWidth(sheet, col))
//            }
//
//            val contentUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)
//            uriArrayList.add(contentUri)
//
//            val uris = session.getUriArrayList("excel_material")
//            if (uris != null) {
//                uris.add(contentUri)
//                session.setUriArrayList("excel_material", uris)
//            } else {
//                val newList = mutableListOf(contentUri)
//                session.setUriArrayList("excel_material", newList)
//            }
//
//            workbook.write()
//            workbook.close()
//
//        }catch (e:Exception){
//            e.printStackTrace()
//        }
//
//    }


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

            Log.e("", "processTextBlock blockText==>> $blockText")


            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox

                Log.e("", "processTextBlock lineText==>> $lineText")

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

        if (mAction.equals(idAddNamePlate)) {
            mPVData.assetImages.namePlateText!!.add(tempText)
            binding.txtExtracted.text =
                binding.txtExtracted.text.toString().trim() + "\n" + tempText
        }

    }


}

interface ItemSelectedForNoune {
    fun selectedItem(noun: String)
}