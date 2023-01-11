package com.example.viewmodeldemo.utils

import android.app.Activity
import android.content.Intent
import com.example.viewmodeldemo.R
import com.example.viewmodeldemo.preferense.Preferences
import com.example.viewmodeldemo.profile.ProfileActivity
import com.example.viewmodeldemo.signIn.SignInActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject


class Method @Inject constructor(var preferences: Preferences) {


    fun dialog(activity: Activity,title: String, message: String, isChecked: Int, type: String) {

        val materialDialog = MaterialAlertDialogBuilder(activity)
        materialDialog.apply {
            setTitle(title)
            setMessage(message)

        }
        materialDialog.setPositiveButton("ok") { _, _ ->

            if (type == "login") {
                if (isChecked == 1) {
                    activity.startActivity(Intent(activity, ProfileActivity::class.java))
                    activity.finishAffinity()

                }
            }
            if (type == "signup") {
                if (isChecked == 1) {
                    activity.startActivity(Intent(activity, SignInActivity::class.java))
                    (activity).finishAffinity()
                }
            }
        }
        materialDialog.show()

    }

    fun logoutDialog(activity: Activity, tittle: String, message: String) {


        val mDialog = MaterialAlertDialogBuilder(activity)

        mDialog.setTitle(tittle)
        mDialog.setMessage(message)

        mDialog.setPositiveButton(R.string.ok) { _, _ ->
            preferences.editor.clear()
            preferences.editor.apply()
            activity.startActivity(Intent(activity, SignInActivity::class.java))
            activity.finishAffinity()

        }
        mDialog.setNegativeButton(R.string.cancel, null)
        mDialog.show()
    }


}