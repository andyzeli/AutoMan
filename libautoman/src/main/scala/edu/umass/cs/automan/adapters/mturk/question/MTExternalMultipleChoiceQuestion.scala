package edu.umass.cs.automan.adapters.mturk.question

import java.security.MessageDigest
import java.util.{Date, UUID}

import edu.umass.cs.automan.adapters.mturk.mock.ExternalMultipleChoiceMockResponse
import edu.umass.cs.automan.adapters.mturk.policy.aggregation.MTurkMinimumSpawnPolicy
import edu.umass.cs.automan.core.logging.{LogType, LogLevelDebug, DebugLog}
import edu.umass.cs.automan.core.util.Utilities
import edu.umass.cs.automan.core.question.ExternalMultipleChoiceQuestion
import org.apache.commons.codec.binary.Hex

import scala.xml.NodeSeq

/**
  * Created by andrewzelinski on 6/14/16.
  */
class MTExternalMultipleChoiceQuestion(sandbox: Boolean) extends ExternalMultipleChoiceQuestion with MTurkQuestion {

  type QuestionOptionType = MTQuestionOption
  override type A = ExternalMultipleChoiceQuestion#A


  _minimum_spawn_policy = MTurkMinimumSpawnPolicy

  private val _action = if (sandbox) {
    "https://workersandbox.mturk.com/mturk/externalSubmit"
  } else {
    "https://www.mturk.com/mturk/externalSubmit"
  }

  private var _iframe_height = 450
  private var _external_url: Option[String] = None



  // public API
  def memo_hash: String = {
    val md = MessageDigest.getInstance("md5")
    new String(Hex.encodeHex(md.digest(toXML(randomize = false).toString().getBytes)))
  }



  def iframe_height_=(height: Int) { _iframe_height = height }
  def iframe_height: Int = _iframe_height

  def external_url: Option[String] = _external_url
  def external_url_=(url: String) { _external_url = Some(url)}


  override def randomized_options: List[QuestionOptionType] = Utilities.randomPermute(options)
  override def description: String = _description match { case Some(d) => d; case None => this.title }
  override def group_id: String = _title match { case Some(t) => t; case None => this.id.toString }

  // private API


  override def toMockResponse(question_id: UUID, response_time: Date, a: A, worker_id: UUID) :
  ExternalMultipleChoiceMockResponse = {
    ExternalMultipleChoiceMockResponse(question_id, response_time, a, worker_id)
  }


  override protected[mturk] def fromXML(x: scala.xml.Node) : A = {
    // There should only be a SINGLE answer here, like this:
    //    <Answer>
    //      <QuestionIdentifier>721be9fc-c867-42ce-8acd-829e64ae62dd</QuestionIdentifier>
    //      <SelectionIdentifier>count</SelectionIdentifier>
    //    </Answer>
    DebugLog("MTExternalMultipleChoiceQuestion: fromXML:\n" + x.toString,LogLevelDebug(),LogType.ADAPTER,id)

    Symbol((x \\ "Answer" \\ "SelectionIdentifier").text)
  }


  override protected[mturk]def toXML(randomize: Boolean) : scala.xml.Node = {
    <QuestionForm xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd">
      <ExternalQuestion>


         { _external_url match {
          case Some(url) => {

            <ExternalUrl>{ url }</ExternalUrl>

          }
          case None => NodeSeq.Empty
        }
          }
            <FrameHeight>{ _iframe_height }</FrameHeight>

      </ExternalQuestion>
    </QuestionForm>
  }

}
