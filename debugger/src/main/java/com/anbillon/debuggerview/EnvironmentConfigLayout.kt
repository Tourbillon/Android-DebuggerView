package com.anbillon.debuggerview

import android.content.Context
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_BASE_URL
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_CURRENT_INDEX
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_EXTRA_URL
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_EXTRA_URL_DESCRIPTION
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_EXTRA_URL_NAME
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_MOCK_DELAY_INDEX
import com.anbillon.debuggerview.Constants.KEY_ENDPOINT_NAME
import java.util.*

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal class EnvironmentConfigLayout @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(
    context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

  private var spinnerEndpoint: Spinner? = null
  private var btnEdit: View? = null
  private var layoutMockDelay: View? = null
  private var spinnerMockDelay: Spinner? = null
  private var dialogCustomEndpoint: CustomEndpointDialog? = null
  private var dialogSystem: AlertDialog? = null

  private var adapterEndpoint: ConfigAdapter? = null
  private var adapterMockDelay: ConfigAdapter? = null

  private var currentName: String? = null
  private var currentBaseUrl: String? = null
  private var currentEndPointIndex: Int = 0
  private var currentMockDelayIndex: Int = 0

  private val currentExtraUrlList = ArrayList<ExtraUrl>()
  private val endpoint: Endpoint? = DebugAppContainer.endpoint

  private fun initData() {
    currentEndPointIndex = getEndpointIndex(context, 0)
    currentName = getEndpointName(context, endpoint?.name(currentEndPointIndex))
    currentBaseUrl = getEndpointUrl(context, endpoint?.url(currentEndPointIndex))
    getEndpointExtraUrl(context, endpoint?.extraUrls(currentEndPointIndex))?.let {
      currentExtraUrlList.clear()
      currentExtraUrlList.addAll(it)
    }
    currentMockDelayIndex = getMockDelayIndex(context)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()

    spinnerEndpoint = findViewById(R.id.spinner_endpoint) as? Spinner
    btnEdit = findViewById(R.id.iv_edit) as? View
    layoutMockDelay = findViewById(R.id.layout_mock_delay) as? View
    spinnerMockDelay = findViewById(R.id.spinner_mock_delay) as? Spinner
    (findViewById(R.id.system_info) as? View)?.setOnClickListener {
      if (dialogSystem == null) {
        dialogSystem = AlertDialog.Builder(context).setMessage(Util.getDeviceInfo(context)).create()
      }
      dialogSystem?.show()
    }

    spinnerEndpoint?.adapter = object : ConfigAdapter() {
      override fun getItem(position: Int): String? {
        return endpoint?.name(position)
      }

      override fun getCount(): Int {
        return endpoint?.count() ?: 0
      }
    }
    spinnerEndpoint?.setSelection(currentEndPointIndex)
    spinnerEndpoint?.onItemSelectedListener = object : SimpleItemSelectedListener() {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (currentEndPointIndex != position) {
          if (endpoint?.isCustom(position) == true) {
            showCustomEndpointDialog(position)
          } else {
            currentEndPointIndex = position
            endpoint?.let {
              setEndpoint(context, currentEndPointIndex, it.name(position), it.url(position),
                  it.extraUrls(position))
              Util.restart(context)
            }
          }
        }
      }
    }

    spinnerMockDelay?.adapter = object : ConfigAdapter() {
      override fun getItem(position: Int): String {
        return MOCK_DELAY_SECONDS[position].toString()
      }

      override fun getCount(): Int {
        return MOCK_DELAY_SECONDS.size
      }
    }
    spinnerMockDelay?.setSelection(currentMockDelayIndex)
    spinnerMockDelay?.onItemSelectedListener = object : SimpleItemSelectedListener() {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (currentMockDelayIndex != position) {
          currentMockDelayIndex = position
          setMockDelayIndex(context, currentMockDelayIndex)
          Util.restart(context)
        }
      }
    }

    btnEdit?.setOnClickListener(this)
  }

  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    super.onVisibilityChanged(changedView, visibility)

    if (this == changedView && visibility == View.VISIBLE) {
      restoreViewState()
    }
  }

  override fun onRestoreInstanceState(state: Parcelable) {
    super.onRestoreInstanceState(state)

    restoreViewState()
  }

  override fun onDetachedFromWindow() {
    dialogCustomEndpoint?.let {
      if (it.isShowing) it.dismiss()
    }

    dialogSystem?.let {
      if (it.isShowing) it.dismiss()
    }

    super.onDetachedFromWindow()
  }

  private fun restoreViewState() {
    initData()

    btnEdit?.visibility = if (endpoint?.isCustom(
        currentEndPointIndex) == true) View.VISIBLE else View.GONE
    layoutMockDelay?.visibility = if (endpoint?.isMock(
        currentEndPointIndex) == true) View.VISIBLE else View.GONE
    spinnerEndpoint?.setSelection(currentEndPointIndex)
    adapterEndpoint?.notifyDataSetChanged()
    spinnerMockDelay?.setSelection(currentMockDelayIndex)
    adapterMockDelay?.notifyDataSetChanged()
  }

  private fun showCustomEndpointDialog(newSelection: Int) {
    dialogCustomEndpoint = CustomEndpointDialog(context,
        currentName ?: endpoint?.name(currentEndPointIndex),
        currentBaseUrl ?: endpoint?.url(currentEndPointIndex), endpoint?.extraUrls(newSelection),
        object : CustomEndpointDialog.SetCustomEndpointListener {
          override fun onSetCustomEndpoint(name: String?, baseUrl: String?,
              extraUrlList: List<ExtraUrl>?) {
            currentEndPointIndex = newSelection
            setEndpoint(context, currentEndPointIndex, name, baseUrl, extraUrlList)
            Util.restart(context)
          }

          override fun onCancel() {
            spinnerEndpoint?.setSelection(currentEndPointIndex)
          }
        })
    dialogCustomEndpoint?.show()
  }

  override fun onClick(v: View) {
    showCustomEndpointDialog(currentEndPointIndex)
  }

  private open class SimpleItemSelectedListener : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }
  }

  companion object {
    private val MOCK_DELAY_SECONDS = intArrayOf(1, 3, 5, 10, 20, 60)

    private fun setEndpoint(context: Context, index: Int, name: String?, baseUrl: String?,
        extraUrlList: List<ExtraUrl>?) {
      val editor = Prefers.load(context).editor()
      editor.putInt(KEY_ENDPOINT_CURRENT_INDEX, index)
      editor.putString(KEY_ENDPOINT_NAME, name)
      editor.putString(KEY_ENDPOINT_BASE_URL, baseUrl)
      extraUrlList?.indices?.forEach { i ->
        editor.putString(KEY_ENDPOINT_EXTRA_URL_NAME + i, extraUrlList[i].name)
        editor.putString(KEY_ENDPOINT_EXTRA_URL + i, extraUrlList[i].url)
        editor.putString(KEY_ENDPOINT_EXTRA_URL_DESCRIPTION + i, extraUrlList[i].description)
      }
      editor.commit()
    }

    private fun setMockDelayIndex(context: Context, index: Int = 0) {
      Prefers.load(context).save(KEY_ENDPOINT_MOCK_DELAY_INDEX, index)
    }

    internal fun getEndpointIndex(context: Context, defaultValue: Int = 0): Int {
      return Prefers.load(context).getInt(KEY_ENDPOINT_CURRENT_INDEX, defaultValue)
    }

    internal fun getEndpointUrl(context: Context, defaultValue: String?): String? {
      return Prefers.load(context).getString(KEY_ENDPOINT_BASE_URL, defaultValue ?: "")
    }

    internal fun getEndpointName(context: Context, defaultValue: String?): String? {
      return Prefers.load(context).getString(KEY_ENDPOINT_NAME, defaultValue ?: "")
    }

    internal fun getEndpointExtraUrl(context: Context, defValue: List<ExtraUrl>?): List<ExtraUrl>? {
      defValue?.indices?.forEach { i ->
        val extraUrl = defValue[i]
        extraUrl.name = Prefers.load(context).getString(KEY_ENDPOINT_EXTRA_URL_NAME + i,
            extraUrl.name) ?: ""
        extraUrl.url = Prefers.load(context).getString(KEY_ENDPOINT_EXTRA_URL + i,
            extraUrl.url ?: "")
        extraUrl.description = Prefers.load(context).getString(
            KEY_ENDPOINT_EXTRA_URL_DESCRIPTION + i, extraUrl.description ?: "")
      }
      return defValue
    }

    private fun getMockDelayIndex(context: Context): Int {
      return Prefers.load(context).getInt(KEY_ENDPOINT_MOCK_DELAY_INDEX, 0)
    }

    internal fun getMockDelayMilliseconds(context: Context): Int {
      return MOCK_DELAY_SECONDS[getMockDelayIndex(context)] * 1000
    }
  }
}
