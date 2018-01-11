package com.anbillon.debuggerview

import android.content.Context
import android.content.res.Resources
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal class DebugDrawerLayout @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null, defStyle: Int = 0) : DrawerLayout(context, attrs, defStyle) {
  private var layoutEnvironmentConfig: EnvironmentConfigLayout? = null
  private var layoutLogPrint: LogPrinterLayout? = null


  override fun onFinishInflate() {
    super.onFinishInflate()

    layoutEnvironmentConfig = findViewById(
        R.id.layout_environment_config) as? EnvironmentConfigLayout
    layoutLogPrint = findViewById(R.id.layout_log_print) as? LogPrinterLayout
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    val width = Resources.getSystem().displayMetrics.widthPixels * 9.0f / 10.0f
    layoutEnvironmentConfig?.layoutParams?.width = width.toInt()
    layoutLogPrint?.layoutParams?.width = width.toInt()
  }
}
