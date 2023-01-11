package com.example.viewmodeldemo.profile

import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.network.ApiInterface
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(var apiInterface: ApiInterface) {

    suspend fun getUserProfileData(hashMap: HashMap<String, String>): Response<DataModel> {
        return apiInterface.profileApiCall(hashMap)
    }

}