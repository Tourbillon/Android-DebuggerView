package com.anbillon.debuggerview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.support.v4.content.PermissionChecker
import android.telephony.TelephonyManager
import timber.log.Timber
import java.io.IOException
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * Device util: used to get device information such as imei, cpu, hardware and so on.
 *
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
internal object DeviceUtil {
  /**
   * Get information of the mobile.
   *
   * @return the information map
   */
  val mobileInfo: Map<String, String>
    get() {
      val mobileInfo = HashMap<String, String>()

      val fields = Build::class.java.declaredFields
      try {
        fields?.forEach { f ->
          f.isAccessible = true
          val name = f.name

          val value = f.get(null).toString()
          mobileInfo.put(name, value)
        }
      } catch (e: Exception) {
        Timber.d("get mobile info error: " + e.message)
      }

      return mobileInfo
    }

  /**
   * Get the information of CPU.
   *
   * @return the information string
   */
  val cpuInfo: String
    get() {
      var result = "N/A"

      val args = arrayOf("/system/bin/cat", "/proc/cpuinfo")

      val cmdBuilder = ProcessBuilder(*args)
      try {
        val process = cmdBuilder.start()
        val `is` = process.inputStream
        val buf = ByteArray(1024)
        result = ""
        while (`is`.read(buf) != -1) {
          result += String(buf)
        }
        `is`.close()
      } catch (e: IOException) {
        Timber.d("get cpu max freq error: " + e.message)
      }

      return result
    }

  /**
   * Get imei code of current device.
   *
   * @param context context
   * @return imei code
   */
  @SuppressLint("MissingPermission", "HardwareIds")
  fun getImei(context: Context): String? {
    var deviceId: String? = null
    if (PermissionChecker.checkSelfPermission(context,
        Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
      deviceId = (context.getSystemService(
          Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
    }
    if (deviceId == null) {
      deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    return deviceId
  }

  /**
   * Get mac address of current device. This util needs
   * "android.permission.ACCESS_WIFI_STATE" permission.
   *
   * @param context context
   * @return mac address
   */
  @Throws(SocketException::class)
  fun getMac(context: Context): String? {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
    while (networkInterfaces.hasMoreElements()) {
      val networkInterface = networkInterfaces.nextElement()
      if ("wlan0" == networkInterface.name) {
        val hardwareAddress = networkInterface.hardwareAddress
        if (hardwareAddress == null || hardwareAddress.isEmpty()) {
          continue
        }
        val buf = StringBuilder()
        for (b in hardwareAddress) {
          buf.append(String.format("%02X:", b))
        }
        if (buf.length > 0) {
          buf.deleteCharAt(buf.length - 1)
        }
        return buf.toString()
      }
    }
    return null
  }

  /**
   * Get the ip of current mobile device. This util needs
   * "android.permission.ACCESS_WIFI_STATE" and "android.permission.INTERNET" permission.
   */
  @Throws(SocketException::class)
  fun getIp(context: Context): String? {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
    while (networkInterfaces.hasMoreElements()) {
      val networkInterface = networkInterfaces.nextElement()
      val inetAddresses = networkInterface.inetAddresses
      while (inetAddresses.hasMoreElements()) {
        val inetAddress = inetAddresses.nextElement()
        if (!inetAddress.isLoopbackAddress && !inetAddress.isLinkLocalAddress) {
          return inetAddress.hostAddress
        }
      }
    }
    return null
  }

  /**
   * Get the model of current mobile.
   *
   * @return mobile model
   */
  fun model(): String = Build.MODEL
}
