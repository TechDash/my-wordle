package com.example.wordlepromax

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HowToPlay : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> setContentView(R.layout.how_to_play_dark)
      Configuration.UI_MODE_NIGHT_NO -> setContentView(R.layout.how_to_play)
    }
    val toolBar = findViewById<Toolbar>(R.id.my_toolbar)
    setSupportActionBar(toolBar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
  }
}