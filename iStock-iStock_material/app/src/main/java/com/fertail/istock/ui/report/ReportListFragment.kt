package com.fertail.istock.ui.report

import android.Manifest
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.*
import com.fertail.istock.databinding.FragmentReportListBinding
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.ReportClickListener
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.verification.CompletedReportAdapter
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.view_model.FileUploadViewModel
import com.fertail.istock.view_model.SaveEquipmentViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ReportListFragment : BaseFragment(), ReportClickListener {

    private var _binding: FragmentReportListBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: CompletedReportAdapter
    lateinit var viewModel: SaveEquipmentViewModel
    lateinit var fileUploadViewModel: FileUploadViewModel
    var newItem: ArrayList<PVData> = ArrayList()
    var data = ArrayList<PVData>()
    var uploadImage: ArrayList<ImageClass> = ArrayList()
    var pos = 0
    var progressDialog: ProgressDialog? = null
    var progressPers = 0
    var increase = 0

    var isMaterialSelected = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReportListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SaveEquipmentViewModel::class.java]
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog!!.setCancelable(false)
        progressDialog!!.setIndeterminate(false)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog!!.setMax(100)
//        progressDialog!!.setCancelable(true)
        progressDialog!!.setMessage("Loading ...")

        initClickLitsner()
        observeViewModel()
        initToolbor()
        data=iStockApplication.pvDataCompletedModel.data
        initRecyclerView(data)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(requireContext())){
            binding.toolbarReportList.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }else{
            binding.toolbarReportList.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.toolbarReportList.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
            Toast.makeText(requireContext(),"You are online!",Toast.LENGTH_SHORT).show()
        } else {
            binding.toolbarReportList.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
            Toast.makeText(requireContext(),"You are offline!",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }


    private fun initClickLitsner() {
        binding.idMaterialListImage.setOnClickListener {
            binding.idMaterialList.post {
                binding.idMaterialList.performClick()
            }
        }

        binding.idEquipmentList.setOnClickListener {
            binding.idMaterialListImage.setBackgroundResource(R.drawable.ic_unselected_circle)
            binding.idEqupmentListImage.setBackgroundResource(R.drawable.ic_selected_circle)
            isMaterialSelected = false
            initRecyclerView(data)

            if (!binding.toolbarReportList.idSelect.text.toString()
                    .equals(resources.getString(R.string.select))
            ) {
                binding.toolbarReportList.idSelect.post {
                    binding.toolbarReportList.idSelect.performClick()
                }
            }

        }

        binding.idEqupmentListImage.setOnClickListener {
            binding.idEquipmentList.post {
                binding.idEquipmentList.performClick()
            }
        }

        binding.idMaterialList.setOnClickListener {
            binding.idMaterialListImage.setBackgroundResource(R.drawable.ic_selected_circle)
            binding.idEqupmentListImage.setBackgroundResource(R.drawable.ic_unselected_circle)
            isMaterialSelected = true
            initRecyclerView(data)
            if (!binding.toolbarReportList.idSelect.text.toString()
                    .equals(resources.getString(R.string.select))
            ) {
                binding.toolbarReportList.idSelect.post {
                    binding.toolbarReportList.idSelect.performClick()
                }
            }
        }

        binding.idClear.setOnClickListener {
            binding.idSearch.setText("")
            binding.idSearch.clearFocus()
            initRecyclerView(data)
        }

        binding.idSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    if (p0.length == 0){
                        initRecyclerView(data)
                    }else{
                        val newItem: java.util.ArrayList<PVData> = java.util.ArrayList()
                        val newItem1 =data.filter { s ->
                            (s.assetNo?.contains(p0, true) == true
                                    || s.sapcode?.contains(p0, true) == true
                                    || s.fixedAssetNo?.contains(p0, true) == true
                                    || s.materialDesc?.contains(p0, true) == true
                                    || s.oldTagNo?.contains(p0, true) == true
                                    || s.equipmentDesc?.contains(p0,true)==true
                                    || s.storagebin?.contains(p0,true)==true)
                        }
                        newItem.addAll(newItem1)
                        initRecyclerView(newItem)
                    }

                }

            }

        })


    }

    private fun initRecyclerView(data: ArrayList<PVData>) {
        val newItem1 : ArrayList<PVData> = ArrayList()
        val equipment : ArrayList<PVData> = ArrayList()
        val material : ArrayList<PVData> = ArrayList()

        equipment.addAll(data.filter { s ->
            s.category.equals("Equipment")
        })

        material.addAll(data.filter { s ->
                !s.category.equals("Equipment")
            }
        )

        binding.idMaterialList.text = getString(R.string.material_list) + " ( "+material.size+" )"
        binding.idEquipmentList.text = getString(R.string.equipment_list) + " ( "+equipment.size+" )"

        newItem1.addAll(
            if (!isMaterialSelected){ data.filter { s ->
                    s.category.equals("Equipment")
                }
            }else{ data.filter { s ->
                    !s.category.equals("Equipment")
                }
            }
        )

        if (newItem1.isNullOrEmpty()) {
            binding.norecordFound.visibility = View.VISIBLE
        } else {
            binding.norecordFound.visibility = View.GONE
        }

        binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CompletedReportAdapter(newItem1, this, requireContext())
        binding.helpRecyclerView.adapter = adapter

        binding.toolbarReportList.idSelect.text = resources.getString(R.string.select)
        binding.btnUpload.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                if (newItem.isNotEmpty()) {
                    increase += 1
                    progressDialog!!.progress = increase

                    for (i in 0 until newItem.size) {
                        iStockApplication.removeCompletedItem(newItem[i])
                    }

                    progressDialog!!.progress = 100
                    progressDialog!!.dismiss()
                    CommonUtils.showAlert(requireContext(), "Uploaded successfully!!")
                    newItem.clear()
                    initRecyclerView(data)
                }

            }
        })
        viewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
            progressDialog!!.dismiss()
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })


        fileUploadViewModel.showSuccessImageClass.observe(viewLifecycleOwner,
            Observer { countries ->
                countries?.let {
                    increase += 1

                    progressDialog!!.progress = increase

                    if (it.isNeedUpload.equals("")) {

                        val projectData = newItem[it.projectIndex!!]

                        if (it.text.equals("pvAudioFile")) {
                            projectData.pvVoiceRecord = it.url
                        }


                        if (it.text.equals("assetImage")) {
                            projectData.assetImages.assetImage[it.index!!] = it.url!!
                        }


                        if (it.text.equals("matImgs")) {
                            //todo new change
                            projectData.assetImages.matImgs[it.index!!] = it.url!!
                        }


                        if (it.text.equals("nameplateImgs")) {
                            projectData.assetImages.nameplateImgs[it.index!!] = it.url!!
                        }


                        if (it.text.equals("namePlateImge")) {
                            projectData.assetImages.namePlateImge!![it.index!!] = it.url!!
                        }

                        if (it.text.equals("namePlateImgeTwo")) {
                            projectData.assetImages.namePlateImgeTwo[it.index!!] = it.url!!
                        }
                        if (it.text.equals("newTagImage")) {
                            projectData.assetImages.newTagImage[it.index!!] = it.url!!
                        }

                        if (it.text.equals("oldTagImage")) {
                            projectData.assetImages.oldTagImage!![it.index!!] = it.url!!
                        }

                        if (it.text.equals("corrosionImage")) {
                            projectData.assetCondition.corrosionImage[it.index!!] = it.url!!
                        }
                        if (it.text.equals("damageImage")) {
                            projectData.assetCondition.damageImage[it.index!!] = it.url!!
                        }

                        if (it.text.equals("leakageImage")) {

                            projectData.assetCondition.leakageImage[it.index!!] = it.url!!
                        }

                        if (it.text.equals("temparatureImage")) {
                            projectData.assetCondition.temparatureImage[it.index!!] = it.url!!
                        }


                        if (it.text.equals("bom_oldTagImg")) {

                            val bomData = projectData.bomModel[it.bomIndex!!]
                            bomData.oldTagImg = it.url!!

                            projectData.bomModel[it.bomIndex!!] = bomData
                        }

                        if (it.text.equals("bom_namePlateImg")) {

                            val bomData = projectData.bomModel[it.bomIndex!!]
                            bomData.namePlateImg = it.url!!

                            projectData.bomModel[it.bomIndex!!] = bomData

                        }

                        if (it.text.equals("bom_barCodeImg")) {

                            val bomData = projectData.bomModel[it.bomIndex!!]
                            bomData.barCodeImg = it.url!!

                            projectData.bomModel[it.bomIndex!!] = bomData
                        }
                        if (it.text.equals("bom_bOMImg")) {

                            val bomData = projectData.bomModel[it.bomIndex!!]
                            bomData.bOMImg = it.url!!

                            projectData.bomModel[it.bomIndex!!] = bomData
                        }

                        newItem[it.projectIndex!!] = projectData

                        if (uploadImage.isNotEmpty()) {
                            uploadImage.removeAt(0)
                        }

                        iStockApplication.saveCompletedItem(projectData)

                        if (uploadImage.isNotEmpty()) {
                            fileUploadViewModel.uploadFile(uploadImage[0], requireContext())
                        } else {
                            progressDialog!!.progress = progressPers
                            progressDialog!!.setMessage("Uploading item to server")
                            progressDialog!!.progress = 0
                            progressDialog!!.max = newItem.size
                            progressPers = newItem.size
                            increase = 0
                            viewModel.saveEquipment(newItem)
                        }
                    }

//                    val imagedata = ImageClass()
//                    imagedata.isNeedUpload = "No"
//                    fileUploadViewModel.showSuccessImageClass.value = imagedata

                }
            })
        fileUploadViewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
            progressDialog!!.dismiss()
        })
        fileUploadViewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {


                if (!progressDialog!!.isShowing && NetworkUtils.isConnected(requireContext())) {
                    progressDialog!!.show()
                }

                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })
    }


    private fun initToolbor() {

        binding.toolbarReportList.toolbarTitle.text = resources.getText(R.string.menu_stock_report)
        binding.toolbarReportList.btnMenu.visibility = View.GONE
        binding.toolbarReportList.btnBack.visibility = View.VISIBLE
        binding.toolbarReportList.btnLogout.visibility = View.GONE
        binding.toolbarReportList.idSelect.visibility = View.VISIBLE

        binding.toolbarReportList.btnBack.setOnClickListener {
            (activity as DashboardActivity?)?.backClicked()
        }

        binding.toolbarReportList.idSelect.setOnClickListener {
            adapter.let {
                if (binding.toolbarReportList.idSelect.text.toString().equals(resources.getString(R.string.select))) {
                    binding.toolbarReportList.idSelect.text = resources.getString(R.string.cancle)
                    it.updateshowSelectIcon(true)
                    binding.btnUpload.visibility = View.VISIBLE
                } else {
                    binding.toolbarReportList.idSelect.text = resources.getString(R.string.select)
                    it.updateshowSelectIcon(false)
                    binding.btnUpload.visibility = View.GONE
                }
            }
        }

        binding.btnUpload.setOnClickListener {
            adapter.let {
                newItem = data.filter {
                        s -> (s.isSelected)
                } as ArrayList<PVData>
                if (newItem.size > 0) {
                    if (!progressDialog!!.isShowing && NetworkUtils.isConnected(requireContext())) {
                        progressDialog!!.show()
                    }

                    progressDialog!!.progress = 0
                    progressDialog!!.setMessage("Uploading Images...")

                    binding.progressbar.visibility = View.VISIBLE
                    uploadImagetoServer()
                } else {
                    Toast.makeText(requireContext(), "Please select at least one item to upload.", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun uploadImagetoServer() {
        pos = 0
        uploadImage.clear()

        for (j in 0 until newItem.size) {
            if (newItem[j].pvVoiceRecord?.isNotEmpty() == true && newItem[j].pvVoiceRecord?.contains("http") == true) {
                val imageData = ImageClass()
                imageData.url = newItem[j].pvVoiceRecord
                imageData.index = 0
                imageData.projectIndex = j
                imageData.text = "pvAudioFile"
                uploadImage.add(imageData)

            }


            for (i in 0 until newItem[j].assetImages.assetImage.size) {
                if (!newItem[j].assetImages.assetImage[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.assetImage[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "assetImage"
                    uploadImage.add(imageData)
                }
            }


            for (i in 0 until newItem[j].assetImages.matImgs.size) {
                if (!newItem[j].assetImages.matImgs[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.matImgs[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "matImgs"
                    uploadImage.add(imageData)
                }
            }


            for (i in 0 until newItem[j].assetImages.nameplateImgs.size) {
                if (!newItem[j].assetImages.nameplateImgs[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.nameplateImgs[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "nameplateImgs"
                    uploadImage.add(imageData)
                }
            }


            for (i in 0 until newItem[j].assetImages.namePlateImge!!.size) {
                if (!newItem[j].assetImages.namePlateImge!![i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.namePlateImge!![i]
                    imageData.index = i
                    imageData.text = "namePlateImge"
                    imageData.projectIndex = j
                    uploadImage.add(imageData)
                }
            }

            for (i in 0 until newItem[j].assetImages.namePlateImgeTwo.size) {
                if (!newItem[j].assetImages.namePlateImgeTwo[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.namePlateImgeTwo[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "namePlateImgeTwo"
                    uploadImage.add(imageData)
                }
            }


            for (i in 0 until newItem[j].assetImages.newTagImage.size) {
                if (!newItem[j].assetImages.newTagImage[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.newTagImage[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "newTagImage"
                    uploadImage.add(imageData)
                }
            }

            for (i in 0 until newItem[j].assetImages.oldTagImage!!.size) {
                if (!newItem[j].assetImages.oldTagImage!![i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetImages.oldTagImage!![i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "oldTagImage"
                    uploadImage.add(imageData)
                }
            }

            for (i in 0 until newItem[j].assetCondition.corrosionImage.size) {
                if (!newItem[j].assetCondition.corrosionImage[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetCondition.corrosionImage[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "corrosionImage"
                    uploadImage.add(imageData)
                }
            }

            for (i in 0 until newItem[j].assetCondition.damageImage.size) {
                if (!newItem[j].assetCondition.damageImage[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetCondition.damageImage[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "damageImage"
                    uploadImage.add(imageData)
                }
            }


            for (i in 0 until newItem[j].assetCondition.leakageImage.size) {
                if (!newItem[j].assetCondition.leakageImage[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].assetCondition.leakageImage[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "leakageImage"
                    uploadImage.add(imageData)
                }
            }

            for (i in 0 until newItem[j].assetCondition.temparatureImage.size) {
                if (!newItem[j].assetCondition.temparatureImage[i].contains("http")) {
                    val imageData = ImageClass()
                    imageData.url =
                        newItem[j].assetCondition.temparatureImage[i]
                    imageData.index = i
                    imageData.projectIndex = j
                    imageData.text = "temparatureImage"
                    uploadImage.add(imageData)
                }
            }

            for (bm in 0 until newItem[j].bomModel.size) {
                if (newItem[j].bomModel[bm].bOMImg?.contains("http") == false) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].bomModel[bm].bOMImg
                    imageData.index = bm
                    imageData.bomIndex = bm
                    imageData.projectIndex = j
                    imageData.projectIndex = j
                    imageData.text = "bom_bOMImg"
                    uploadImage.add(imageData)
                }

                if (newItem[j].bomModel[bm].barCodeImg?.contains("http") == false) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].bomModel[bm].barCodeImg
                    imageData.index = bm
                    imageData.bomIndex = bm
                    imageData.projectIndex = j
                    imageData.projectIndex = j
                    imageData.text = "bom_barCodeImg"
                    uploadImage.add(imageData)
                }

                if (newItem[j].bomModel[bm].namePlateImg?.contains("http") == false) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].bomModel[bm].namePlateImg
                    imageData.index = bm
                    imageData.bomIndex = bm
                    imageData.projectIndex = j
                    imageData.projectIndex = j
                    imageData.text = "bom_namePlateImg"
                    uploadImage.add(imageData)
                }

                if (newItem[j].bomModel[bm].oldTagImg?.contains("http") == false) {
                    val imageData = ImageClass()
                    imageData.url = newItem[j].bomModel[bm].oldTagImg
                    imageData.index = bm
                    imageData.bomIndex = bm
                    imageData.projectIndex = j
                    imageData.projectIndex = j
                    imageData.text = "bom_oldTagImg"
                    uploadImage.add(imageData)
                }
            }
        }

        if (uploadImage.isEmpty()) {
            progressDialog!!.setMessage("Uploading item to server")
            progressDialog!!.progress = 0
            progressDialog!!.max = newItem.size
            progressPers = newItem.size
            increase = 0
            viewModel.saveEquipment(newItem)
        } else {
            progressDialog!!.progress = 0
            progressDialog!!.setMessage("Uploading Images...")
            progressPers = uploadImage.size
            progressDialog!!.max = uploadImage.size
            fileUploadViewModel.uploadFile(uploadImage[0], requireContext())
        }

    }

    override fun itemClicked(data: PVData) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    if (!isMaterialSelected){
                        VerificationDetailsMobileActivity.start(requireContext(), data)
                    }else{
                        VerificationDetailsNewActivity.start(requireContext(), data)
                    }
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {}
            })
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
            .check()
    }



    override fun onDestroy() {
        super.onDestroy()
        fileUploadViewModel.clearallData()
    }
}