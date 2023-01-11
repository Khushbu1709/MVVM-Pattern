package com.example.viewmodeldemo.signUp.activity

import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.network.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class SignUpRepository @Inject constructor(val apiInterface: ApiInterface){

    suspend fun getUserSignUpData(body: MultipartBody.Part, hashmap: HashMap<String, RequestBody>):Response<DataModel>{
        return apiInterface.registerApiCall(body,hashmap)
    }
}