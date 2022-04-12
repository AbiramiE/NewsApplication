package com.newsapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Source : Serializable{
    @SerializedName("id")
    @Expose
    var id: Any? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}