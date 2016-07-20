import com.amazonaws.mturk.addon._
import edu.umass.cs.automan.adapters.mturk._
import edu.umass.cs.automan.adapters.mturk.mock.MockSetup
import edu.umass.cs.automan.adapters.mturk.question.MTRadioButtonVectorQuestion
import edu.umass.cs.automan.core.answer._
import edu.umass.cs.automan.core.logging.LogLevelDebug
import edu.umass.cs.automan.core.policy.aggregation.UserDefinableSpawnPolicy


object simple_program extends App {
  //val opts = Utilities.unsafe_optparse(args, "simple_program")

  val a = MTurkAdapter { mt =>
    mt.access_key_id = "mykey" //AKIAIMWMTFQUYWFECTVQ"
    mt.secret_access_key = "mysec"//"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"
    mt.sandbox_mode = true
    mt.use_mock= MockSetup(budget = 8)
    mt.logging = LogConfig.NO_LOGGING
    mt.log_verbosity = LogLevelDebug()


  }


  //i think this is how anonymous constructors work. define class Foo. then val a = Foo { f => f.value = fvalue...
  // the compiler initializes a Foo, locally called f with the paramaters listed

  val inputFile = "url.input"

  val input: HITDataInput = new HITDataCSVReader(inputFile)

  val qustFile = "ext.quest"
  val questionNode: HITQuestion = new HITQuestion(qustFile)

  val success: HITDataOutput = new HITDataCSVWriter(inputFile + ".success")
  val failure: HITDataOutput = new HITDataCSVWriter(inputFile + ".failure")

//and here RadioButtonDistribution method is defined in Automan adapter, but the a implementation is the MTurkAdapter
  // . the following is type simpleprogram.a.RBDQ#O

  def which_one() = a.RadioButtonDistributionQuestion { q => //calls schedule(init,
    // rbdqFactory(), which

    // is
    // overriden
    // in MTurkAdapter implementation. class is MTRadioButtonVectorQuestion
    q.budget = 8.00
    q.text = "Which one of these does not belong?"
    q.options = List(
      a.Option('oscar, "Oscar the Grouch", "http://tinyurl.com/qfwlx56"),
      a.Option('kermit, "Kermit the Frog", "http://tinyurl.com/nuwyz3u"),
      a.Option('spongebob, "Spongebob Squarepants", "http://tinyurl.com/oj6wzx6"),
      a.Option('cookie, "Cookie Monster", "http://tinyurl.com/otb6thl"),
      a.Option('count, "The Count", "http://tinyurl.com/nfdbyxa")
    )
    q.minimum_spawn_policy = UserDefinableSpawnPolicy(0)
  }

  automan(a) {
    which_one().answer match {
      case answer: Answer[Symbol] =>
        println("The answer is: " + answer.value)
      case lowconf: LowConfidenceAnswer[Symbol] =>
        println(
          "You ran out of money. The best answer is \"" +
          lowconf.value + "\" with a confidence of " + lowconf.confidence
        )
    }
  }
}