package com.example.viewmodeldemo.signIn

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.model.DataModel
import com.example.viewmodeldemo.utils.Resource
import com.example.viewmodeldemo.utils.ResponseCodeCheck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(var context: Context) : ViewModel() {

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var signInRepository: SignInRepository


    companion object {
        var TAG: String = SignInActivity::class.java.simpleName
    }

    private var responseCodeCheck: ResponseCodeCheck = ResponseCodeCheck()

    private var mutableData: MutableLiveData<Resource<DataModel>> = MutableLiveData()
    var livedata: LiveData<Resource<DataModel>> = mutableData

    var edtEmail: String = ""
    var edtPass: String = ""
    var id: String = ""


    var emailError = ""
    var passwordError = ""

    private fun getString(plzEnterEmail: Int): String {
        return context.resources.getString(plzEnterEmail)
    }

    fun signInValidation(): Boolean {

        if (edtEmail.isEmpty()) {
            emailError = getString(R.string.plz_enter_email)
            return false
        }
        if (edtPass.isEmpty()) {
            passwordError = getString(R.string.plz_enter_pass)
            return false
        }
        return true
    }

    fun getApiLogin() {

        mutableData.value = Resource.loading(null)

        val hashmap: HashMap<String, String> = HashMap()
        hashmap.apply {
            put("email", edtEmail)
            put("password", edtPass)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                mutableData.postValue(
                    responseCodeCheck.getResponseResult(
                        response = signInRepository.getUserLoginData(
                            hashmap
                        )
                    )
                )

            } catch (e: Exception) {

                mutableData.postValue(Resource.error(getString(R.string.wrong), null))
                Log.d(TAG, e.toString())

            }

        }

        /*loginCall.loginApiCall(hashmap).enqueue(object : Callback<DataModel?> {
                override fun onResponse(call: Call<DataModel?>, response: Response<DataModel?>) {
                    if (response.body() != null) {
                        try {
                            mutableData.postValue(
                                responseCodeCheck.getResponseResult(
                                    Response.success(
                                        response.body()
                                    )
                                )
                            )
                        } catch (e: Exception) {
                            mutableData.postValue(
                                Resource.error(
                                    context.resources.getString(R.string.wrong),
                                    null
                                )
                            )
                        }
                    }
                }
                override fun onFailure(call: Call<DataModel?>, t: Throwable) {
                    Log.d(TAG, "" + t.message.toString())
                    mutableData.postValue(Resource.error(context.resources.getString(R.string.wrong), null))
                }
            })*/

    }

}


