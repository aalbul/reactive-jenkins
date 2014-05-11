package org.github.aalbul.jenkins.util

import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Created by nuru on 5/9/14.
 */
object ConcurrencyUtils {

  def pollFor[R](timeout: FiniteDuration, retries: Int)(action: => Option[R])(implicit system: ActorSystem): Future[R] = {
    asyncPollFor[R](timeout, retries)(Future.successful[Option[R]](action))
  }

  /**
   * Create a postponed result link (future) and poll for result in background in non-blocking way
   * @param timeout - timeout before ticks
   * @param retries - number of poll retry
   * @param action - poll predicate. This method will call this action until it return Some result or amount of retries exceeded
   * @param system - actor system instance
   * @tparam R - resulting value type
   * @return result future
   */
  def asyncPollFor[R](timeout: FiniteDuration, retries: Int)(action: => Future[Option[R]])(implicit system: ActorSystem): Future[R] = {
    implicit val ec = system.dispatcher
    val callback = promise[R]()
    val config = PollerConfig[R](timeout, retries, system, () => action, 0, callback)
    system.scheduler.scheduleOnce(0.seconds, new PollerRunnable[R](config))
    callback.future
  }

  case class PollerConfig[R](timeout: FiniteDuration,
                             retries: Int,
                             system: ActorSystem,
                             action: () => Future[Option[R]],
                             currentStep: Int = 0,
                             callback: Promise[R])

  /**
   * Runnable that is responsible for recurring status validation.
   * In case when result is produced or maximum amount of tries reached, it will modify promise to signal future
   * @param config - poller configuration instance that contains all the necessary information
   * @tparam R - result type
   */
  class PollerRunnable[R](config: PollerConfig[R])(implicit ec: ExecutionContext) extends Runnable {
    override def run(): Unit = {
      config.action().onComplete {
        case Success(result) =>
          if (result.isEmpty) handleNoResult(new TimeoutException)
          else config.callback.success(result.get)
        case Failure(ex) =>
          handleNoResult(ex)
      }
    }

    /**
     * Handle the case when no result produced.
     * This method check for the maximum retries count and if it is not reached, schedule another try
     */
    private def handleNoResult(exception: Throwable) {
      if (config.currentStep == config.retries) {
        config.callback.failure(exception)
      } else {
        config.system.scheduler.scheduleOnce(
          config.timeout, new PollerRunnable[R](config.copy(retries = config.retries + 1))
        )(config.system.dispatcher)
      }
    }
  }
}
