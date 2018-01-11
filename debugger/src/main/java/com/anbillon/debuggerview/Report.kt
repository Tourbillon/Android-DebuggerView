package com.anbillon.debuggerview

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal data class Report(val email: String?,
    val title: String?,
    val description: String?,
    val includeScreenshot: Boolean?,
    val includeLogs: Boolean?)
