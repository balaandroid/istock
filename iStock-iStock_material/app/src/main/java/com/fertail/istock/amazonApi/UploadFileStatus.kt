package com.fertail.istock.amazonApi

sealed class UploadFileStatus{
    data class FileStatus(val status: Int): UploadFileStatus()
    data class Error(val exception: Throwable): UploadFileStatus()
    data class Complete(val s3Url: String): UploadFileStatus()
    data class Start(val start: Boolean): UploadFileStatus()
}