package com.example.viewmodeldemo.signUp.activity


import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.network.ApiInterface
import com.example.viewmodeldemo.utils.Method
import com.example.viewmodeldemo.utils.Resource
import com.example.viewmodeldemo.utils.ResponseCodeCheck
import com.example.viewmodeldemo.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SingUpViewModel @Inject constructor(var application: Application) : ViewModel(){

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var signUpRepository: SignUpRepository


    var name: String = ""
    var email: String = ""
    var password: String = ""
    var imagePath: String = ""

    var nameError: String = ""
    var emailError: String = ""
    var passError: String = ""
    var imagePathError: String = ""

    @Inject
    lateinit var method: Method

    var responseCodeCheck: ResponseCodeCheck = ResponseCodeCheck()

    private var mutableData: MutableLiveData<Resource<DataModel>> = MutableLiveData()
    var livedata: LiveData<Resource<DataModel>> = mutableData

    private fun getSomeString(id: Int): String {
        return application.resources.getString(id)
    }

    fun validation(): Boolean {
        nameError = ""
        emailError = ""
        passError = ""
        imagePathError=""

        when {
            name.isEmpty() -> {

                nameError = getSomeString(R.string.plz_enter_name)
                return false
            }
            email.isEmpty() -> {
                emailError = getSomeString(R.string.plz_enter_email)
                return false
            }
            !email.isValidEmail() -> {
                emailError = getSomeString(R.string.plz_valid_email)
                return false
            }
            password.isEmpty() -> {
                passError = getSomeString(R.string.plz_enter_pass)
                return false
            }
            password.length < 6 -> {
                passError = getSomeString(R.string.password_six)
                return false
            }
            imagePath.isEmpty() -> {
                imagePathError=getSomeString(R.string.plz_select_img)

            }
        }
        return true
    }

    fun getApiRegistration(imagePath: String) {

        mutableData.value = Resource.loading(null)

        val file = File(imagePath)
        val requestFile: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            file
        )
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
        val sendName: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), name)
        val sendEmail: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), email)
        val sendPassword: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), password)

        val hashmap: HashMap<String, RequestBody> = HashMap()
        hashmap.apply {
            put("name", sendName)
            put("email", sendEmail)
            put("password", sendPassword)
        }

        val apiInterface = retrofit.create(ApiInterface::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = signUpRepository.getUserSignUpData(body, hashmap)
                mutableData.postValue(responseCodeCheck.getResponseResult(response = repository))
            } catch (e: Exception) {
                Log.d("data_information",e.toString())
                mutableData.postValue(
                    Resource.error(
                        getSomeString(R.string.wrong),
                        null
                    )
                )

            }
        }

    /*  signUpCall.registerApiCall(body, hashmap).enqueue(object : Callback<DataModel?> {
              override fun onResponse(call: Call<DataModel?>, response: Response<DataModel?>) {
                  if (response.isSuccessful) {
                      try {
                          mutableData.postValue(
                              responseCodeCheck.getResponseResult(
                                  Response.success(
                                      response.body()
                                  )
                              )
                          )
                      } catch (_: Exception) {
                          mutableData.postValue(
                              Resource.error(
                                  getSomeString(R.string.wrong),
                                  null
                              )
                          )
                      }
                  }
              }

              override fun onFailure(call: Call<DataModel?>, t: Throwable) {
                  mutableData.postValue(Resource.error("Not Data Found", null))

                  Log.e(TAG, "Not Success")
              }
          })*/

    }
}





