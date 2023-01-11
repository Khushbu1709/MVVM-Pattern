package com.example.viewmodeldemo.profile

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.network.ApiInterface
import com.example.viewmodeldemo.network.ApiServices
import com.example.viewmodeldemo.preferense.Preferences
import com.example.viewmodeldemo.utils.Resource
import com.example.viewmodeldemo.utils.ResponseCodeCheck
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(var application: Application) : ViewModel(){

    lateinit var id: String
    private lateinit var preferences: Preferences

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var retrofit: Retrofit

    private var responseCodeCheck: ResponseCodeCheck = ResponseCodeCheck()
    private var mutableLiveData: MutableLiveData<Resource<DataModel>> = MutableLiveData()
    var liveData: LiveData<Resource<DataModel>> = mutableLiveData

    fun getProfileCall(activity: Activity) {

        preferences = Preferences(activity)
        id = preferences.getPrefString("id")!!

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.apply {
            put("user_id", id)
        }

        mutableLiveData.value=Resource.loading(null)


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<DataModel> = profileRepository.getUserProfileData(hashMap)
                mutableLiveData.postValue(responseCodeCheck.getResponseResult(response = response))
            } catch (e: Exception) {
                mutableLiveData.postValue(
                    Resource.error(
                        application.getString(R.string.wrong), null
                    )
                )
                Log.d("data_information", e.toString())
            }
        }

        /*profileCall.profileApiCall(hashMap).enqueue(object : Callback<DataModel?> {
            override fun onResponse(call: Call<DataModel?>, response: Response<DataModel?>) {

                if (response.isSuccessful) {
                    try {

                        mutableLiveData.postValue(
                            responseCodeCheck.getResponseResult(
                                Response.success(
                                    response.body()
                                )
                            )
                        )
                    } catch (e: Exception) {
                        mutableLiveData.postValue(
                            Resource.error(
                                e.message.toString(),
                                response.body()
                            )
                        )
                    }

                } else {
                    mutableLiveData.postValue(
                        Resource.error(
                            getApplication<Application>().getString(R.string.wrong), null
                        )
                    )
                }
            }

            override fun onFailure(call: Call<DataModel?>, t: Throwable) {
                Log.d(TAG, "" + t.message)
                mutableLiveData.postValue(
                    Resource.error(
                        getApplication<Application>().getString(R.string.wrong), null
                    )
                )
            }
        })*/


    }


}