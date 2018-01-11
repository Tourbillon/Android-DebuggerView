package com.anbillon.debuggerview

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal abstract class ConfigAdapter : BaseAdapter() {
  abstract override fun getItem(position: Int): String?

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, view: View?, container: ViewGroup): View {
    val ret = view ?: LayoutInflater.from(container.context)
        .inflate(android.R.layout.simple_spinner_item, container, false)
    bindView(getItem(position), ret)
    return ret
  }

  override fun getDropDownView(position: Int, view: View?, container: ViewGroup): View {
    val ret = view ?: LayoutInflater.from(container.context)
        .inflate(android.R.layout.simple_spinner_item, container, false)
    bindView(getItem(position), ret)
    return ret
  }

  private fun bindView(item: String?, view: View) {
    val holder: ViewHolder

    if (view.tag is ViewHolder) {
      holder = view.tag as ViewHolder
    } else {
      holder = ViewHolder()
      view.tag = holder

      holder.textView = view.findViewById(android.R.id.text1) as? TextView
      holder.textView?.let {
        val padding = view.resources.getDimension(R.dimen.padding).toInt()
        it.setPadding(padding, padding, padding, padding)
        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.resources.getDimension(R.dimen.font_normal))
        it.setTextColor(view.resources.getColor(R.color.text_secondary))
        it.gravity = Gravity.CENTER
      }
    }
    holder.textView?.text = item
  }

  private class ViewHolder {
    internal var textView: TextView? = null
  }
}
