package com.example.viewmodeldemo.signUp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.databinding.ActivitySignUpBinding
import com.example.viewmodeldemo.signIn.SignInActivity
import com.example.viewmodeldemo.utils.*
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    @Inject
    lateinit var method: Method

    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SingUpViewModel
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var launcherPermission: ActivityResultLauncher<String>
    private var isCamara: Boolean = false

    companion object {
        var TAG = ActivitySignUpBinding::class.java.simpleName!!
        const val CAMERA_REQUEST_CODE = 101
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        signUpViewModel = ViewModelProvider(this)[SingUpViewModel::class.java]
        progressDialog = ProgressDialog(this)


        binding.signUpViewModel = signUpViewModel

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        signUpViewModel.livedata.observe(this) {
            when (it.status) {
                StatusClass.SUCCESS -> {
                    Log.d("data_information", "" + it.data?.status)
                    if (it.data?.status!!) {
                        method.dialog(
                            this,
                            resources.getString(R.string.sign_up),
                            it.data.message!!, 1, "signup"
                        )
                    } else {
                        method.dialog(
                            this,
                            resources.getString(R.string.sign_up),
                            it.data.message!!, 0, "signup"
                        )
                    }
                    progressDialog.dismiss()

                }
                StatusClass.ERROR -> {
                    Log.d(TAG, "" + StatusClass.ERROR)
                    progressDialog.dismiss()

                }
                StatusClass.LOADING -> {
                    progressDialog.setMessage(resources.getString(R.string.loading))
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    Log.d(TAG, "" + StatusClass.LOADING)

                }
            }
        }



        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (isCamara) {
                        openCamera()
                    } else {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                } else {
                    val showRationale: Boolean = if (isCamara) {
                        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    } else {
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    if (!showRationale) {
                        val deniedDialog = AlertDialog.Builder(this)

                        deniedDialog.setTitle(R.string.permission_denied)
                        deniedDialog.setMessage(R.string.permission_setting)
                        deniedDialog.setPositiveButton(
                            R.string.setting
                        ) { _, _ ->
                            showPermissionDeniedDialog()
                        }
                        deniedDialog.setNegativeButton(R.string.cancel, null)
                        deniedDialog.show()
                    }
                }
            }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.profilePicture.setImageURI(uri).toString()
                signUpViewModel.imagePath = PathUtil.getPath(this, uri)!!

                binding.imageDeleteText.visibility = View.VISIBLE

                Glide.with(binding.profilePicture)
                    .load(uri)
                    .placeholder(R.drawable.gallery)
                    .into(binding.profilePicture)
            } else {
                Log.d(TAG, getString(R.string.not_select_Image))

            }
        }

        binding.imageDeleteText.setOnClickListener {

            Glide.with(binding.profilePicture)
                .load(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(binding.profilePicture)

            signUpViewModel.imagePath = ""

        }

        binding.btnSignUp.setOnClickListener {

            inputMethodManager.hideSoftInputFromWindow(binding.inputLayoutName.windowToken, 0)
            inputMethodManager.hideSoftInputFromWindow(binding.inputLayoutEmail.windowToken, 0)

            binding.inputLayoutName.error = null
            binding.inputLayoutEmail.error = null
            binding.inputLayoutPassword.error = null

            if (signUpViewModel.validation()) {
                if (isNetworkAvailable(this)) {
                    signUpViewModel.getApiRegistration(signUpViewModel.imagePath)
                } else {
                    Toast.makeText(this, R.string.internet_connection, Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.inputLayoutName.error = signUpViewModel.nameError
                binding.inputLayoutEmail.error = signUpViewModel.emailError
                binding.inputLayoutPassword.error = signUpViewModel.passError
                if (signUpViewModel.imagePathError != "") {
                    method.dialog(
                        this,
                        resources.getString(R.string.app_name),
                        signUpViewModel.imagePathError,
                        0,
                        ""
                    )
                }
            }


        }
        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        binding.profilePicture.setOnClickListener {
            selectImageDialog()
        }
    }

    private fun selectImageDialog() {
        val dialog = Dialog(this, R.style.dialogStyle)
        dialog.setTitle(R.string.select_any_one_option)
        dialog.setContentView(R.layout.item_camara_gallery)
        val camera: ShapeableImageView = dialog.findViewById(R.id.imgCamera)
        val gallery: ShapeableImageView = dialog.findViewById(R.id.imgGallery)

        camera.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
                dialog.dismiss()
            } else {
                //permission ask
                isCamara = true
                launcherPermission.launch(Manifest.permission.CAMERA)
                dialog.dismiss()
            }
        }
        gallery.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                dialog.dismiss()
            } else {
                isCamara = false
                launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showPermissionDeniedDialog() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts(
            "package", packageName, null
        )
        intent.data = uri
        startActivity(intent)
    }

    private fun openCamera() {
        val clickImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(clickImage, CAMERA_REQUEST_CODE)
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val pic = data?.getParcelableExtra<Bitmap>("data")
                    binding.profilePicture.setImageBitmap(pic)

                }

        }
    }
}
