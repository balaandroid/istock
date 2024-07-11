package com.fertail.istock.amazonApi

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader
import java.io.File


class AmazonApi {
//    https://androidrepo.com/repo/jgilfelt-android-simpl3r-android-sdk
    private val YOUR_S3_ACCESS_KEY = ""
    private val YOUR_S3_SECRET = ""
    private val YOUR_S3_BUCKETNAME = ""

    fun uploadFile(context: Context){
        val s3Client = AmazonS3Client(
            BasicAWSCredentials(YOUR_S3_ACCESS_KEY, YOUR_S3_SECRET)
        )

        val file = File("path/to/some.file")
        val s3Key: String = file.getPath()
//
//        val uploader = Uploader(context, s3Client, )
//
//// register listener for upload progress updates
//
//// register listener for upload progress updates
//        uploader.setProgressListener(object : UploadProgressListener() {
//            fun progressChanged(
//                progressEvent: ProgressEvent?,
//                bytesUploaded: Long, percentUploaded: Int
//            ) {
//                // broadcast/notify ...
//            }
//        })

// initiate the upload

// initiate the upload
//        val urlLocation: String = uploader.start()
    }
}