package com.anbillon.debuggerview.sample

import android.content.Context
import com.anbillon.debuggerview.AppContainer

/**
 * @author Cosouly (cosouly@gmail.com)
 */
object Flavor {
  fun appContainer(context: Context): AppContainer = AppContainer.DEFAULT

  fun mockDelayMillis(context: Context): Int = 0

  fun env(context: Context): String = "release"

  fun GITUrl(context: Context): String = Config.URL_GIT

  fun SEUrl(context: Context): String = Config.URL_SE

  fun SNSUrl(context: Context): String = Config.URL_SNS

  fun TweetUrl(context: Context): String = Config.URL_TWEET
}