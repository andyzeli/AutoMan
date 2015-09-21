package edu.umass.cs.automan.core.question

import edu.umass.cs.automan.core.AutomanAdapter
import edu.umass.cs.automan.core.answer._
import edu.umass.cs.automan.core.info.QuestionType
import edu.umass.cs.automan.core.logging.Memo
import edu.umass.cs.automan.core.scheduler.Scheduler
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class FreeTextQuestion extends ScalarQuestion {
  type A = String
  type QuestionOptionType <: QuestionOption

  protected var _allow_empty: Boolean = false
  protected var _num_possibilities: BigInt = 1000
  protected var _pattern: Option[String] = None
  protected var _pattern_error_text: String = ""

  def allow_empty_pattern_=(ae: Boolean) { _allow_empty = ae }
  def allow_empty_pattern: Boolean = _allow_empty
  def num_possibilities: BigInt = _pattern match {
    case Some(p) =>
      val count = PictureClause(p, _allow_empty)._2
      if (count > 1000) 1000 else count
    case None => 1000
  }
  def pattern: String = _pattern match { case Some(p) => p; case None => ".*" }
  def pattern_=(p: String) { _pattern = Some(p) }
  def pattern_error_text: String = _pattern_error_text
  def pattern_error_text_=(p: String) { _pattern_error_text = p }
  def regex: String = _pattern match {
    case Some(p) => PictureClause(p, _allow_empty)._1
    case None => "^.*$"
  }

  override protected[automan] def getQuestionType = QuestionType.FreeTextQuestion
  override protected[automan] def getOutcome(adapter: AutomanAdapter) : O = {
    val scheduler = new Scheduler(this, adapter)
    val f = Future{
      blocking {
        scheduler.run().asInstanceOf[AA]
      }
    }
    ScalarOutcome(f)
  }
}
