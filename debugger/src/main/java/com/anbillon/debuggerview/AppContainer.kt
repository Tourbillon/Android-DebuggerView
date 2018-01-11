package com.anbillon.debuggerview

import android.app.Activity
import android.view.ViewGroup

/**
 * @author Cosouly (cosouly@gmail.com)
 */
interface AppContainer {
  fun bind(activity: Activity): ViewGroup
}