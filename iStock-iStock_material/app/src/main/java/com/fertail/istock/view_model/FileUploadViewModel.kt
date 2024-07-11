package com.fertail.istock.view_model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fertail.istock.Constant
import com.fertail.istock.iStockApplication
import com.fertail.istock.ui.dataclass.ImageClass
import com.fertail.istock.util.NetworkUtils
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File


class FileUploadViewModel : ViewModel(), Constant {

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<String>()
    val showSuccessImageClass = MutableLiveData<ImageClass>()
    val showError = MutableLiveData<String>()
    val showLoading = MutableLiveData<Boolean>()

    val oldurl = "https://dgmdr92vab.execute-api.ap-south-1.amazonaws.com/dev/upload"
    val oldKey = "2WdNu6vMl16CXxpHaK2Av9WB3nU1qcRc52K1T35i"
    val oldDownloadableURL = "https://dwd86npks63nx.cloudfront.net/"

//    val newurl = "https://3sifoxk887.execute-api.ap-south-1.amazonaws.com/dev/upload"
//    val newKey = "bmRZIxWEIWWahPAMF1TC416ghciZc3f3hBf3Rlj0"
//    val newDownloadableURL = "https://d1tmzlpsck2wsh.cloudfront.net/"
//
    val newurl = "https://w6z8s5dwce.execute-api.me-south-1.amazonaws.com/dev/upload"
    val newKey = "Pw3IgcUkno9xGTf3VtUBn2Gmi9rKA3Sb7Fh8liIH"
    val newDownloadableURL = "https://d30f70hjt97rz5.cloudfront.net/"


    val newurl_wcb = "https://i0fxakkk4e.execute-api.ap-south-1.amazonaws.com/dev/upload"
    val newKey_wcb = "2ojFIeiOcG3sZq8jpAPvw5t4IzH7yqtG4Ili126W"
    val newDownloadableURL_wcb = "https://d8j8r4t3hw2xc.cloudfront.net/"

    fun clearallData() {
        val imagedata = ImageClass()
        imagedata.isNeedUpload = "No"
        showSuccessImageClass.value = imagedata
    }

    fun uploadFile(fileName: String, file: File, context: Context) {
        if (NetworkUtils.isConnected(iStockApplication.appController!!)) {
            showLoading.value = true
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

                val client = OkHttpClient().newBuilder().build()
                val type = "image/jpeg"

                Log.e("TAG", "uploadFile ==>> file-name: $fileName")
                Log.e("TAG", "uploadFile ==>> type: $type")
                getURL()

                val mediaType: MediaType? = type?.toMediaTypeOrNull()
                val body: RequestBody = RequestBody.Companion.create(mediaType, file)
                val request: Request = Request.Builder()
                    .url(getURL())
                    .method("POST", body)
                    .addHeader("file-name", fileName)
                    .addHeader("x-api-key", getKey())
                    .addHeader("Content-Type", type)
                    .build()
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    showSuccess.postValue(getDownloadUrl() + fileName)
                } else {
                    showError.postValue("File upload filed, please try again later")
                }

                showLoading.postValue(false)

                Log.e("TAG", "uploadFile ==>> uploadFile: " + response.body)

            }

        } else {
            onError("Error : $check_connection_for_upload ")
        }
    }

    private fun getURL(): String {

        Log.e("TAG", "getURL: ==>>>"+ if (iStockApplication.isWCBURL) {newurl_wcb}else{newurl})

        return  if (iStockApplication.isWCBURL) {newurl_wcb}else{newurl}
    }

    fun uploadFile(imageData: ImageClass, context: Context) {

        val file = File(imageData.url!!)
        val fileName = file.name

        if (NetworkUtils.isConnected(iStockApplication.appController!!)) {
            showLoading.value = true
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

                val client = OkHttpClient().newBuilder()
                    .build()
                val type = "image/jpeg"

                Log.e("TAG", "okhttp uploadFile ==>> file-name: $fileName")
                Log.e("TAG", "okhttp uploadFile ==>> type: $type")
                getURL()

                val mediaType: MediaType? = type?.toMediaTypeOrNull()
                val body: RequestBody = RequestBody.Companion.create(mediaType, file)
                val request: Request = Request.Builder()
                    .url(getURL())
                    .method("POST", body)
                    .addHeader("file-name", fileName)
                    .addHeader("x-api-key", getKey())
                    .addHeader("Content-Type", type!!)
                    .build()
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    imageData.url = getDownloadUrl() + fileName
                    showSuccessImageClass.postValue(imageData)
                    Log.e("TAG", "okhttp uploadFile ==>> uploadFile: " + response.body)
                } else {
                    showError.postValue("File upload filed, please try again later")
                    Log.e("TAG", "okhttp uploadFile ==>> uploadFile: please try again later ")
                }

                showLoading.postValue(false)


            }

        } else {
            onError("Error : $check_connection_for_upload ")
        }
    }

    private fun getDownloadUrl(): String {
        return if (iStockApplication.isWCBURL) {newDownloadableURL_wcb } else {newDownloadableURL}
    }

    private fun getKey(): String {

        return if (iStockApplication.isWCBURL) {newKey_wcb} else {newKey}
    }


    fun getMimeType(file: File?, context: Context): String? {
        val uri: Uri = Uri.fromFile(file)
        val cR: ContentResolver = context.getContentResolver()
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun onError(message: String) {
        showError.postValue(message)
        showLoading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}