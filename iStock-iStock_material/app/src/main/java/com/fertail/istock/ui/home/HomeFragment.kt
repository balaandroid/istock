package com.fertail.istock.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fertail.istock.*
import com.fertail.istock.amazonApi.S3Uploader
import com.fertail.istock.amazonApi.S3Utils.generates3ShareUrl
import com.fertail.istock.databinding.FragmentHomeBinding
import com.fertail.istock.model.AssetBuilding
import com.fertail.istock.model.AssetCondition
import com.fertail.istock.model.AssetImages
import com.fertail.istock.model.GIS
import com.fertail.istock.model.PVData
import com.fertail.istock.model.PVuser
import com.fertail.istock.model.UserDetailsResponse
import com.fertail.istock.ui.ScannerActivity
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.ui.po.POListActivity
import com.fertail.istock.ui.scan.ScanActivity
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.view_model.BOMListViewModel
import com.fertail.istock.view_model.GetAllMasterViewModel
import com.fertail.istock.view_model.GetNounViewModel
import com.fertail.istock.view_model.GetQRViewModel
import com.fertail.istock.view_model.LocalMasterViewModel
import com.fertail.istock.view_model.PVDataViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    lateinit var viewModel: LocalMasterViewModel
    private var s3uploaderObj: S3Uploader? = null
    lateinit var pvDataViewModel: PVDataViewModel
    lateinit var bomViewModel: BOMListViewModel
    private var hasStartedActivity = false

    private val qrViewModel: GetQRViewModel by viewModels()
    private val getNounViewModel: GetNounViewModel by viewModels()


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root
        s3uploaderObj = S3Uploader(requireContext())

        viewModel = ViewModelProvider(this)[LocalMasterViewModel::class.java]
        pvDataViewModel = ViewModelProvider(this)[PVDataViewModel::class.java]
        bomViewModel = ViewModelProvider(this)[BOMListViewModel::class.java]

        pvDataViewModel.getPVData()
//        bomViewModel.getBOMList()

        updateData()
        observeViewModel()
        initToolbor()
        initViewClick()
        return root
    }

    private fun updateData() {
        val gson = Gson()
        val myType = object : TypeToken<UserDetailsResponse>() {}.type
        val userDetailsResponse = gson.fromJson<UserDetailsResponse>(iStockApplication.appPreference.KEY_USER_DETAILS, myType)

        try {
            binding.title.text = "Hello, " + userDetailsResponse.firstName + " " + userDetailsResponse.lastName + "!"

        }catch (e:Exception){
            e.printStackTrace()
        }

        val pendingItem = iStockApplication.pvDataModel.data.size
        val newItem = iStockApplication.pvDataModel.data.filter { s -> (s.isNewOne) }.size
        val completedItem = iStockApplication.pvDataCompletedModel.data.size
        val totalsize = pendingItem + completedItem

        binding.assignedItem.text = totalsize.toString()
        binding.completedItem.text = completedItem.toString()
        binding.newItem.text = newItem.toString()
        binding.pendingItem.text = pendingItem.toString()

    }

    private fun initViewClick() {
        binding.lnrAssignedItem.setOnClickListener {
            binding.physicalverification.post {
                binding.physicalverification.performClick()
            }
        }

        binding.lnrPendingItem.setOnClickListener {
            binding.physicalverification.post {
                binding.physicalverification.performClick()
            }
        }

        binding.lnrCompletedItem.setOnClickListener {
            binding.idSyncUp.post {
                binding.idSyncUp.performClick()
            }
        }

        binding.homeProfile.setOnClickListener {
            navigateWithBottomNavUpdate(R.id.action_nav_profile)
        }

        binding.physicalverification.setOnClickListener {
            navigateWithBottomNavUpdate(R.id.action_nav_verification)

        }
        binding.homeReport.setOnClickListener {
            navigateWithBottomNavUpdate(R.id.action_nav_report)
        }

        binding.idSyncUp.setOnClickListener {
            navigateWithBottomNavUpdate(R.id.action_nav_report_list)
        }

        binding.idDownload.setOnClickListener {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(requireContext(), object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                qrViewModel.getQRData(action)
                            }
                        })
                    }
                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                .check()

        }

        binding.homeHelp.setOnClickListener {
            navigateWithBottomNavUpdate(R.id.action_nav_stock_help)
        }

        binding.toolbarHome.btnLogout.setOnClickListener {
            CommonUtils.showLogotPopup(requireContext())
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.toolbarHome.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
            Toast.makeText(requireContext(),"You are online!", Toast.LENGTH_SHORT).show()
        } else {
            binding.toolbarHome.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
            Toast.makeText(requireContext(),"You are offline!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun initToolbor() {
        binding.toolbarHome.btnMenu.setOnClickListener {
            (activity as DashboardActivity?)?.openDrawer()
        }

    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(requireContext())){
            binding.toolbarHome.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }else{
            binding.toolbarHome.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
        }
        hasStartedActivity=false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        _binding = null
    }

    private fun observeViewModel() {

        getNounViewModel.showError.observe(this, Observer { isError ->
            Toast.makeText(requireActivity(), isError, Toast.LENGTH_SHORT).show()
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



        qrViewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                if (!hasStartedActivity) {
                    QRScanningDetailActivity.start(requireContext(), it)
                    hasStartedActivity = true
                }
                qrViewModel.showSuccess.value = null
            }
        })


        qrViewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
        })

        qrViewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })


        viewModel.localMasterResponse.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {

            }
        })
        viewModel.localMasterError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
