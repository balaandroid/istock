package com.fertail.istock.ui.verification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
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
import com.fertail.istock.databinding.FragmentLocationBinding
import com.fertail.istock.iStockApplication
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.ui.LocationConverter
import com.fertail.istock.ui.ScannerActivity
import com.fertail.istock.ui.dataclass.CustomAdapter
import com.fertail.istock.ui.dataclass.CustomBottomSheetDialogFragment
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.ui.dataclass.actionSelected
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.util.NetworkUtils
import com.fertail.istock.view_model.FileUploadViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.*
import java.nio.channels.FileChannel


class LocationFragment : BaseFragment(), actionSelected {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    lateinit var locationManager: LocationManager

    var locationByGps: Location? = null
    var locationByNetwork: Location? = null

    private var uri: Uri? = null

    var numPlateList: ArrayList<ImageClass> = ArrayList()
    var mAction: String = ""
    lateinit var fileUploadViewModel: FileUploadViewModel

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                val uri = it.data!!.data

                if (mAction.equals(NAMEPLATE)) {
                    var data = ImageClass()
                    data.uri = uri
                    numPlateList.add(data)

                    loadNumberPlateImag()
                } else {


                    getContactBitmapFromURI(iStockApplication.appController!!, uri)?.let { it1 ->
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
                                        } else {
                                            VerificationDetailsActivity.mPVData.assetImages.newTagImage.add(
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

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                if (mAction.equals(NAMEPLATE)) {
                    var data = ImageClass()
                    data.uri = uri
                    numPlateList.add(data)

                    loadNumberPlateImag()

                } else {

                    var data = ImageClass()
                    data.uri = uri


                    getContactBitmapFromURI(iStockApplication.appController!!, uri)?.let { it1 ->
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
                                        } else {
                                            VerificationDetailsActivity.mPVData.assetImages.newTagImage.add(
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

//                imageView!!.setImageBitmap(bitmap)
            }
        }


    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"

        galleryLauncher.launch(intent)
//        startActivityForResult(intent, CHOOSE_PHOTO)
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

        cameraLauncher.launch(intent)
//        startActivityForResult(intent, CAPTURE_PHOTO)
    }


    //     FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e.,
// how often you should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient
// has a new Location
    private lateinit var locationCallback: LocationCallback

    // This will store current location info
    private var currentLocation: Location? = null

    var latitude: Double? = null
    var longitude: Double? = null


    val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByGps = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    //------------------------------------------------------//
    val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByNetwork = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    fun loadAssetsImage() {
        val horizontalLayoutManagaer =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyAssets.layoutManager = horizontalLayoutManagaer
        binding.rcyAssets.adapter = CustomAdapter(
            VerificationDetailsActivity.mPVData.assetImages.newTagImage,
            requireActivity()
        )
    }

    fun loadNumberPlateImag() {
        binding.rcyNamePlate.layoutManager = LinearLayoutManager(requireContext())
        binding.rcyNamePlate.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//        binding.rcyNamePlate.adapter = CustomAdapter(numPlateList, requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fileUploadViewModel = ViewModelProvider(this)[FileUploadViewModel::class.java]
        observeViewModel()

        isLocationEnabled()
        initToolbor()
        initCustomTabClick()

        binding.oldTagText.text = VerificationDetailsActivity.mPVData.uniqueId
        binding.ideqpno.text = VerificationDetailsActivity.mPVData.sAPEquipment ?: NoData
        binding.idDescriptionContent.text = VerificationDetailsActivity.mPVData.equipmentDesc
        binding.oldTagNoValue.text = VerificationDetailsActivity.mPVData.oldTagNo

        updateData()

        loadAssetsImage()

        return binding.root
    }

    private fun updateData() {
        binding.newTagText.setText(VerificationDetailsActivity.mPVData.newTagNo)
        binding.lat1.setText(VerificationDetailsActivity.mPVData.gIS?.lattitudeStart)
        binding.lan1.setText(VerificationDetailsActivity.mPVData.gIS?.longitudeStart)
        binding.lat2.setText(VerificationDetailsActivity.mPVData.gIS?.lattitudeEnd)
        binding.lan2.setText(VerificationDetailsActivity.mPVData.gIS?.longitudeEnd)

        binding.lat1.addTextChangedListener(MyTextWatcher("lat1"))
        binding.lan1.addTextChangedListener(MyTextWatcher("lan1"))
        binding.lat2.addTextChangedListener(MyTextWatcher("lat2"))
        binding.lan2.addTextChangedListener(MyTextWatcher("lan2"))
    }

    override fun onResume() {
        super.onResume()
        requestlocation()
    }

    private fun observeViewModel() {
        fileUploadViewModel.showSuccess.observe(viewLifecycleOwner) { countries ->
            countries?.let {
                if (it.isNotEmpty()) {
                    VerificationDetailsActivity.mPVData.assetImages.newTagImage.add(it)
                    loadAssetsImage()
                    fileUploadViewModel.showSuccess.postValue("")
                }

            }
        }
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
            LocationServices.getFusedLocationProviderClient(requireActivity())

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


    private fun initToolbor() {

        binding.toolbarLocation.toolbarTitle.text = resources.getText(R.string.location)

        binding.toolbarLocation.btnMenu.visibility = View.GONE
        binding.toolbarLocation.btnBack.visibility = View.VISIBLE
        binding.toolbarLocation.btnLogout.visibility = View.GONE
        binding.toolbarLocation.btnSearch.visibility = View.GONE

        binding.toolbarLocation.btnBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.getLocation1.setOnClickListener {
            if (currentLocation != null) {
                binding.lat1.setText(LocationConverter.getLatitudeAsDMS(currentLocation, 3))
                binding.lan1.setText(LocationConverter.getLongitudeAsDMS(currentLocation, 3))
            } else {
                CommonUtils.showAlert(
                    requireContext(),
                    "Location data not available now, try after some time!!"
                )
            }

        }

        binding.getLocation2.setOnClickListener {
            if (currentLocation != null) {
                binding.lat2.setText(LocationConverter.getLatitudeAsDMS(currentLocation, 3))
                binding.lan2.setText(LocationConverter.getLongitudeAsDMS(currentLocation, 3))
            } else {
                CommonUtils.showAlert(
                    requireContext(),
                    "Location data not available now, try after some time!!"
                )
            }

        }

        binding.newTagScanner.setOnClickListener {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        ScannerActivity.start(requireContext(), object : actionSelected {
                            override fun optionChosen(action: String, from: String) {
                                binding.newTagText.text = action
                                VerificationDetailsActivity.mPVData.newTagNo = action
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
                                binding.oldTagText.text = action
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

        binding.footer.idBack.visibility = View.VISIBLE

        binding.footer.btnContinue.text = resources.getText(R.string.next)

        binding.footer.btnContinue.setOnClickListener {
            continueClicked()

        }

        binding.footer.idCancle.setOnClickListener {
            requireActivity().finish()
        }

        binding.footer.idBack.setOnClickListener {
            (activity as VerificationDetailsActivity?)?.onBackPressed()
        }

        binding.addAssetImage.setOnClickListener {
            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(ASSETS, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )
        }
        binding.addnameplateImage.setOnClickListener {

            val bottomSheetFilterDialogFragment = CustomBottomSheetDialogFragment(NAMEPLATE, this)
            bottomSheetFilterDialogFragment.show(
                requireActivity().supportFragmentManager,
                "bottomSheetFrag"
            )

        }

        binding.customTab.imgTick1.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick2.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick3.setBackgroundResource(R.drawable.tick_green_icon)
        binding.customTab.imgTick4.setBackgroundResource(R.drawable.tick_orange_four_icon)

    }

    private fun continueClicked() {

        VerificationDetailsActivity.mPVData.gIS.lattitudeStart = binding.lat1.text.toString()
        VerificationDetailsActivity.mPVData.gIS.longitudeStart = binding.lan1.text.toString()

        VerificationDetailsActivity.mPVData.gIS.lattitudeEnd = binding.lat2.text.toString()
        VerificationDetailsActivity.mPVData.gIS.longitudeEnd = binding.lan2.text.toString()

        findNavController().navigate(R.id.action_nav_bom)

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
    }

    var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            currentLocation = location

            val latitude = location.latitude
            val longitude = location.longitude
            val msg = "New Latitude: " + latitude + "New Longitude: " + longitude
//            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
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

    override fun onDestroy() {
        super.onDestroy()

    }


    fun initCustomTabClick() {

        binding.customTab.constrainPhysicalObservation.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainEquipmentInfo.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainPhotos.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.customTab.constrainLocation.setOnClickListener {
        }

        binding.customTab.constrainBom.setOnClickListener {
//            it.findNavController().navigate(R.id.action_nav_bom)
            continueClicked()
        }
    }

    fun showAlert() {
        Toast.makeText(requireContext(), "Fill Asset Conditions first", Toast.LENGTH_SHORT).show()
    }

    @Throws(IOException::class)
    private fun copyFile(src: File, dst: File) {
        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null
        try {
            inChannel = FileInputStream(src).getChannel()
            outChannel = FileOutputStream(dst).getChannel()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } finally {
            if (inChannel != null) inChannel.close()
            if (outChannel != null) outChannel.close()
        }
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


    class MyTextWatcher(val type: String) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            if (type.equals("lat1")) {
                VerificationDetailsActivity.mPVData.gIS?.lattitudeStart = p0.toString()
            }


            if (type.equals("lan1")) {
                VerificationDetailsActivity.mPVData.gIS?.longitudeStart = p0.toString()
            }


            if (type.equals("lat2")) {
                VerificationDetailsActivity.mPVData.gIS?.lattitudeEnd = p0.toString()
            }

            if (type.equals("lan2")) {
                VerificationDetailsActivity.mPVData.gIS?.longitudeEnd = p0.toString()
            }

        }

    }


}