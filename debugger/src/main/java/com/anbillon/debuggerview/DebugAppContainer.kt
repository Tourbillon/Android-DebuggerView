package com.anbillon.debuggerview

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup


/**
 * @author Cosouly (cosouly@gmail.com)
 */

object DebugAppContainer : AppContainer {
  lateinit var endpoint: Endpoint

  override fun bind(activity: Activity): ViewGroup {
    val parent = activity.findViewById<ViewGroup>(android.R.id.content)
    LayoutInflater.from(activity).inflate(R.layout.layout_debug_drawer, parent, true)
    val debugDrawerLayout = parent.findViewById<DebugDrawerLayout>(R.id.layout_debug_drawer)
    return debugDrawerLayout.findViewById(R.id.content)
  }

  fun debugEnvironment(context: Context): DebugEnvironment {
    return EnvironmentConfigLayout.getEndpointIndex(context, 0).let {
      DebugEnvironment(it,
          EnvironmentConfigLayout.getEndpointName(context, endpoint.name(it)),
          EnvironmentConfigLayout.getEndpointUrl(context, endpoint.url(it)),
          EnvironmentConfigLayout.getEndpointExtraUrl(context, endpoint.extraUrls(it)))
    }
  }

  fun mockDelayMillis(context: Context): Int {
    return EnvironmentConfigLayout.getMockDelayMilliseconds(context)
  }

}