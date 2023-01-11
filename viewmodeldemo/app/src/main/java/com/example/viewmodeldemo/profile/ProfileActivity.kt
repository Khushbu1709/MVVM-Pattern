package com.example.viewmodeldemo.profile

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.databinding.ActivityProfileBinding
import com.example.viewmodeldemo.preferense.Preferences
import com.example.viewmodeldemo.utils.StatusClass
import com.example.viewmodeldemo.utils.Method
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var method: Method

    lateinit var profileViewModel: ProfileViewModel

    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: ActivityProfileBinding

    companion object {
        var TAG: String = ProfileActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.profileModel = profileViewModel

        progressDialog = ProgressDialog(this)

        profileViewModel.liveData.observe(this) {
            when (it.status) {
                StatusClass.SUCCESS -> {
                    Log.d(TAG, "" + it.data?.message)
                    if (it.data?.status!!) {

                        Glide.with(this)
                            .load(it.data.data.profilePicture)
                            .placeholder(R.drawable.placeholder)
                            .into(binding.imageViewUserProfile)
                        binding.textViewUserNameProfile.text = it.data.data.name
                    }
                    else {
                        Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss()
                }
                StatusClass.LOADING -> {
                    progressDialog.setMessage(resources.getString(R.string.loading))
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                }
                StatusClass.ERROR -> {
                    Log.d(TAG, "" + StatusClass.ERROR)
                }
            }
        }
        profileViewModel.getProfileCall(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                method.logoutDialog(this,resources.getString( R.string.logout), resources.getString(R.string.account_logout))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}