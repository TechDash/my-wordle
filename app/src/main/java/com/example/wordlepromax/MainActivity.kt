package com.example.wordlepromax

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class MainActivity : AppCompatActivity() {

  private val buttons = ArrayList<Button>()
  private var word = ""
  private var firstBoot = true
  private lateinit var layout: ConstraintLayout
  private var letterCount = 0
  private var attempt = 1
  private var notFound = true
  private lateinit var answer: String
  private lateinit var words: ArrayList<String>
  private lateinit var builderC: AlertDialog

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)
    val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
    setSupportActionBar(myToolbar)

    layout = findViewById(R.id.attempt1)

    if (savedInstanceState != null) {
      firstBoot = savedInstanceState.getBoolean("FIRST_BOOT")
      words = savedInstanceState.getStringArrayList("words") as ArrayList<String>
      answer = words[Random().nextInt(words.size)].uppercase()
    }
    if (firstBoot) {
      firstBoot = false
      try {
        data
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

  @get:Throws(IOException::class)
  val data: Unit
    get() {
      val context: Context = this
      val am = context.assets
      val im = am.open("words.txt")
      val reader = BufferedReader(InputStreamReader(im))
      var st = reader.readLine()
      words = ArrayList()
      while (st != null) {
        words.add(st)
        st = reader.readLine()
      }
      answer = words[Random().nextInt(words.size)].uppercase()
    }

  fun set(view: View) {
    if (letterCount < 5 && attempt <= 6 && notFound) {
      val button = view as Button
      buttons.add(button)
      word += button.hint.toString()
      letterCount++
      for (i in 0 until letterCount) {
        val currView = layout.getChildAt(i) as TextView
        currView.text = word[i].toString()
      }
    }
  }

  fun validate(view: View?) {
    attempt++
    if (attempt > 7) {
      return
    } else if (word.length < 5 && notFound) {
      Toast.makeText(this, "Not enough letters", Toast.LENGTH_SHORT).show()
      attempt--
    } else if (!words.contains(word.lowercase())) {
      Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show()
      attempt--
    } else if (words.contains(word.lowercase())) {
      val comparison = compareWords(word, answer)
      val scale = resources.displayMetrics.density
      for (i in 0..4) {
        val currView = layout.getChildAt(i) as TextView
        val key = buttons[i]
        currView.cameraDistance = scale * 8000
        val anim = AnimatorInflater.loadAnimator(this, R.animator.flip_tile) as AnimatorSet
        anim.setTarget(currView)
        anim.start()

        if (comparison[i] == 0) {
          currView.setBackgroundColor(Color.parseColor("#3EBF44"))
          key.setBackgroundResource(R.drawable.in_place)
          currView.setTextColor(Color.WHITE)
          key.setHintTextColor(Color.WHITE)
          key.tag = "true"
        } else if (comparison[i] > 0) {
          currView.setBackgroundColor(Color.parseColor("#E1CB07"))
          currView.setTextColor(Color.WHITE)
          if (!java.lang.Boolean.parseBoolean(key.tag as String)) {
            key.setBackgroundResource(R.drawable.in_word)
            key.setHintTextColor(Color.WHITE)
            key.tag = "yellow"
          }
        } else if (comparison[i] < 0) {
          currView.setBackgroundColor(Color.parseColor("#474747"))
          currView.setTextColor(Color.WHITE)
          val tag = key.tag as String
          if (!java.lang.Boolean.parseBoolean(tag) && tag != "yellow") {
            key.setBackgroundResource(R.drawable.not_in_word)
            key.setHintTextColor(Color.WHITE)
          }
        }
      }
      if (word != answer && attempt == 7) {
        Toast.makeText(this, answer, Toast.LENGTH_LONG).show()
        updateGameData(false, attempt - 1)
        popUp()
      }
      if (word == answer) {
        complement()
        notFound = false
        updateGameData(true, attempt - 1)
        popUp()
      }
      when (attempt) {
        1 -> layout = findViewById(R.id.attempt1)
        2 -> layout = findViewById(R.id.attempt2)
        3 -> layout = findViewById(R.id.attempt3)
        4 -> layout = findViewById(R.id.attempt4)
        5 -> layout = findViewById(R.id.attempt5)
        6 -> layout = findViewById(R.id.attempt6)
      }
      buttons.clear()
      letterCount = 0
      word = ""
    }
  }

  private fun updateGameData(won: Boolean, attempt: Int) {
    val gameData = getSharedPreferences("gameData", MODE_PRIVATE)

    val played = gameData.getInt("played", 0) + 1
    val maxStreak = gameData.getInt("maxStreak", 0)
    val currStreak = gameData.getInt("currStreak", 0)
    val wins = gameData.getInt("wins", 0) + 1

    with(gameData.edit()) {
      putInt("played", played)
      if (won) {
        putInt("wins", wins)
        putInt("currStreak", currStreak + 1)
        if (currStreak >= maxStreak) {
          putInt("maxStreak", currStreak + 1)
        }
      } else {
        putInt("currStreak", 0)
      }
      apply()
    }

    val guessDistro = getSharedPreferences("distro", MODE_PRIVATE)
    if (won) {
      val attemptNum = guessDistro.getInt(attempt.toString(), 0) + 1
      with(guessDistro.edit()) {
        putInt(attempt.toString(), attemptNum)
        apply()
      }
    }
  }

  private fun complement() {
    when (attempt - 1) {
      1 -> Toast.makeText(this, "Smart", Toast.LENGTH_SHORT).show()
      2 -> Toast.makeText(this, "Brilliant", Toast.LENGTH_SHORT).show()
      3 -> Toast.makeText(this, "Awesome", Toast.LENGTH_SHORT).show()
      4 -> Toast.makeText(this, "Bravo", Toast.LENGTH_SHORT).show()
      5 -> Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show()
      6 -> Toast.makeText(this, "So close", Toast.LENGTH_SHORT).show()
    }
  }

  private fun popUp() {
    val gameData = getSharedPreferences("gameData", MODE_PRIVATE)
    val won = gameData.getInt("wins", 0).toFloat()
    val playedNum = gameData.getInt("played", 0).toFloat()
    val percentage = (won.div(playedNum)).times(100)

    val builder = AlertDialog.Builder(this)

    val popUp = layoutInflater.inflate(R.layout.play_again, null)
    val played = popUp.findViewById<TextView>(R.id.playedNum)
    val winPercent = popUp.findViewById<TextView>(R.id.winPercentageNum)
    val maxStreak = popUp.findViewById<TextView>(R.id.maxStreakNum)
    val currStreak = popUp.findViewById<TextView>(R.id.currStreakNum)

    played.text = playedNum.toInt().toString()
    winPercent.text = percentage.toInt().toString()
    maxStreak.text = gameData.getInt("maxStreak", 0).toString()
    currStreak.text = gameData.getInt("currStreak", 0).toString()

    val chart = popUp.findViewById<HorizontalBarChart>(R.id.idBarChart)
    val dataSet = BarDataSet(getBarEntries(), null)
    val barData = BarData(dataSet)
    chart.data = barData
    dataSet.setColors(ContextCompat.getColor(this, R.color.app_green))

    val vf: ValueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
      }
    }
    dataSet.valueFormatter = vf
    dataSet.valueTextSize = 15f
    if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
      Configuration.UI_MODE_NIGHT_NO
    ) {
      dataSet.valueTextColor = Color.BLACK
    } else {
      dataSet.valueTextColor = Color.WHITE
    }
    chart.xAxis.textSize = 15f
    chart.description.isEnabled = false
    chart.xAxis.setDrawGridLines(false)
    chart.axisLeft.setDrawGridLines(false)
    chart.axisRight.setDrawGridLines(false)
    chart.axisRight.isEnabled = false
    chart.axisLeft.setDrawLabels(false)
    chart.legend.isEnabled = false
    builder.setView(popUp)
    builderC = builder.create()
    builderC.show()
  }

  private fun getBarEntries(): ArrayList<BarEntry> {
    val guessDistro = getSharedPreferences("distro", MODE_PRIVATE)
    val entries = ArrayList<BarEntry>()
    if (guessDistro.all.isEmpty()) {
      with(guessDistro.edit()) {
        for (i in 1..6) {
          putInt(i.toString(), 0)
        }
        apply()
      }
    }

    val map = guessDistro.all as HashMap<String, Int>
    var index = 0f
    map.forEach { (_, value) ->
      index++
      entries.add(BarEntry(index, value.toFloat()))
    }
    return entries
  }

  fun delete(view: View?) {
    if (word.length - 1 >= 0) {
      val currView = layout.getChildAt(word.length - 1) as TextView
      currView.text = ""
      word = word.substring(0, word.length - 1)
      buttons.removeAt(buttons.size - 1)
      letterCount--
    }
  }

  fun playAgain(view: View?) {
    if (!notFound || attempt == 7) {
      builderC.cancel()
      recreate()
    }
  }

  public override fun onSaveInstanceState(outState: Bundle) {
    outState.putString("word", word)
    outState.putBoolean("FIRST_BOOT", firstBoot)
    outState.putStringArrayList("words", words)
    super.onSaveInstanceState(outState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    for (i in 0 until menu.size()) {
      val drawable = menu.getItem(i).icon
      if (drawable != null) {
        drawable.mutate()
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
          Configuration.UI_MODE_NIGHT_NO
        ) {
          drawable.setColorFilter(
            resources.getColor(R.color.black),
            PorterDuff.Mode.SRC_ATOP
          )
        } else {
          drawable.setColorFilter(
            resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_ATOP
          )
        }
      }
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.howtoPlay) {
      val intent = Intent(this, HowToPlay::class.java)
      startActivity(intent)
    }
    if (item.itemId == R.id.stats) {
      popUp()
    }
    return super.onOptionsItemSelected(item)
  }
}