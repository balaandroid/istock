package com.fertail.istock


import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.databinding.ActivityCompletedReportBinding
import com.fertail.istock.util.SessionManager
import com.fertail.istock.util.UriWithDate
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompletedReportActivity : AppCompatActivity() {
    lateinit var binding: ActivityCompletedReportBinding
    private lateinit var excelReportMaterialAdapter: ExcelReportMaterialAdapter
    private lateinit var excelReportEqipAdapter: ExcelReportEquipmentAdapter
    var appController: iStockApplication? = null
    private lateinit var session:SessionManager

    private val uriListMaterial: MutableList<UriWithDate> = ArrayList()
    private val uriListEquipment: MutableList<UriWithDate> = ArrayList()
    private var isMaterialSelected = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)
        callAdapter()
        clickListner()

    }


    private fun clickListner() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.idEquipmentList.setOnClickListener {
            binding.idMaterialListImage.setBackgroundResource(R.drawable.ic_unselected_circle)
            binding.idEqupmentListImage.setBackgroundResource(R.drawable.ic_selected_circle)
            isMaterialSelected = false
            callAdapter()
        }

        binding.idMaterialListImage.setOnClickListener {
            binding.idMaterialList.post {
                binding.idMaterialList.performClick()
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
            callAdapter()

        }
    }


    private fun callAdapter() {
        binding.reportRv.layoutManager = LinearLayoutManager(this)
       if (!isMaterialSelected){
           excelReportEqipAdapter = ExcelReportEquipmentAdapter(this, ArrayList()){ position ->
               val currentDateTime = uriListEquipment[position].date
               val fileName = "data_equipment_$currentDateTime.xls"
               downloadFileFromUri(this,Uri.parse(uriListEquipment[position].uri),fileName)
           }
           binding.reportRv.adapter = excelReportEqipAdapter
           val equipment = session.getUriArrayListEquipmentNew("excel_equipment")
           if (!equipment.isNullOrEmpty()) { // Add new data
               for (uri in equipment) {
                   if (!uriListEquipment.contains(uri)) {
                       uriListEquipment.clear()
                       uriListEquipment.add(uri)
                   }
               }
               excelReportEqipAdapter.updateList(uriListEquipment)
           }else{
               excelReportEqipAdapter.updateList(ArrayList())
           }

       }else{
           excelReportMaterialAdapter = ExcelReportMaterialAdapter(this, ArrayList()){ position ->
               val currentDateTime = uriListMaterial[position].date
               val fileName = "data_material_$currentDateTime.xls"
               downloadFileFromUri(this,Uri.parse(uriListMaterial[position].uri),fileName)
           }
           binding.reportRv.adapter = excelReportMaterialAdapter
       }

        val uris = session.getUriArrayListNew("excel_material")
        if (uris != null) {
            uriListMaterial.clear()
            uriListMaterial.addAll(uris)
            excelReportMaterialAdapter.updateList(uriListMaterial)
            binding.idNoData.visibility = View.GONE

        }else{
            excelReportMaterialAdapter.updateList(ArrayList())
            binding.idNoData.visibility = View.VISIBLE

        }
    }


    private fun downloadFileFromUri(context: Context, uri: Uri, fileName: String) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            if (inputStream != null) {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadDir, fileName)
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                MediaScannerConnection.scanFile(context,
                    arrayOf(file.absolutePath),
                    null) { path, uri ->
                    // Optional callback when scanning is complete
                    Log.d("Download", "Scanned $path:")
                    Log.d("Download", "-> uri=$uri")
                }
                Toast.makeText(context," downloaded Successfully !!", Toast.LENGTH_SHORT).show()

                Log.d("Download", "File downloaded: ${file.absolutePath}")
            } else {
                Log.e("Download", "Failed to open input stream for URI: $uri")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Download", "Error downloading file from URI: $uri")
        }
    }
}