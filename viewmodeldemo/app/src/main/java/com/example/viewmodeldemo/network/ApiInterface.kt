package com.example.viewmodeldemo.network

import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.utils.BaseUrlPath.login
import com.example.viewmodeldemo.utils.BaseUrlPath.profile
import com.example.viewmodeldemo.utils.BaseUrlPath.register
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {

    //Login
    @FormUrlEncoded
    @POST(login)
    suspend fun loginApiCall(@FieldMap hashMap: HashMap<String, String>): Response<DataModel>

    //Register
    @Multipart
    @POST(register)
    suspend fun registerApiCall(
        @Part image: MultipartBody.Part,
        @PartMap hashMap: HashMap<String, RequestBody>
    ): Response<DataModel>

    //Profile
    @FormUrlEncoded
    @POST(profile)
    suspend fun profileApiCall(@FieldMap hashMap: HashMap<String, String>): Response<DataModel>


}