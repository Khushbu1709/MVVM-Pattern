package com.example.viewmodeldemo.signIn

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.databinding.ActivitySignInBinding
import com.example.viewmodeldemo.preferense.Preferences
import com.example.viewmodeldemo.signUp.activity.SignUpActivity
import com.example.viewmodeldemo.utils.Method
import com.example.viewmodeldemo.utils.StatusClass
import com.example.viewmodeldemo.utils.isNetworkAvailable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    lateinit var signInViewModel: SignInViewModel

    @Inject
    lateinit var preferences: Preferences

    @Inject
    lateinit var dialogMethod: Method


    private lateinit var progressDialog: ProgressDialog
    private lateinit var inputMethodManager: InputMethodManager

    companion object {
        var TAG = ActivitySignInBinding::class.java.simpleName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding.signInModel = signInViewModel

        progressDialog = ProgressDialog(this)

        signInViewModel.livedata.observe(this) {
            when (it.status) {
                StatusClass.SUCCESS -> {
                    if (it.data?.status!!) {
                        preferences.setPrefBoolean("login", true)
                        preferences.setPrefString("id", it.data.data.id!!)
                        preferences.setPrefString("email", it.data.data.email!!)
                        dialogMethod.dialog(
                            this,
                            resources.getString(R.string.sign_in),
                            it.data.message!!, 1, "login"
                        )
                        progressDialog.dismiss()
                    } else {
                        dialogMethod.dialog(
                            this,
                            resources.getString(R.string.sign_in),
                            it.data.message!!,
                            0,
                            "login"
                        )
                    }
                    Log.d(TAG, "" + StatusClass.SUCCESS)
                    progressDialog.dismiss()
                }

                StatusClass.ERROR -> {
                    Log.d(TAG, "" + StatusClass.ERROR)
                    dialogMethod.logoutDialog(
                        this,
                        resources.getString(R.string.sign_in),
                        resources.getString(R.string.wrong)
                    )
                    progressDialog.dismiss()
                }

                StatusClass.LOADING -> {
                    progressDialog.setMessage(resources.getString(R.string.loading))
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                }
            }

        }
        binding.btnSignIn.setOnClickListener {

            inputMethodManager.hideSoftInputFromWindow(binding.inputLayoutEmail.windowToken, 0)
            inputMethodManager.hideSoftInputFromWindow(binding.inputLayoutPassword.windowToken, 0)

            signInViewModel.emailError = ""
            signInViewModel.passwordError = ""

            if (signInViewModel.signInValidation()) {
                if (isNetworkAvailable(this)) {
                    signInViewModel.getApiLogin()
                } else {
                    Toast.makeText(this, R.string.internet_connection, Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.inputLayoutEmail.helperText = signInViewModel.emailError
                binding.inputLayoutPassword.helperText = signInViewModel.passwordError

            }

        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }
}