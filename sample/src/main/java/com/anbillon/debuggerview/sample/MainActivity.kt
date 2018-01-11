package com.anbillon.debuggerview.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Flavor.appContainer(this).bind(this).addView(
        layoutInflater.inflate(R.layout.activity_main, null))

    val textView = findViewById<TextView>(R.id.textview)
    textView?.text = "${Flavor.env(this)}\n${Flavor.GITUrl(this)}\n${Flavor.SEUrl(
        this)}\n${Flavor.SNSUrl(
        this)}\n${Flavor.TweetUrl(this)}\nmock delay${Flavor.mockDelayMillis(this)}"

    textView.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
  }
}
