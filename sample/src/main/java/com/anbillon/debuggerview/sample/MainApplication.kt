package com.anbillon.debuggerview.sample

import android.app.Application
import timber.log.Timber

/**
 * @author Cosouly (cosouly@gmail.com)
 */

class MainApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())

    Thread(Runnable {
      while (true) {
        Timber.d(System.currentTimeMillis().toString())
        Thread.sleep(2000)
      }
    }).start()
  }
}