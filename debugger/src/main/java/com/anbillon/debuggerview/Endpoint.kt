package com.anbillon.debuggerview

/**
 * @author Cosouly (cosouly@gmail.com)
 */
interface Endpoint {
  fun isCustom(index: Int): Boolean

  fun isMock(index: Int): Boolean

  fun count(): Int

  fun name(index: Int): String

  fun url(index: Int): String

  fun extraUrls(index: Int): List<ExtraUrl>
}
