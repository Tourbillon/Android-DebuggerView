package com.anbillon.debuggerview.sample

import android.content.Context
import com.anbillon.debuggerview.AppContainer
import com.anbillon.debuggerview.DebugAppContainer
import com.anbillon.debuggerview.Endpoint
import com.anbillon.debuggerview.ExtraUrl

/**
 * @author Cosouly (cosouly@gmail.com)
 */

object Flavor : Endpoint {
  internal const val URL_KEY_SE = "urlKeySE"
  internal const val URL_KEY_SNS = "urlKeySNS"
  internal const val URL_KEY_TWEET = "urlKeyTweet"

  override fun isCustom(index: Int): Boolean = ENV.CUSTOM.ordinal == index

  override fun isMock(index: Int): Boolean = ENV.MOCK.ordinal == index

  override fun count(): Int = ENV.values().size

  override fun name(index: Int): String = ENV.values()[index].env

  override fun url(index: Int): String = ENV.values()[index].baseUrl

  override fun extraUrls(index: Int): List<ExtraUrl> = ENV.values()[index].extraUrls

  init {
    DebugAppContainer.endpoint = this
  }

  fun appContainer(context: Context): AppContainer = DebugAppContainer

  fun mockDelayMillis(context: Context): Int = DebugAppContainer.mockDelayMillis(context)

  fun env(context: Context): String = DebugAppContainer.debugEnvironment(context).name ?: ""

  fun GITUrl(context: Context): String = DebugAppContainer.debugEnvironment(context).url ?: ""

  fun SEUrl(context: Context): String {
    return fetchUrl(context, URL_KEY_SE)
  }

  fun SNSUrl(context: Context): String {
    return fetchUrl(context, URL_KEY_SNS)
  }

  fun TweetUrl(context: Context): String {
    return fetchUrl(context, URL_KEY_TWEET)
  }

  private fun fetchUrl(context: Context, key: String): String {
    return DebugAppContainer.debugEnvironment(context).extraUrls?.let {
      var ret: String? = ""
      it.forEach { extra ->
        if (extra.name == key) {
          ret = extra.url
          return@forEach
        }
      }
      return ret ?: ""
    } ?: ""
  }
}