package com.anbillon.debuggerview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal class LogPrinterLayout : LinearLayout, View.OnClickListener {
  private val subscriptions = CompositeDisposable()

  private var recyclerView: RecyclerView? = null
  private var adapter: LogAdapter? = null

  constructor(context: Context) : super(context) {
    initData()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initData()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    initData()
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
      context, attrs, defStyleAttr, defStyleRes) {
    initData()
  }

  private fun initData() {

  }

  override fun onFinishInflate() {
    super.onFinishInflate()

    recyclerView = findViewById<View>(R.id.recycler_log_printer) as RecyclerView
    val layoutManager = LinearLayoutManager(context)
    layoutManager.stackFromEnd = true
    layoutManager.reverseLayout = true
    recyclerView?.layoutManager = layoutManager
    adapter = LogAdapter()
    recyclerView?.adapter = adapter

    findViewById<View>(R.id.btn_scroll_to_top).setOnClickListener(this)
    findViewById<View>(R.id.btn_scroll_to_bottom).setOnClickListener(this)
    findViewById<View>(R.id.btn_clear).setOnClickListener(this)
    findViewById<View>(R.id.btn_share).setOnClickListener(this)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    adapter?.setLogs(LumberYard.bufferedLogs())
    subscriptions.add(LumberYard.logs()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ log -> adapter!!.addLog(log) }) { throwable -> Timber.e(throwable.message) })
  }

  override fun onDetachedFromWindow() {
    adapter!!.clear()
    subscriptions.clear()
    super.onDetachedFromWindow()
  }

  override fun onClick(v: View) {
    val id = v.id
    if (id == R.id.btn_scroll_to_top) {
      val count = adapter!!.itemCount
      val position = count - 1
      if (position > 0) {
        recyclerView?.scrollToPosition(position)
      }
    } else if (id == R.id.btn_scroll_to_bottom) {
      recyclerView!!.scrollToPosition(0)
    } else if (id == R.id.btn_clear) {
      adapter?.clear()
      LumberYard.clear()
    } else if (id == R.id.btn_share) {
      subscriptions.add(Util.logsShare(context))
    }
  }

  private class LogAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val logs = LinkedList<Log>()

    override fun getItemCount(): Int = logs.size

    internal fun setLogs(logs: List<Log>?) {
      if (logs != null) {
        this.logs.clear()
        this.logs.addAll(logs)
        notifyDataSetChanged()
      }
    }

    internal fun clear() {
      logs.clear()
      notifyDataSetChanged()
    }

    internal fun addLog(log: Log) {
      logs.addLast(log)
      notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.setEntry(logs[position])
    }
  }

  private class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(
      itemView) {
    internal var tvLevel: TextView = itemView.findViewById<View>(R.id.tv_level) as TextView
    internal var tvTag: TextView = itemView.findViewById<View>(R.id.tv_tag) as TextView
    internal var tvMessage: TextView = itemView.findViewById<View>(R.id.tv_message) as TextView

    internal fun setEntry(log: Log?) {
      log?.let {
        itemView.setBackgroundResource(backgroundForLevel(it.priority ?: 0))
        tvLevel.text = it.level()
        tvTag.text = it.tag
        tvMessage.text = it.message()
      }
    }

    companion object {
      @DrawableRes internal fun backgroundForLevel(level: Int): Int {
        return when (level) {
          android.util.Log.VERBOSE, android.util.Log.DEBUG -> R.color.debug_log_accent_debug
          android.util.Log.INFO -> R.color.debug_log_accent_info
          android.util.Log.WARN -> R.color.debug_log_accent_warn
          android.util.Log.ERROR, android.util.Log.ASSERT -> R.color.debug_log_accent_error
          else -> R.color.debug_log_accent_unknown
        }
      }
    }
  }
}