//                binding.progressbar.visibility = if (it) { View.VISIBLE }else { View.GONE }
            }
        })

        pvDataViewModel.showSuccess.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                iStockApplication.updatePVData(it).let {
                    updateData()
                }
            }
        })

        pvDataViewModel.showError.observe(viewLifecycleOwner, Observer { isError ->
            Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
        })

        pvDataViewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                if (it){
                    binding.progressbar.visibility = View.VISIBLE
                }else{
                    binding.progressbar.visibility = View.GONE
                    lifecycleScope.launch {
                        getNounViewModel.getAllDictionary().collectLatest {
                            if (it.isEmpty()) {
                                getNounViewModel.getDictionary()
                            }
                        }
                    }
                }
            }
        })

    }


    private fun navigateWithBottomNavUpdate(actionId: Int) {
        findNavController().navigate(actionId)
        updateBottomNavigationView(actionId)
    }

    private fun updateBottomNavigationView(actionId: Int) {
        when (actionId) {
            R.id.nav_home -> {
                // If you're on the home fragment, select the corresponding item in BottomNavigationView
                // Assuming your BottomNavigationView has an ID like bottomNavView
                (activity as? DashboardActivity)?.binding?.appBarMain!!.includedMain.navigation.selectedItemId=R.id.nav_home
            }
            R.id.action_nav_verification -> {
                (activity as? DashboardActivity)?.binding?.appBarMain!!.includedMain.navigation.selectedItemId=R.id.nav_verification

            }
            R.id.action_nav_report -> {
                (activity as? DashboardActivity)?.binding?.appBarMain!!.includedMain.navigation.selectedItemId=R.id.nav_report

            }
            R.id.action_nav_report_list -> {
                (activity as? DashboardActivity)?.binding?.appBarMain!!.includedMain.navigation.selectedItemId=R.id.nav_report_list

            }
            R.id.action_nav_stock_help -> {
                (activity as? DashboardActivity)?.binding?.appBarMain!!.includedMain.navigation.selectedItemId=R.id.nav_stock_help

            }

            R.id.action_nav_profile -> {
                (activity as? DashboardActivity)?.binding?.appBarMain!!.includedMain.navigation.selectedItemId=R.id.nav_profile

            }

        }
    }

}