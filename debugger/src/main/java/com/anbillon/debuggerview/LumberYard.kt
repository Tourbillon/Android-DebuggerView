package com.anbillon.debuggerview

import android.content.Context
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import okio.BufferedSink
import okio.Okio
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*


/**
 * @author Cosouly (cosouly@gmail.com)
 */
internal object LumberYard {
  private const val BUFFER_SIZE = 200

  private val entries = ArrayDeque<Log>(BUFFER_SIZE + 1)
  private val entrySubject = PublishProcessor.create<Log>()

  init {
    Timber.plant(object : Timber.DebugTree() {
      override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
        addLog(Log(priority, tag, message))
      }
    })
  }

  @Synchronized private fun addLog(entry: Log) {
    entries.addLast(entry)
    if (entries.size > BUFFER_SIZE) {
      entries.removeFirst()
    }

    entrySubject.onNext(entry)
  }

  fun bufferedLogs(): List<Log> {
    return ArrayList(entries)
  }

  fun logs(): Flowable<Log> {
    return entrySubject.onBackpressureBuffer()
  }

  fun clear() {
    entries.clear()
  }

  fun save(context: Context): Observable<File> {
    return Observable.create({ emitter ->
      val folder = context.externalCacheDir ?: context.cacheDir
      val output = File(folder, System.currentTimeMillis().toString() + ".log")

      var sink: BufferedSink? = null
      try {
        sink = Okio.buffer(Okio.sink(output))
        for ((_, _, message) in bufferedLogs()) {
          sink?.writeUtf8(message ?: "")?.writeUtf8("\n")
        }
        // need to close before emiting file to the subscriber, because when subscriber receives data in the same thread
        // the file may be truncated
        sink?.close()
        sink = null

        emitter.onNext(output)
        emitter.onComplete()
      } catch (e: IOException) {
        emitter.onError(e)
      } finally {
        try {
          sink?.close()
        } catch (e: IOException) {
          emitter.onError(e)
        }
      }
    })
  }

  fun cleanUp(context: Context) {
    Observable.defer {
      val folder = context.externalCacheDir ?: context.cacheDir
      folder?.listFiles()?.filter { it.name.endsWith(".log") }?.forEach { it.delete() }
      Observable.empty<Any>()
    }.subscribeOn(Schedulers.io()).subscribe()
  }
}