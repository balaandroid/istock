package com.fertail.istock

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fertail.istock.databinding.ActivityCompletedReportBinding
import com.fertail.istock.iStockApplication.Companion.getWorkbookUriList
import com.fertail.istock.util.SessionManager
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompletedReportActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompletedReportBinding
    lateinit var excelReportMaterialAdapter: ExcelReportMaterialAdapter
    lateinit var excelReportEqipAdapter: ExcelReportEquipmentAdapter
    var appController: iStockApplication? = null
    private lateinit var session:SessionManager
    private var uriListMaterial=ArrayList<Uri>()
    private var uriListEquipment=ArrayList<Uri>()
    var isMaterialSelected = true
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
               val currentDateTime = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
               val fileName = "data_equipment_$currentDateTime.xls"
               downloadFileFromUri(this,uriListEquipment[position],fileName)
           }
           binding.reportRv.adapter = excelReportEqipAdapter

           val equipment = session.getUriArrayListEquipment("excel_equipment")
           if (equipment != null) {
               for (uri in equipment) {
                   if (!uriListEquipment.contains(uri)) {
                       uriListEquipment.add(uri)
                   }
               }
               excelReportEqipAdapter.updateList(uriListEquipment)
           }else{
               excelReportEqipAdapter.updateList(ArrayList())
           }

       }else{
           excelReportMaterialAdapter = ExcelReportMaterialAdapter(this, ArrayList()){ position ->
               val currentDateTime = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
               val fileName = "data_material_$currentDateTime.xls"
               downloadFileFromUri(this,uriListMaterial[position],fileName)

           }
           binding.reportRv.adapter = excelReportMaterialAdapter
       }

        val uris = session.getUriArrayList("excel_material")
        if (uris != null) {
            uriListMaterial.addAll(uris)
            excelReportMaterialAdapter.updateList(uriListMaterial)
        }else{
            excelReportMaterialAdapter.updateList(ArrayList())
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

                MediaScannerConnection.scanFile(
                    context,
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