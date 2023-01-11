package com.example.viewmodeldemo.preferense

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class Preferences @Inject constructor(val context: Context) {

     private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setPrefString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }
    fun getPrefString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }
    fun setPrefBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun getPrefBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

}
