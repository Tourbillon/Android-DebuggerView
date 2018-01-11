package com.anbillon.debuggerview

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal object Prefers {
  /**
   * Prefer file: shared preference file, save, read
   * or remove value via this class.
   */
  internal class PreferFile(private val sp: SharedPreferences) {

    fun save(key: String, value: Int) {
      sp.edit().putInt(key, value).apply()
    }

    fun save(key: String, value: Long) {
      sp.edit().putLong(key, value).apply()
    }

    fun save(key: String, value: Float) {
      sp.edit().putFloat(key, value).apply()
    }

    fun save(key: String, value: Boolean) {
      sp.edit().putBoolean(key, value).apply()
    }

    fun save(key: String, value: String) {
      sp.edit().putString(key, value).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
      return sp.getInt(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
      return sp.getLong(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
      return sp.getFloat(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
      return sp.getBoolean(key, defValue)
    }

    fun getString(key: String, defValue: String): String? {
      return sp.getString(key, defValue)
    }

    operator fun contains(key: String): Boolean {
      return sp.contains(key)
    }

    fun remove(key: String) {
      sp.edit().remove(key).apply()
    }

    fun clear() {
      sp.edit().clear().apply()
    }

    fun editor(): SharedPreferences.Editor {
      return sp.edit()
    }
  }

  /**
   * get shared preferences according to api
   */
  private fun getPrefer(context: Context, preferName: String): SharedPreferences {
    return context.getSharedPreferences(preferName, Context.MODE_PRIVATE)
  }

  /**
   * Load default shared preferences(packagename_preferences).
   *
   * @return [PreferFile]
   */
  fun load(context: Context): PreferFile {
    return PreferFile(PreferenceManager.getDefaultSharedPreferences(context))
  }

  /**
   * Load shared preferences according to prefer name.
   *
   * @param preferName the name of shared preference
   * @return [PreferFile]
   */
  fun load(context: Context, preferName: String): PreferFile = PreferFile(
      getPrefer(context, preferName))
}
