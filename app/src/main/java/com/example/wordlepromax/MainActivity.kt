package com.example.wordlepromax

import android.animation.AnimatorSet
import android.animation.AnimatorInflater
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

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.formatter.ValueFormatter

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.Random

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.Throws

class MainActivity : AppCompatActivity() {
  private lateinit var answer: String
  private var word = ""
  private lateinit var dict: DictionaryBST
  private var firstBoot = true
  private lateinit var words: ArrayList<String>
  private lateinit var layout: ConstraintLayout
  private var letterCount = 0
  private var attempt = 1
  private val buttons = ArrayList<Button>()
  private var notFound = true
  private lateinit var db: DataHandler
  private lateinit var builderC: AlertDialog

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    pickTheme()
    val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
    setSupportActionBar(myToolbar)

    db = DataHandler(this)
    layout = findViewById(R.id.attempt1)

    if (savedInstanceState != null) {
      firstBoot = savedInstanceState.getBoolean("FIRST_BOOT")
      dict = savedInstanceState.getParcelable("dict")!!
      words = savedInstanceState.getStringArrayList("words") as ArrayList<String>
      answer = words[Random().nextInt(words.size)].uppercase(Locale.getDefault())
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

  private fun pickTheme() {
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> setContentView(R.layout.activity_main_dark)
      Configuration.UI_MODE_NIGHT_NO -> setContentView(R.layout.activity_main)
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
      dict = DictionaryBST()
      words = ArrayList()
      while (st != null) {
        dict.addWord(st)
        words.add(st)
        st = reader.readLine()
      }
      answer = words[Random().nextInt(words.size)].uppercase(Locale.getDefault())
    }

  fun set(view: View) {
    if (letterCount < 5 && attempt <= 6 && notFound) {
      val button = view as Button
      buttons.add(button)
      when (attempt) {
        1 -> layout = findViewById(R.id.attempt1)
        2 -> layout = findViewById(R.id.attempt2)
        3 -> layout = findViewById(R.id.attempt3)
        4 -> layout = findViewById(R.id.attempt4)
        5 -> layout = findViewById(R.id.attempt5)
        6 -> layout = findViewById(R.id.attempt6)
      }
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
    } else if (!dict.isWord(word)) {
      Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show()
      attempt--
    } else if (dict.isWord(word)) {
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
        }
        if (comparison[i] > 0) {
          currView.setBackgroundColor(Color.parseColor("#E1CB07"))
          currView.setTextColor(Color.WHITE)
          if (!java.lang.Boolean.parseBoolean(key.tag as String)) {
            key.setBackgroundResource(R.drawable.in_word)
            key.setHintTextColor(Color.WHITE)
            key.tag = "yellow"
          }
        }
        if (comparison[i] < 0) {
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
        db.updateGameData(false, attempt - 1)
        popUp()
      }
      if (word == answer) {
        complement()
        notFound = false
        db.updateGameData(true, attempt - 1)
        popUp()
      }
      buttons.clear()
      letterCount = 0
      word = ""
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
    val data = db.data
    val won = data["won"]?.toFloat()
    val playedNum = data["played"]?.toFloat()
    val percentage = (won?.div(playedNum!!))?.times(100)
    val builder = AlertDialog.Builder(this)
    val popUp = layoutInflater.inflate(R.layout.play_again, null)
    val played = popUp.findViewById<TextView>(R.id.playedNum)
    val winPercent = popUp.findViewById<TextView>(R.id.winPercentageNum)
    val maxStreak = popUp.findViewById<TextView>(R.id.maxStreakNum)
    val currStreak = popUp.findViewById<TextView>(R.id.currStreakNum)
    val playedTitle = popUp.findViewById<TextView>(R.id.played)
    val winPercentTitle = popUp.findViewById<TextView>(R.id.winPercentage)
    val maxStreakTitle = popUp.findViewById<TextView>(R.id.maxStreak)
    val currStreakTitle = popUp.findViewById<TextView>(R.id.currStreak)
    val stat = popUp.findViewById<TextView>(R.id.stat)
    val guess = popUp.findViewById<TextView>(R.id.guess)
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> {
        played.setTextColor(Color.WHITE)
        winPercent.setTextColor(Color.WHITE)
        maxStreak.setTextColor(Color.WHITE)
        currStreak.setTextColor(Color.WHITE)
        playedTitle.setTextColor(Color.WHITE)
        winPercentTitle.setTextColor(Color.WHITE)
        maxStreakTitle.setTextColor(Color.WHITE)
        currStreakTitle.setTextColor(Color.WHITE)
        stat.setTextColor(Color.WHITE)
        guess.setTextColor(Color.WHITE)
      }
      Configuration.UI_MODE_NIGHT_NO -> {
        played.setTextColor(Color.BLACK)
        winPercent.setTextColor(Color.BLACK)
        maxStreak.setTextColor(Color.BLACK)
        currStreak.setTextColor(Color.BLACK)
        playedTitle.setTextColor(Color.BLACK)
        winPercentTitle.setTextColor(Color.BLACK)
        maxStreakTitle.setTextColor(Color.BLACK)
        currStreakTitle.setTextColor(Color.BLACK)
        stat.setTextColor(Color.BLACK)
        guess.setTextColor(Color.BLACK)
      }
    }
    played.text = data["played"].toString()
    if (percentage != null) {
      winPercent.text = percentage.toInt().toString()
    }
    maxStreak.text = data["maxStreak"].toString()
    currStreak.text = data["currStreak"].toString()
    val chart = popUp.findViewById<HorizontalBarChart>(R.id.idBarChart)
    val dataSet = BarDataSet(getBarEntries(data), null)
    val barData = BarData(dataSet)
    chart.data = barData
    dataSet.setColors(ContextCompat.getColor(this, R.color.app_green))

    // setting text color.
    dataSet.valueTextColor = Color.BLACK
    val vf: ValueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
      }
    }
    dataSet.valueFormatter = vf
    dataSet.valueTextSize = 15f
    if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
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

  private fun getBarEntries(map: HashMap<String, Int>): ArrayList<BarEntry> {
    val entries = ArrayList<BarEntry>()
    val one = map["one"]!!
    val two = map["two"]!!
    val three = map["three"]!!
    val four = map["four"]!!
    val five = map["five"]!!
    val six = map["six"]!!
    entries.add(BarEntry(1f, one.toFloat()))
    entries.add(BarEntry(2f, two.toFloat()))
    entries.add(BarEntry(3f, three.toFloat()))
    entries.add(BarEntry(4f, four.toFloat()))
    entries.add(BarEntry(5f, five.toFloat()))
    entries.add(BarEntry(6f, six.toFloat()))
    return entries
  }

  fun delete(view: View?) {
    when (attempt) {
      1 -> layout = findViewById(R.id.attempt1)
      2 -> layout = findViewById(R.id.attempt2)
      3 -> layout = findViewById(R.id.attempt3)
      4 -> layout = findViewById(R.id.attempt4)
      5 -> layout = findViewById(R.id.attempt5)
      6 -> layout = findViewById(R.id.attempt6)
    }

    if (word.length - 1 >= 0) {
      val currView = layout.getChildAt(word.length - 1) as TextView
      currView.text = ""
      word = word.substring(0, word.length - 1)
      buttons.removeAt(buttons.size - 1)
      letterCount--
    }
  }

  private fun compareWords(word1: String, word2: String): ArrayList<Int> {
    val output = ArrayList<Int>()
    val seenOnce = ArrayList<String>()
    val occ = occurrences(word1, word2)
    for (i in 0..4) {
      output.add(-1)
    }
    for (i in word1.indices) {
      val currLetter = word1[i].toString()
      val letterOcc = occ[currLetter]
      if (word1[i] == word2[i]) output[i] = 0
      if (!seenOnce.contains(currLetter)) {
        if (letterOcc != null) {
          if (letterOcc.size < 2) {
            seenOnce.add(currLetter)
          }
          for (j in letterOcc.indices) {
            if (j == letterOcc.size) break
            val pos = letterOcc[j]
            if (i == pos) {
              output[i] = 0
              letterOcc.removeAt(j)
              occ[currLetter] = letterOcc
            } else if (output[i] == -1) {
              output[i] = 1
              letterOcc.removeAt(j)
              occ[currLetter] = letterOcc
            }
          }
        }
      }
    }
    return output
  }

  private fun occurrences(word1: String, word2: String): HashMap<String, ArrayList<Int>?> {
    val output = HashMap<String, ArrayList<Int>?>()
    for (i in word1.indices) {
      for (j in word1.indices) {
        if (word1[i] == word2[j]) {
          if (output.containsKey(word1[i].toString())) {
            val occ = output[word1[i].toString()]!!
            if (occ[occ.size - 1] < j) {
              occ.add(j)
              output[word1[i].toString()] = occ
            }
          } else {
            val occ = ArrayList<Int>()
            occ.add(j)
            output[word1[i].toString()] = occ
          }
        }
      }
    }
    return output
  }

  fun playAgain(view: View?) {
    if (!notFound || attempt == 7) {
      builderC.cancel()
      recreate()
    }
  }

  public override fun onSaveInstanceState(outState: Bundle) {
    // call superclass to save any view hierarchy
    outState.putString("word", word)
    outState.putParcelable("dict", dict)
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
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
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