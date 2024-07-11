package com.fertail.istock.ui.dataclass

import android.net.Uri

data class ImageClass(
    var uri : Uri? = null,
    var url : String? = null,
    var text : String? = null,
    var isNeedUpload : String = "",
    var index : Int? = null,
    var projectIndex : Int? = null,
    var bomIndex : Int? = null
)
