package com.anbillon.debuggerview

import android.content.Context
import android.content.Intent

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal object Intents {
  /**
   * Attempt to launch the supplied [Intent]. Queries on-device packages before launching and
   * will display a simple message if none are available to handle it.
   */
  fun maybeStartActivity(context: Context, intent: Intent): Boolean {
    return maybeStartActivity(context, intent, false)
  }

  /**
   * Attempt to launch Android's chooser for the supplied [Intent]. Queries on-device
   * packages
   * before launching and will display a simple message if none are available to handle it.
   */
  fun maybeStartChooser(context: Context, intent: Intent): Boolean {
    return maybeStartActivity(context, intent, true)
  }

  private fun maybeStartActivity(context: Context, intent: Intent, chooser: Boolean): Boolean {
    return if (hasHandler(context, intent)) {
      context.startActivity(if (chooser) Intent.createChooser(intent, null) else intent)
      return true
    } else false
  }

  /**
   * Queries on-device packages for a handler for the supplied [Intent].
   */
  private fun hasHandler(context: Context,
      intent: Intent): Boolean = !context.packageManager.queryIntentActivities(intent, 0).isEmpty()
}
