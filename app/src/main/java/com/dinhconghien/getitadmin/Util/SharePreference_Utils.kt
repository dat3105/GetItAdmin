package com.dinhconghien.getitadmin.Util

import android.content.Context
import android.content.SharedPreferences
import com.dinhconghien.getitadmin.Model.User

class SharePreference_Utils(context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    var SHARED_PREF_NAME = "session"
    var SESSION_KEY = "session_user"
    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
    fun saveSession(listUser: ArrayList<User>, i : Int) {
        val userID= listUser[i].userID
        editor.putString(SESSION_KEY, userID).commit()
    }

    fun getSession(): String {
        return sharedPreferences.getString(SESSION_KEY, "").toString()
    }

    fun removeSession() {
        editor.clear().commit()
    }


}