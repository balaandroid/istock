package com.fertail.istock.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.fertail.istock.*
import com.fertail.istock.internetChecking.NetworkChangeReceiver
import java.io.File


object CommonUtils {

    /**
     * Check the Internet connection available status
     *
     * @param context - Context environment passed by this parameter
     * @return boolean true if the Internet Connection is Available and false otherwise
     */

    private lateinit var session:SessionManager

    fun showLog(message: String){
        Log.e("iStock", message )
    }

    fun showAlert(context: Context,  message : String) {
        var builder: AlertDialog.Builder? = null
        builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.setTitle(context.resources.getString(R.string.app_name))
        alert.show()

    }

    fun showAlert(context: Context,  message : String, commonInterface: CommonInterface) {
        var builder: AlertDialog.Builder? = null
        builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                commonInterface.btnPositiveSelected(dialog)
            }
        val alert = builder.create()
        alert.setTitle(context.resources.getString(R.string.app_name))
        alert.show()

    }

    fun showAlertWithCancel(context: Context,  message : String, commonInterface: CommonInterface) {
        var builder: AlertDialog.Builder? = null
        builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                commonInterface.btnPositiveSelected(dialog)
            }.setNegativeButton("Cancel") {  dialog, id ->
                commonInterface.btnNegativeSelected(dialog)
            }
        val alert = builder.create()
        alert.setTitle(context.resources.getString(R.string.app_name))
        alert.show()

    }


    fun showLogotPopup(context: Context){

        if(iStockApplication.pvDataCompletedModel.data.isNotEmpty()) {
            showAlert(context, "Please upload all completed item before you logout")
            return
        }

        var builder: AlertDialog.Builder? = null
        builder = AlertDialog.Builder(context)

        builder.setMessage("Are you sure to want logout ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                iStockApplication.appPreference.clearAppPreference()
                LoginActivity.start(context)
            }
            .setNegativeButton("No") { dialog, id -> //  Action for 'NO' Button
                dialog.cancel()
            }
        val alert = builder.create()
        alert.setTitle( context.resources.getString(R.string.app_name))
        alert.show()
    }


    fun showApiLogout(context: Context, url: String){
        session= SessionManager(context)
        var builder: AlertDialog.Builder? = null
        builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.app_name) +" Confirm URL Selection")
        builder.setMessage("Are you sure you want to select this URL? If yes, the app will refresh to use the selected base URL.")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                iStockApplication.appPreference.clearAppPreference()
                LoginActivity.start(context)
                iStockApplication.appPreference.KEY_ACCESS_TOKEN_TYPE=url
            }
            .setNegativeButton("No") { dialog, id -> //  Action for 'NO' Button
                dialog.cancel()
            }
        val alert = builder.create()
//        alert.setTitle( context.resources.getString(R.string.app_name))
        alert.show()
    }

    fun getCapturedImageOrientation(context: Context, imageUri: Uri): Int {
        var rotate = 0
        try {
            context.contentResolver.notifyChange(imageUri, null)
            val imageFile = imageUri.path?.let { File(it) }
            val exif = imageFile?.let { ExifInterface(it.getAbsolutePath()) }
            val orientation: Int = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: 0
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    fun getRotateAngle(context: Context, imageUri: Uri?): Int {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION)
        val cursor = context.contentResolver.query(imageUri!!, columns, null, null, null)
            ?: //If null, it is not in the gallery, so may be temporary image
            return getCapturedImageOrientation(context, imageUri)
        cursor.moveToFirst()
        val orientationColumnIndex = cursor.getColumnIndex(columns[1])
        val orientation = cursor.getInt(orientationColumnIndex)
        cursor.close()
        return orientation
    }

    fun rotateImage(degrees: Int, mCropImage: Bitmap): Bitmap {
        val mat = Matrix()
        mat.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(
            mCropImage,
            0,
            0,
            mCropImage.width,
            mCropImage.height,
            mat,
            true
        )
    }

    fun decodeFile(path: String?): Bitmap? { //you can provide file path here
        val orientation: Int
        return try {
            if (path == null) {
                return null
            }
            // decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            // Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 70
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 0
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE
                ) break
                width_tmp /= 2
                height_tmp /= 2
                scale++
            }
            // decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val bm = BitmapFactory.decodeFile(path, o2)
            var bitmap = bm
            val exif = ExifInterface(path)
            orientation = exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            Log.e("ExifInteface .........", "rotation =$orientation")

            //          exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);
            Log.e("orientation", "" + orientation)
            val m = Matrix()
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                m.postRotate(180f)
                //              m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation)
                bitmap = Bitmap.createBitmap(
                    bm, 0, 0, bm.width,
                    bm.height, m, true
                )
                return bitmap
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90f)
                Log.e("in orientation", "" + orientation)
                bitmap = Bitmap.createBitmap(
                    bm, 0, 0, bm.width,
                    bm.height, m, true
                )
                return bitmap
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270f)
                Log.e("in orientation", "" + orientation)
                bitmap = Bitmap.createBitmap(
                    bm, 0, 0, bm.width,
                    bm.height, m, true
                )
                return bitmap
            }
            bitmap
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun getAppVersion() : String{
        return "App Version : ${BuildConfig.VERSION_NAME}"
    }


    fun isConnectedToInternet(context: Context?): Boolean {
        return try {
            if (context != null) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
            false
        } catch (e: java.lang.Exception) {
            Log.e(NetworkChangeReceiver::class.java.name, e.message!!)
            false
        }
    }
}