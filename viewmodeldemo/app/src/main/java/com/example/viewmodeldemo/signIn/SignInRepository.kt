package com.example.viewmodeldemo.signIn

import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.network.ApiInterface
import retrofit2.Response
import javax.inject.Inject

class SignInRepository @Inject constructor( var apiInterface:ApiInterface){

    suspend fun getUserLoginData(hashMap: HashMap<String,String>): Response<DataModel> {
        return apiInterface.loginApiCall(hashMap)
    }

}