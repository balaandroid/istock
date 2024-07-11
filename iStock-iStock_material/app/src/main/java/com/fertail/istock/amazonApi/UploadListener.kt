package com.fertail.istock.amazonApi

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import java.lang.Exception

class UploadListener: TransferListener {

    override fun onStateChanged(id: Int, state: TransferState?) {
        TODO("Not yet implemented")
    }

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        TODO("Not yet implemented")
    }

    override fun onError(id: Int, ex: Exception?) {
//        Log.e(TAG, "Error during upload: " + id, e);
//        s3UploadInterface.onUploadError(e.toString());
//        s3UploadInterface.onUploadError("Error");
    }
}