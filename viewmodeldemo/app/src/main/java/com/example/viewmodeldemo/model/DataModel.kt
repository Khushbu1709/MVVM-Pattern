package com.example.viewmodeldemo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataModel(
    @SerializedName("data")
    var`data`:Data,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("message")
    var message: String?
) : Serializable

data class Data(
    @SerializedName("id")
    var id:String?,
    @SerializedName("name")
    var name:String?,
    @SerializedName("email")
    var email:String?,
    @SerializedName("profile_picture")
    var profilePicture:String?

):Serializable

