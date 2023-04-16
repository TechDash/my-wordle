package com.example.wordlepromax

import java.util.HashMap
import kotlin.collections.ArrayList

/**
 * Compares input and the answer
 *
 * @param guess Input of the user
 *
 * @param answer Answer to the current game
 *
 * @return Returns a list of int that indicate if a char is present in the right position of the guess
 */
fun compareWords(guess: String, answer: String): ArrayList<Int> {
  val output = arrayListOf(-1, -1, -1, -1, -1)
  val occ = occurrences(guess, answer)
  val seen = ArrayList<Char>()

  for (i in 0..4) {
    if (guess[i] == answer[i]) {
      output[i] = 0
      seen.add(guess[i])
    }
  }

  for (i in 0..4) {
    if (output[i] != -1) continue
    val currLetter = guess[i]
    if (answer.contains(currLetter)) {
      val times = seen.filter { c -> c == currLetter }
      if (seen.contains(currLetter) && occ.containsKey(currLetter)) {
        if (occ[currLetter]!!.size == times.size + 1) {
          output[i] = 1
        }
      } else {
        output[i] = 1
      }
    }
    seen.add(currLetter)
  }
  return output
}

/**
 * @param guess Users guess
 *
 * @param answer Answer to the current game
 *
 * @return Returns a HashMap of characters and their indices in that are in the answer
 */
fun occurrences(guess: String, answer: String): HashMap<Char, LinkedHashSet<Int>> {
  val output = HashMap<Char, LinkedHashSet<Int>>()
  for (i in guess.indices) {
    val char = guess[i]
    var index = answer.indexOf(char, 0)
    while (index >= 0) {
      val occ = output[char] ?: LinkedHashSet()
      occ.add(index)
      output[char] = occ
      index = answer.indexOf(char, index + 1)
    }
  }
  return output
}