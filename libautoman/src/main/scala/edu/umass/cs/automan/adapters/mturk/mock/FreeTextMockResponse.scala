package edu.umass.cs.automan.adapters.mturk.mock

import java.util.UUID

import edu.umass.cs.automan.core.mock.MockResponse

case class FreeTextMockResponse(question_id: UUID, answer: String) extends MockResponse {
  def toXML : String = {
    val xml_decl = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
    val assn =
      <QuestionFormAnswers xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2005-10-01/QuestionFormAnswers.xsd">
        <Answer>
          <QuestionIdentifier>{ question_id.toString }</QuestionIdentifier>
          <FreeText>{ answer }</FreeText>
        </Answer>
      </QuestionFormAnswers>
    xml_decl + assn.toString()
  }
}