package com.fertail.istock.amazonApi

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.create


class UploadImage {

    suspend fun uploadFile(){


        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = "image/PNG".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.Companion.create(mediaType, "<file contents here>")
        val request: Request = Request.Builder()
            .url("https://dgmdr92vab.execute-api.ap-south-1.amazonaws.com/dev/upload")
            .method("POST", body)
            .addHeader("file-name", "my_image_test6.png")
            .addHeader("x-api-key", "2WdNu6vMl16CXxpHaK2Av9WB3nU1qcRc52K1T35i")
            .addHeader("Content-Type", "image/PNG")
            .build()
        val response: Response = client.newCall(request).execute()
    }


}