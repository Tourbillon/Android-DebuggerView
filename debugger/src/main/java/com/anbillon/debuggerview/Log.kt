package com.anbillon.debuggerview


/**
 * @author Cosouly (cosouly@gmail.com)
 */

internal data class Log(val priority: Int? = 0, val tag: String? = null,
    val message: String? = null) {
  fun level(): String {
    return when (priority) {
      android.util.Log.VERBOSE -> "V"
      android.util.Log.DEBUG -> "D"
      android.util.Log.INFO -> "I"
      android.util.Log.WARN -> "W"
      android.util.Log.ERROR -> "E"
      android.util.Log.ASSERT -> "A"
      else -> "?"
    }
  }

  fun message(): String {
    return String.format("%22s %s %s", tag, level(),
        /* Indent newlines to match the original indentation. */
        message?.replace("\\n", "\n                         "))
  }
}