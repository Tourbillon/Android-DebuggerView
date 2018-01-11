package com.anbillon.debuggerview.sample

import com.anbillon.debuggerview.ExtraUrl

/**
 * @author Cosouly (cosouly@gmail.com)
 */

internal enum class ENV(val env: String, val baseUrl: String, val extraUrls: List<ExtraUrl>) {
  TEST1("test1", DebugConfig.URL_GIT1, DebugConfig.EXTRA_URL_TEST1),
  TEST2("test2", DebugConfig.URL_GIT2, DebugConfig.EXTRA_URL_TEST2),
  MOCK("mock", DebugConfig.URL_GIT1, DebugConfig.EXTRA_URL_TEST1),
  CUSTOM("custom", DebugConfig.URL_GIT2, DebugConfig.EXTRA_URL_CUSTOM),
}