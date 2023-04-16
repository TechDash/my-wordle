package com.example.wordlepromax

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HowToPlay : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.how_to_play)
    val toolBar = findViewById<Toolbar>(R.id.my_toolbar)
    setSupportActionBar(toolBar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
  }
}