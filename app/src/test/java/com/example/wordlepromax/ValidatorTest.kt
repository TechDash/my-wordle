package com.example.wordlepromax

import org.junit.Assert.assertEquals
import org.junit.Test

class ValidatorTest {
  @Test
  fun testOccurrences1() {
    val stringOne = "meets"
    val stringTwo = "meats"
    val expected = hashMapOf(
      'm' to linkedSetOf(0),
      'e' to linkedSetOf(1, 1),
      't' to linkedSetOf(3),
      's' to linkedSetOf(4)
    )
    assertEquals(expected, occurrences(stringOne, stringTwo))
  }

  @Test
  fun testOccurrences2() {
    val stringOne = "meats"
    val stringTwo = "meets"
    val expected = hashMapOf(
      'm' to linkedSetOf(0),
      'e' to linkedSetOf(1, 2),
      't' to linkedSetOf(3),
      's' to linkedSetOf(4)
    )
    assertEquals(expected, occurrences(stringOne, stringTwo))
  }

  @Test
  fun testOccurrences3() {
    val stringOne = "sleep"
    val stringTwo = "meets"
    val expected = hashMapOf(
      's' to linkedSetOf(4),
      'e' to linkedSetOf(1, 2),
    )
    assertEquals(expected, occurrences(stringOne, stringTwo))
  }

  @Test
  fun testOccurrences4() {
    val stringOne = "sleep"
    val stringTwo = "sleep"
    val expected = hashMapOf(
      's' to linkedSetOf(0),
      'l' to linkedSetOf(1),
      'e' to linkedSetOf(2, 3),
      'p' to linkedSetOf(4),
    )
    assertEquals(expected, occurrences(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord1() {
    val stringOne = "sleep"
    val stringTwo = "meets"
    val expected = arrayListOf(1, -1, 0, 1, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord2() {
    val stringOne = "slept"
    val stringTwo = "meets"
    val expected = arrayListOf(1, -1, 0, -1, 1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord3() {
    val stringOne = "sleep"
    val stringTwo = "mends"
    val expected = arrayListOf(1, -1, 1, -1, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }


  @Test
  fun testCompareWord4() {
    val stringOne = "sleep"
    val stringTwo = "sleep"
    val expected = arrayListOf(0, 0, 0, 0, 0)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord5() {
    val stringOne = "books"
    val stringTwo = "broke"
    val expected = arrayListOf(0, -1, 0, 0, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord6() {
    val stringOne = "apple"
    val stringTwo = "brick"
    val expected = arrayListOf(-1, -1, -1, -1, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord7() {
    val stringOne = "eager"
    val stringTwo = "sleep"
    val expected = arrayListOf(1, -1, -1, 0, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord8() {
    val stringOne = "sleep"
    val stringTwo = "eager"
    val expected = arrayListOf(-1, -1, 1, 0, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord9() {
    val stringOne = "asses"
    val stringTwo = "asset"
    val expected = arrayListOf(0, 0, 0, 0, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }

  @Test
  fun testCompareWord10() {
    val stringOne = "asset"
    val stringTwo = "asses"
    val expected = arrayListOf(0, 0, 0, 0, -1)
    assertEquals(expected, compareWords(stringOne, stringTwo))
  }
}