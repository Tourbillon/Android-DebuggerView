package com.anbillon.debuggerview

/**
 * @author Cosouly (cosouly@gmail.com)
 */
data class DebugEnvironment(val index: Int,
    val name: String?,
    val url: String?,
    val extraUrls: List<ExtraUrl>?)
