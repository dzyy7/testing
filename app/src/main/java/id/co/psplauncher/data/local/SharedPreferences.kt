package id.co.psplauncher.data.local

import android.content.Context

object          SharedPreferences {
    private const val PREFS_NAME = "my_shared_prefs"
    private const val KEY_FB_TOKEN = "fb_token"

    //    ==============================================================================================
    fun saveFbToken(context: Context, token: String) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(KEY_FB_TOKEN, token).apply()
    }

    fun getFbToken(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_FB_TOKEN, null)
    }
}