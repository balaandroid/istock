package com.fertail.istock.ui.verification

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.*
import com.fertail.istock.databinding.FragmentVerificationBinding
import com.fertail.istock.model.*
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.ReportClickListener
import com.fertail.istock.ui.bottomsheet.ChooseMassterBottomsheet
import com.fertail.istock.ui.bottomsheet.FunctionalLocationChooseBottomsheet
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.view_model.GetAllMasterViewModel
import com.fertail.istock.view_model.PVDataViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class VerificationFragment : BaseFragment(), ReportClickListener {
    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    lateinit var  viewModel: PVDataViewModel
    private val masterViewModel: GetAllMasterViewModel by viewModels()


    private val data = ArrayList<PVData>()
    lateinit var adapter: VerificationReportAdapter
    var butnClicked = false
    var isMaterialSelected = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[PVDataViewModel::class.java]
        initToolbor()
        observeViewModel()
        initViewClick()
        btnAddNew(true)
        initRecyclerView(data)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(requireContext())){
            binding.toolbarVerification.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }else{
            binding.toolbarVerification.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
        }
        viewModel.getPVData()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.toolbarVerification.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
            Toast.makeText(requireContext(),"You are online!",Toast.LENGTH_SHORT).show()
        } else {
            binding.toolbarVerification.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
            Toast.makeText(requireContext(),"You are offline!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewClick() {
        binding.idFarID.setOnClickListener {
            if (iStockApplication.appPreference.KEY_All_Master.equals("")) {
                masterViewModel.getAllMaster()
            } else {
                var modalBottomSheet : ChooseMassterBottomsheet? = null

                modalBottomSheet = ChooseMassterBottomsheet( object : ItemSelectedForNoune {
                    override fun selectedItem(noun: String) {

                        val splittedArray = noun.split("##")

                        binding.idFarID.text = splittedArray[0]
                        binding.idRegion.text = splittedArray[1]

                        val newItem: ArrayList<PVData> = ArrayList()
                        val newItem1 = data.filter { s ->
                            (s.fixedAssetNo?.contains(splittedArray[0], true) == true)
                        }
                        newItem.addAll(newItem1)
                        initRecyclerView(newItem)

                        modalBottomSheet?.dismiss()
                    }
                })

                modalBottomSheet?.show(childFragmentManager, FunctionalLocationChooseBottomsheet.TAG)
            }
        }

        binding.idMaterialListImage.setOnClickListener {
            binding.idMaterialList.post {
                binding.idMaterialList.performClick()
            }
        }

        binding.idEquipmentList.setOnClickListener {
            binding.idMaterialListImage.setBackgroundResource(R.drawable.ic_unselected_circle)
            binding.idEqupmentListImage.setBackgroundResource(R.drawable.ic_selected_circle)
            isMaterialSelected = false
            btnAddNew(false)
            initRecyclerView(data)
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
            btnAddNew(true)
            initRecyclerView(data)
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
                        val newItem: ArrayList<PVData> = ArrayList()
                        val newItem1 = data.filter { s ->
                            (s.assetNo?.contains(p0, true) == true
                                    || s.sapcode?.contains(p0, true) == true
                                    || s.fixedAssetNo?.contains(p0, true) == true
                                    || s.oldTagNo?.contains(p0, true) == true
                                    || s.equipmentDesc?.contains(p0,true)==true
                                    || s.stroageBin?.contains(p0,true)==true
                                    || s.stroageLocation?.contains(p0,true)==true)
                        }
                        newItem.addAll(newItem1)
                        initRecyclerView(newItem)
                    }

                }

            }

        })
    }


    private fun btnAddNew(update:Boolean){
        if (update){
            binding.addnew.visibility=View.GONE
        }else{
            binding.addnew.visibility=View.VISIBLE
        }

        binding.addnew.setOnClickListener {
                butnClicked = true
                viewModel.getCurrentLoc()

        }

    }

    private fun initRecyclerView(data: ArrayList<PVData>) {
        val newItem1 : ArrayList<PVData> = ArrayList()

        newItem1.addAll(
            if (!isMaterialSelected){
                data.filter { s ->
                    s.category.equals("Equipment")
                }
            }else{
                data.filter { s ->
                    !s.category.equals("Equipment")
                }
            })

        val equipment : ArrayList<PVData> = ArrayList()
        val material : ArrayList<PVData> = ArrayList()

        equipment.addAll(data.filter { s ->
            s.category.equals("Equipment")
        })

        material.addAll(data.filter { s ->
            !s.category.equals("Equipment")
        })

        Log.e("TAG","data1    ${equipment.size}")
        Log.e("TAG","data2    ${material.size}")

        binding.idMaterialList.text = getString(R.string.material_list) + " ( "+material.size+" )"
        binding.idEquipmentList.text = getString(R.string.equipment_list) + " ( "+equipment.size+" )"


        binding.itemCount.text = "Count: " + newItem1.size + ""

        binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.norecordFound.visibility = if (newItem1.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val hasNonNullCategory=data.any {
            it.category!=null
        }


        val adapter=if(!isMaterialSelected && hasNonNullCategory){
            VerificationReportLatestAdapter(newItem1,this)

        }else{
            val sortedList = newItem1.sortedBy { it.stroageBin }
            print("data1>>>>>>>>>>>>>>>>>>>>$sortedList")
            VerificationReportAdapter(sortedList,this)
        }
        binding.helpRecyclerView.adapter=adapter

    }

    private fun initToolbor() {
        binding.toolbarVerification.toolbarTitle.text = resources.getText(R.string.menu_physical_verification)
        binding.toolbarVerification.btnMenu.visibility = View.GONE
        binding.toolbarVerification.btnBack.visibility = View.VISIBLE
        binding.toolbarVerification.btnLogout.visibility = View.GONE
        binding.toolbarVerification.btnRefresh.visibility = View.VISIBLE
        binding.toolbarVerification.btnSearch.visibility = View.GONE

        binding.toolbarVerification.btnBack.setOnClickListener {
            (activity as DashboardActivity?)?.backClicked()

        }

        binding.toolbarVerification.btnLogout.setOnClickListener {
            CommonUtils.showLogotPopup(requireContext())
        }

        binding.toolbarVerification.btnRefresh.setOnClickListener {
            viewModel.getPVDataFromServer()
        }

    }

    override fun itemClicked(data: PVData) {
        if (data.action.isNullOrEmpty()){
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

        }else{
            CommonUtils.showAlertWithCancel(
                requireContext(), "Item marked as completed",
                object : CommonInterface {
                    override fun btnPositiveSelected(dialog: DialogInterface) {
                        val gson = Gson()
                        val myType = object : TypeToken<UserDetailsResponse>() {}.type
                        val userDetailsResponse = gson.fromJson<UserDetailsResponse>(
                            iStockApplication.appPreference.KEY_USER_DETAILS,
                            myType)


                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                        val currentDate = sdf.format(Date())
                        System.out.println(" C DATE is  $currentDate")

                        data.isSaved = true
                        data.mVerificateionDate = currentDate
                        data.mVerifiedBy = userDetailsResponse.userid

                        var i = 0
                        var id = -1
                        for (x in iStockApplication.pvDataModel.data) {

                            if (x.assetNo.equals(data.assetNo)) {
                                id = i
                                break
                            }

                            i += 1
                        }

                        if (id != -1) {
                            iStockApplication.pvDataModel.data.removeAt(id)
                        }

                        iStockApplication.saveData(iStockApplication.pvDataModel.data)
                        iStockApplication.saveCompletedItem(data)

                        dialog.dismiss()
                        viewModel.getPVData()
                    }

                    override fun btnNegativeSelected(dialog: DialogInterface) {
                        dialog.dismiss()
                    }

                })
        }


    }

    private fun observeViewModel() {
        viewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                data.clear()
                data.addAll(it)
                initRecyclerView(data)
                print("data>>>>>>>>>>>>>>$data")
            }
        })

        viewModel.getCurrentLocSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                if (butnClicked) {
                    val gson = Gson()
                    val myType = object : TypeToken<UserDetailsResponse>() {}.type
                    val userDetailsResponse = gson.fromJson<UserDetailsResponse>(
                        iStockApplication.appPreference.KEY_USER_DETAILS,
                        myType
                    )


                    val tempData = ArrayList<PVData>()
                    it.assetNo = System.currentTimeMillis().toString()
                    it.isNewOne = true
                    it.itemType = "New"
                    it.pVuser = PVuser()
                    it.pVuser!!.userId = userDetailsResponse.userid
                    it.pVuser!!.name = userDetailsResponse.userName
                    it.assetImages = AssetImages()
                    it.assetCondition = AssetCondition()
                    it.gIS = GIS()
                    it.assetBuilding = AssetBuilding()

                    tempData.add(it)
//                    itemClicked(it)

                    iStockApplication.updatePVData(tempData).let {
                        data.clear()
                        data.addAll(it)
                        initRecyclerView(data)
                    }

                    butnClicked = false
                }

            }
        })

        viewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
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
    }

}