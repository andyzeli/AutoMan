package edu.umass.cs.automan.core

import java.util.Locale
import edu.umass.cs.automan.core.answer.Answer
import edu.umass.cs.automan.core.info.StateInfo
import edu.umass.cs.automan.core.question._
import memoizer.Memo
import edu.umass.cs.automan.core.scheduler.{Scheduler, Thunk}
import scala.concurrent.{blocking, Future}

abstract class AutomanAdapter {
  // question types are determined by adapter implementations
  // answer types are invariant
//  type CBQ <: CheckboxQuestion                    // answer scalar
//  type CBDQ <: CheckboxDistributionQuestion       // answer vector
//  type FTQ <: FreeTextQuestion                    // answer scalar
//  type FTDQ <: FreeTextDistributionQuestion       // answer vector
  type RBQ = RadioButtonQuestion                 // answer scalar
//  type RBDQ <: RadioButtonDistributionQuestion    // answer vector

//  protected var _default_budget: BigDecimal = 5.00
  protected var _default_confidence: Double = 0.95
  protected var _locale: Locale = Locale.getDefault
  protected var _memoizer: Option[Memo] = None
  protected var _plugins: List[Class[_ <: Plugin]] = List.empty
  protected var _plugins_initialized: List[_ <: Plugin] = List.empty
  protected var _poll_interval_in_s : Int = 30
  protected var _schedulers: List[Scheduler[_]] = List.empty
//  protected var _thunklog: Option[ThunkLogger] = None
  protected var _thunk_db: String = "ThunkLogDB"
  protected var _thunk_conn_string: String = "jdbc:derby:" + _thunk_db + ";create=true"
  protected var _thunk_user: String = ""
  protected var _thunk_pass: String = ""
  protected var _use_memoization: Boolean = true

  // user-visible getters and setters
  def default_confidence: Double = _default_confidence
  def default_confidence_=(c: Double) { _default_confidence = c }
  def locale: Locale = _locale
  def locale_=(l: Locale) { _locale = l }
  def plugins: List[Class[_ <: Plugin]] = _plugins
  def plugins_=(ps: List[Class[_ <: Plugin]]) { _plugins = ps }
  def use_memoization = _use_memoization
  def use_memoization_=(um: Boolean) { _use_memoization = um }

  // marshaling calls
  protected[automan] def accept[A](t: Thunk[A]) : Thunk[A]
  protected[automan] def backend_budget(): BigDecimal
  protected[automan] def cancel[A](t: Thunk[A]) : Thunk[A]
  protected[automan] def post[A](ts: List[Thunk[A]], exclude_worker_ids: List[String]) : List[Thunk[A]]
  protected[automan] def process_custom_info[A](t: Thunk[A], i: Option[String]) : Thunk[A]
  protected[automan] def reject[A](t: Thunk[A]) : Thunk[A]
  protected[automan] def retrieve[A](ts: List[Thunk[A]]) : List[Thunk[A]]
//  protected[automan] def timeout[A <: Answer](ts: List[Thunk[A]]) : List[Thunk[A]]
  protected[automan] def question_startup_hook[A](q: Question[A]): Unit = {}
  protected[automan] def question_shutdown_hook[A](q: Question[A]): Unit = {}

  // end-user syntax: Question creation
//  def CheckboxQuestion(init: CBQ => Unit) : Future[CheckboxOldAnswer] = schedule(CBQFactory(), init)
//  def CheckboxDistributionQuestion(init: CBDQ => Unit) : Future[Set[CheckboxOldAnswer]] = scheduleVector(CBDQFactory(), init)
//  def FreeTextQuestion(init: FTQ => Unit) : Future[FreeTextOldAnswer] = schedule(FTQFactory(), init)
//  def FreeTextDistributionQuestion(init: FTDQ => Unit) : Future[Set[FreeTextOldAnswer]] = scheduleVector(FTDQFactory(), init)
  def RadioButtonQuestion(init: Question[Symbol] => Unit) = schedule(RBQFactory(), init)
//  def RadioButtonDistributionQuestion(init: RBDQ => Unit) : Future[Set[RadioButtonOldAnswer]] = scheduleVector(RBDQFactory(), init)
  def Option(id: Symbol, text: String) : QuestionOption

  // state management
  protected[automan] def init() {
    plugins_init()
    if (_use_memoization) {
      memo_init()
      thunklog_init()
    }
  }
  protected[automan] def close() = {
    plugins_shutdown()
  }
  private def plugins_init() {
    // load user-supplied plugins using reflection
    _plugins_initialized = _plugins.map { clazz =>
      val instance = clazz.newInstance()
      instance.startup(this)
      instance
    }
  }
  private def plugins_shutdown(): Unit = {
    _plugins_initialized.foreach { plugin => plugin.shutdown() }
  }
  private def memo_init() {
//    _memoizer = Some(new AutomanMemoizer(_memo_conn_string, _memo_user, _memo_pass))
    ???
  }
  private def thunklog_init() {
//    _thunklog = Some(new ThunkLogger(_thunk_conn_string, _thunk_user, _thunk_pass))
    ???
  }
//  def state_snapshot(): StateInfo = {
//    StateInfo(_schedulers.flatMap { s => s.state })
//  }

  // thread management
  private def schedule[A](q: Question[A], init: Question[A] => Unit): Answer[A] = {
    // initialize question with end-user lambda
    init(q)
    // initialize QA strategy
    q.init_strategy()
    // startup a new scheduler
    val sched = new Scheduler(q, this, _memoizer, _poll_interval_in_s)
    // add scheduler to list for plugin inspection
    _schedulers = sched :: _schedulers
    // start job
    q.getAnswer(sched)
  }


  // subclass instantiators; these are needed because
  // the JVM erases our type parameters (RBQ) at runtime
  // and thus 'new RBQ' does not suffice in the DSL call above
//  protected def CBQFactory() : CBQ
//  protected def CBDQFactory() : CBDQ
//  protected def FTQFactory() : FTQ
//  protected def FTDQFactory() : FTDQ
  protected def RBQFactory() : RBQ
//  protected def RBDQFactory() : RBDQ

}