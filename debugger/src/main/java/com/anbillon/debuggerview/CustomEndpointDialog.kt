package com.anbillon.debuggerview

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal class CustomEndpointDialog(context: Context, private val name: String?,
    private val url: String?, extraUrlList: List<ExtraUrl>?,
    private val listener: SetCustomEndpointListener?) : AlertDialog(context) {
  private val extraUrls = mutableListOf<ExtraUrl>()
  private val editTexts = mutableListOf<EditText>()

  private var nameEditText: EditText? = null
  private var urlEditText: EditText? = null
  private var layoutContent: LinearLayout? = null

  init {
    extraUrlList?.let {
      extraUrls.addAll(extraUrlList)
    }

    setTitle(R.string.debug_custom_endpoint_dialog_title)
    setView(layoutInflater.inflate(R.layout.dialog_custom_endpoint, layoutContent, false))
    setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.debug_cancel)
    ) { _, _ ->
      listener?.onCancel()
    }
    setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.debug_confirm)
    ) { _, _ ->
      listener?.let {
        val list = mutableListOf<ExtraUrl>()
        editTexts.indices.mapTo(list) { i ->
          extraUrls[i].copy(url = editTexts[i].text.toString().trim())
        }
        it.onSetCustomEndpoint(nameEditText?.text?.toString()?.trim(),
            urlEditText?.text?.toString(), list)
      }
    }
    setCanceledOnTouchOutside(false)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    nameEditText = findViewById<View>(R.id.et_name) as? EditText
    nameEditText?.setText(name)

    urlEditText = findViewById<View>(R.id.et_url) as? EditText
    initEditText(urlEditText, url)

    layoutContent = findViewById<View>(R.id.layout_content) as? LinearLayout
    initExtraEditText()
  }

  private fun initEditText(editText: EditText?, url: String?) {
    editText?.let {
      if (TextUtils.isEmpty(url)) {
        it.setText(R.string.debug_endpoint_protocol)
      } else {
        it.setText(url)
      }
      it.setSelection(it.text.length)
    }
  }

  private fun initExtraEditText() {
    extraUrls.forEach { extraUrl ->
      val view = layoutInflater.inflate(R.layout.item_extra_url, layoutContent, false)

      (view?.findViewById(R.id.tv_extra_url_description) as? TextView)?.let {
        it.text = extraUrl.description
      }

      (view?.findViewById(R.id.et_extra_url) as? EditText)?.let {
        initEditText(it, extraUrl.url)
        editTexts.add(it)
      }

      layoutContent?.addView(view)
    }
  }

  internal interface SetCustomEndpointListener {
    fun onSetCustomEndpoint(name: String?, baseUrl: String?, extraUrlList: List<ExtraUrl>?)

    fun onCancel()
  }
}
