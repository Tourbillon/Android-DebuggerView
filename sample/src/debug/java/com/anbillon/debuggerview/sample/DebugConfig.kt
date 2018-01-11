package com.anbillon.debuggerview.sample

import com.anbillon.debuggerview.ExtraUrl
import com.anbillon.debuggerview.sample.Flavor.URL_KEY_SE
import com.anbillon.debuggerview.sample.Flavor.URL_KEY_SNS
import com.anbillon.debuggerview.sample.Flavor.URL_KEY_TWEET

/**
 * @author Cosouly (cosouly@gmail.com)
 */

object DebugConfig {
  val EXTRA_URL_TEST1 = mutableListOf<ExtraUrl>()
  val EXTRA_URL_TEST2 = mutableListOf<ExtraUrl>()
  val EXTRA_URL_CUSTOM = mutableListOf<ExtraUrl>()

  const val URL_GIT1 = "https://github.com"
  const val URL_GIT2 = "https://gitlab.com"

  const val URL_SE1 = Config.URL_SE
  const val URL_SE2 = "https://bing.com"

  const val URL_SNS1 = Config.URL_SNS
  const val URL_SNS2 = "https://qzone.com"

  const val URL_TWEET1 = Config.URL_TWEET
  const val URL_TWEET2 = "https://weibo.com"

  init {
    EXTRA_URL_TEST1.add(ExtraUrl(URL_KEY_SE, URL_SE1, "Search Engines"))
    EXTRA_URL_TEST1.add(ExtraUrl(URL_KEY_SNS, URL_SNS1, "Social Networking Site"))
    EXTRA_URL_TEST1.add(ExtraUrl(URL_KEY_TWEET, URL_TWEET1, "Tweet Site"))

    EXTRA_URL_TEST2.add(ExtraUrl(URL_KEY_SE, URL_SE2, "Search Engines"))
    EXTRA_URL_TEST2.add(ExtraUrl(URL_KEY_SNS, URL_SNS2, "Social Networking Site"))
    EXTRA_URL_TEST2.add(ExtraUrl(URL_KEY_TWEET, URL_TWEET2, "Tweet Site"))

    EXTRA_URL_CUSTOM.add(ExtraUrl(URL_KEY_SE, URL_SE2, "Search Engines"))
    EXTRA_URL_CUSTOM.add(ExtraUrl(URL_KEY_SNS, URL_SNS2, "Social Networking Site"))
    EXTRA_URL_CUSTOM.add(ExtraUrl(URL_KEY_TWEET, URL_TWEET2, "Tweet Site"))
  }
}