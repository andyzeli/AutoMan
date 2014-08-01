package edu.umass.cs.automan.core.question

import edu.umass.cs.automan.core.answer.FreeTextAnswer
import util.matching.Regex

abstract class FreeTextQuestion extends ScalarQuestion {
  type A = FreeTextAnswer

  protected var _pattern_error_text: String = ""

  def num_possibilities: BigInt
  def num_possibilities_=(i: BigInt)
  def regex: Regex
//  def regex_=(r: Regex)
  def pattern_=(s: String)
  def pattern: String
  def pattern_error_text: String = _pattern_error_text
  def pattern_error_text_=(p: String) { _pattern_error_text = p }
}