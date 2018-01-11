package com.anbillon.debuggerview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.support.v4.app.ShareCompat
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import com.jakewharton.processphoenix.ProcessPhoenix
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

/**
 * @author Cosouly (cosouly@gmail.com)
 */

internal object Util {
  fun restart(context: Context) {
    ProcessPhoenix.triggerRebirth(context)
  }

  fun logsShare(context: Context): Disposable {
    return LumberYard.save(context)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          val intent = Intent(Intent.ACTION_SEND)
          intent.type = "text/plain"
          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(it))
          Intents.maybeStartChooser(context, intent)
        }, {
          Toast.makeText(context, "Couldn't save the logs for sharing.", Toast.LENGTH_SHORT)
              .show()
        })
  }

  fun onBugReport(activity: Activity, report: Report, screenshot: File): Disposable {
    return if (report.includeLogs == true) {
      LumberYard.save(activity)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            submitReport(activity, report, screenshot, it)
          }, {
            Timber.e(it.message)
            submitReport(activity, report, screenshot, null)
          })
    } else {
      submitReport(activity, report, screenshot, null)
      Disposables.empty()
    }
  }

  private fun submitReport(activity: Activity, report: Report, logs: File?, screenshot: File?) {
    val displayMetrics = Resources.getSystem().displayMetrics

    val intentBuilder = ShareCompat.IntentBuilder.from(activity)
    intentBuilder.setType("message/rfc822").addEmailTo(report.email).setSubject(report.title)

    val body = StringBuilder()
    if (!TextUtils.isEmpty(report.description)) {
      body.append("{panel:title=Description}\n").append(report.description).append(
          "\n{panel}\n\n")
    }
    val pkgName = activity.packageName
    var packageInfo: PackageInfo? = null
    try {
      packageInfo = activity.packageManager.getPackageInfo(pkgName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.e(e.message)
    }

    body.append("{panel:title=App}\n")
    body.append("Version: ")
        .append(if (packageInfo == null) "" else packageInfo.versionName)
        .append('\n')
    body.append("Version code: ")
        .append(if (packageInfo == null) "" else packageInfo.versionCode)
        .append('\n')
    body.append("{panel}\n\n")

    body.append("{panel:title=Device}\n")
    body.append("Make: ").append(Build.MANUFACTURER).append('\n')
    body.append("Model: ").append(Build.MODEL).append('\n')
    body.append("Resolution: ")
        .append(displayMetrics.heightPixels)
        .append("x")
        .append(displayMetrics.widthPixels)
        .append('\n')
    body.append("Density: ")
        .append(displayMetrics.densityDpi)
        .append("dpi (")
        .append(density(displayMetrics))
        .append(")\n")
    body.append("Release: ").append(Build.VERSION.RELEASE).append('\n')
    body.append("API: ").append(Build.VERSION.SDK_INT).append('\n')
    body.append("{panel}")

    intentBuilder.setText(body.toString())

    if (screenshot != null && report.includeScreenshot == true) {
      intentBuilder.addStream(Uri.fromFile(screenshot))
    }
    if (logs != null) {
      intentBuilder.addStream(Uri.fromFile(logs))
    }

    Intents.maybeStartActivity(activity, intentBuilder.intent)
  }

  fun density(displayMetrics: DisplayMetrics): String {
    return when (displayMetrics.densityDpi) {
      DisplayMetrics.DENSITY_LOW -> "ldpi"
      DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
      DisplayMetrics.DENSITY_HIGH -> "hdpi"
      DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
      DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
      DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
      DisplayMetrics.DENSITY_TV -> "tvdpi"
      else -> displayMetrics.densityDpi.toString()
    }
  }

  fun getDeviceInfo(context: Context): String {
    /* show the device information */
    val builder = StringBuilder()
    context.resources.displayMetrics.let {
      val width = it.widthPixels
      val height = it.heightPixels
      builder.append("===== Display metrics =====")
      builder.append("\n\n")
      builder.append("width: ")
      builder.append(width.toString())
      builder.append("\n")
      builder.append("height: ")
      builder.append(height.toString())
      builder.append("\n")
      builder.append("dpi: ")
      builder.append(it.densityDpi.toString())
      builder.append("\n")
      builder.append("density: ")
      builder.append(it.density.toString())
      builder.append("\n")
      builder.append("scaled density: ")
      builder.append(it.scaledDensity.toString())
      builder.append("\n")
      builder.append("xdpi: ")
      builder.append(it.xdpi.toString())
      builder.append("\n")
      builder.append("ydpi: ")
      builder.append(it.ydpi.toString())
      builder.append("\n")
      builder.append("smallest width: ")
      val config = context.resources.configuration
      builder.append(config.smallestScreenWidthDp.toString())
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val point = Point()
        (context.getSystemService(
            Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.getRealSize(point)
        val x = Math.pow((point.x / it.xdpi).toDouble(), 2.0)
        val y = Math.pow((point.y / it.ydpi).toDouble(), 2.0)
        val inches = Math.sqrt(x + y)
        val dpi = Math.sqrt((width * width + height * height).toDouble()) / inches
        builder.append("\n")
        builder.append("inches: ")
        builder.append(inches.toString())
        builder.append("\n")
        builder.append("real dpi: ")
        builder.append(dpi.toString())
        builder.append("\n\n")
      }
    }

    builder.append("===== Device info =====")
    builder.append("\n\n")
    val infos = DeviceUtil.mobileInfo
    val keys = infos.keys
    for (key in keys) {
      val info = infos[key]
      builder.append(key)
      builder.append(": ")
      builder.append(info)
      builder.append("\n")
    }

    builder.append("\n\n")

    val cpuInfo = DeviceUtil.cpuInfo
    builder.append("===== CPU info =====")
    builder.append("\n\n")
    builder.append(cpuInfo)

    return builder.toString()
  }
}