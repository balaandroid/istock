package com.fertail.istock

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.databinding.ActivityQrscanningDetailBinding
import com.fertail.istock.model.AssetCondition
import com.fertail.istock.model.AssetImages
import com.fertail.istock.model.GetAllMasterDataResponse
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.view_model.FileUploadViewModel
import com.fertail.istock.view_model.GetAllMasterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class QRScanningDetailActivity :BaseActivity(){
    lateinit var binding:ActivityQrscanningDetailBinding
    var data: GetAllMasterDataResponse? = null
    lateinit var context: Context
    lateinit var locationManager: LocationManager
    lateinit var viewModel: GetAllMasterViewModel
    lateinit var fileUploadViewModel: FileUploadViewModel

    companion object {
        var mPVData = PVData()
        fun start(caller: Context, data: PVData) {
            val intent = Intent(caller, QRScanningDetailActivity::class.java)
            mPVData = data
            caller.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQrscanningDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[GetAllMasterViewModel::class.java]
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]
        EventBus.getDefault().register(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        context = this

        correctData()
        setContentView(binding.root)
        loadRecyclerView(ASSETS)
        loadRecyclerView(NAMEPLATE)
        loadRecyclerView(NAMEPLATE1)
        loadRecyclerView(addOldTag)
        loadRecyclerView(addnewTag)
        loadRecyclerView(ASSETSMAXIMO)
        loadRecyclerView(captureCorrosion)
        loadRecyclerView(captureDamage)
        loadRecyclerView(captureLeakage)
        updateData()
        binding.viewModel = this@QRScanningDetailActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(this)){
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        }else{
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.internet_alert_color))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            Toast.makeText(this,"You are online!", Toast.LENGTH_SHORT).show()
        } else {
            binding.topcl.setBackgroundColor(ContextCompat.getColor(this,R.color.internet_alert_color))
            Toast.makeText(this,"You are offline!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        binding.descriptionedt.text = mPVData.equipmentDesc ?: ""
        binding.idStorageLocationValue.text= mPVData.siteId ?: ""
        binding.idStorageBinValue.text= mPVData.assetNo ?: ""
        binding.idSYSValue.text =mPVData.assCondition ?: ""
        binding.idUOMValue.text =mPVData.equipmentDesc ?: ""
        binding.idUOMValue1.text =mPVData.parent ?: ""
        binding.idUOMValue2.text =mPVData.location ?: ""
        binding.idUOMValue3.text =mPVData.status ?: ""
        binding.idUOMValue4.text =mPVData.assetType ?: ""
        binding.idUOMValue5.text =mPVData.reportGroup ?: ""
        binding.idUOMValue6.text =mPVData.serialNo ?: ""
        binding.idUOMValue7.text =mPVData.modelNo ?: ""
        binding.idUOMValue8.text =mPVData.modelYear ?: ""
        binding.uniqueid.text = "Asset ID: " + mPVData.assetNo?: ""
        binding.presentLocTxt.text= mPVData.presentLocation?: ""
        binding.existBarcodeTxt.text= mPVData.existingBarCode?: ""
        binding.barcodeNoTxt.text= mPVData.barcode?: ""
        binding.remarkAdditionalTxt.text= mPVData.remarks?: ""
        binding.ownedTxt.text= mPVData.mOwnedBy?: ""
        binding.operateTxt.text= mPVData.mOperatedBy?: ""
        binding.maintainedTxt.text= mPVData.mMaintainer?: ""
        binding.idRemarkCondtion.text= mPVData.assetConditionRemarks?: ""
        binding.idRemark.text= mPVData.cRemarks?: ""
        binding.longitudeValue.text= mPVData.gIS.lattitudeStart?: ""
        binding.latitudeValue.text= mPVData.gIS.longitudeStart?: ""
        binding.oldTagId.text= mPVData.oldTagNo?: ""
        binding.newTagId.text= mPVData.newTagNo?: ""

        val rankData=mPVData.assetCondition.rank ?: ""
        binding.txtAssetCondiHeading.text= "ASSET CONDITION -> Ranking ( $rankData ) "



        if(mPVData.assetImages.namePlateText!=null){
            mPVData.assetImages.namePlateText!!.forEach {
                if (binding.namePlateOneTvValue.text.toString().isEmpty()) {
                    binding.namePlateOneTvValue.text = it
                } else {
                    binding.namePlateOneTvValue.text =
                        binding.namePlateOneTvValue.text.toString() + "\n" + it
                }
            }
        }else if(mPVData.assetImages.namePlateTextTwo!=null){
            mPVData.assetImages.namePlateTextTwo!!.forEach {
                if (binding.namePlateTwoTvValue.text.toString().isEmpty()) {
                    binding.namePlateTwoTvValue.text = it
                } else {
                    binding.namePlateTwoTvValue.text =
                        binding.namePlateTwoTvValue.text.toString() + "\n" + it
                }
            }
        }

        binding.imgBack.setOnClickListener {
            saveDataAndFinish()
        }

    }

    private fun saveDataAndFinish() {
        // Launch a coroutine to save data asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            // Save the non-completed item
            iStockApplication.saveNonCompletedItem(QRScanningDetailActivity.mPVData)
            withContext(Dispatchers.Main) {
                // Finish the activity on the main thread after saving
                finish()
            }
        }
    }



    private fun correctData() {

        if (mPVData.assetCondition == null){
            mPVData.assetCondition = AssetCondition()
        }

        if (mPVData.assetImages == null){
            mPVData.assetImages = AssetImages()

            if ( mPVData.assetImages.namePlateImgeTwo ==null){
                mPVData.assetImages.namePlateImgeTwo = ArrayList()
            }
            if ( mPVData.assetImages.namePlateImgeTwo ==null){
                mPVData.assetImages.namePlateImgeTwo = ArrayList()
            }
        }
    }

    fun loadRecyclerView(action: String) {

        val horizontalLayoutManagaer = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        when (action) {
            ASSETS -> {
                if (mPVData.assetImages.assetImage.isNotEmpty()){
                    binding.rcyAssets.layoutManager = horizontalLayoutManagaer
                    binding.rcyAssets.adapter = CustomAdapter(mPVData.assetImages.assetImage, this)
                }else{
                    binding.rcyAssets.visibility=View.GONE
                    binding.assetTxtNoData.visibility=View.VISIBLE
                }


            }

            ASSETSMAXIMO->{
                if(mPVData.assetImages.matImgs.isNotEmpty()){
                    binding.rcyAssetsMax.layoutManager = horizontalLayoutManagaer
                    binding.rcyAssetsMax.adapter = CustomAdapter(mPVData.assetImages.matImgs, this)
                }else{
                    binding.rcyAssetsMax.visibility=View.GONE
                    binding.assetmaxTxtNoData.visibility=View.VISIBLE
                }

            }

            NAMEPLATE -> {
                if(mPVData.assetImages.namePlateImge!=null){
                    binding.rcyNamePlate.layoutManager = horizontalLayoutManagaer
                    binding.rcyNamePlate.adapter = CustomAdapter(mPVData.assetImages.namePlateImge!!, this)
                }else{
                    binding.rcyNamePlate.visibility=View.GONE
                    binding.nameplateTxtNoData.visibility=View.VISIBLE
                }

            }

            NAMEPLATE1 -> {
                if (mPVData.assetImages.namePlateImgeTwo!=null){
                    binding.rcyNamePlate1.layoutManager = horizontalLayoutManagaer
                    binding.rcyNamePlate.adapter = CustomAdapter(mPVData.assetImages.namePlateImgeTwo, this)
                }else{
                    binding.rcyNamePlate1.visibility=View.GONE
                    binding.nameplate1TxtNoData.visibility=View.VISIBLE
                }


            }

            addOldTag -> {
                if (mPVData.assetImages.oldTagImage!=null){
                    binding.rcyOldTag.layoutManager = horizontalLayoutManagaer
                    binding.rcyOldTag.adapter = CustomAdapter(mPVData.assetImages.oldTagImage!!, this)
                }else{
                    binding.rcyOldTag.visibility=View.GONE
                    binding.oldTagTxtNoData.visibility=View.VISIBLE
                }

            }

            addnewTag -> {
                if(mPVData.assetImages.newTagImage.isNotEmpty()){
                    binding.rcynewTag.layoutManager = horizontalLayoutManagaer
                    binding.rcynewTag.adapter = CustomAdapter(
                        mPVData.assetImages.newTagImage,
                        this)
                }else{
                    binding.rcynewTag.visibility=View.GONE
                    binding.newTagTxtNoData.visibility=View.VISIBLE
                }

            }

            captureCorrosion->{
                if (mPVData.assetCondition.corrosionImage.isNotEmpty()){
                    binding.rcyAssetsCondtion.layoutManager = horizontalLayoutManagaer
                    binding.rcyAssetsCondtion.adapter = CustomAdapter(
                        mPVData.assetCondition.corrosionImage,
                        this)
                }else{
                    binding.rcyAssetsCondtion.visibility=View.GONE
                    binding.assettCorrTxtNoData.visibility=View.VISIBLE
                }

            }

            captureDamage->{

                if(mPVData.assetCondition.damageImage.isNotEmpty()){
                    binding.rcyAssetsCondtionDamage.layoutManager = horizontalLayoutManagaer
                    binding.rcyAssetsCondtionDamage.adapter = CustomAdapter(
                        mPVData.assetCondition.damageImage,
                        this)
                }else{
                    binding.rcyAssetsCondtionDamage.visibility=View.GONE
                    binding.assettDamageTxtNoData.visibility=View.VISIBLE
                }

            }

            captureLeakage->{
                if (mPVData.assetCondition.leakageImage.isNotEmpty()){
                    binding.rcyAssetsCondtionLeakage.layoutManager = horizontalLayoutManagaer
                    binding.rcyAssetsCondtionLeakage.adapter = CustomAdapter(
                        mPVData.assetCondition.leakageImage,
                        this)
                }else{
                    binding.rcyAssetsCondtionLeakage.visibility=View.GONE
                    binding.assettLeakageTxtNoData.visibility=View.VISIBLE
                }

            }

        }
    }

}

