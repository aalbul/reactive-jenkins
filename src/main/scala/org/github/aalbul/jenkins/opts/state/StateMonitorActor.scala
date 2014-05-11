package org.github.aalbul.jenkins.opts.state

import akka.actor.Actor
import org.github.aalbul.jenkins.domain.BuildStatus
import org.github.aalbul.jenkins.opts.InformationalOpts
import org.github.aalbul.jenkins.domain.BuildStatus.BuildStatus
import org.github.aalbul.jenkins.opts.state.StateMonitorActor.BuildState
import scala.Some
import org.github.aalbul.jenkins.domain.Job

/**
 * Created by nuru on 4/27/14.
 *
 * Actor that monitors jenkins job state
 * In case if state have been changes, this actor publishes an appropriate event to the message buss
 */
object StateMonitorActor {
  case class BuildState(job: String, buildNumber: Int, state: JobState)
}

class StateMonitorActor(client: InformationalOpts) extends Actor {
  implicit val ec = context.system.dispatcher

  override def receive = behaviour(None)

  def behaviour(state: Option[BuildState]): Receive = {
    case job: Job => retrieveState(job)
    case newState: BuildState => transitState(state, newState)
  }

  /**
   * Transits current state to new received
   * The state transition will be done only if new state is different from what we have right now.
   * Notification will be done for any state change. The only exceptions is first state change. (We do not want to be informed
   * the first time because we do not know what state was previously and this means that we cannot state chat state
   * have changed)
   * @param currentState - current state
   * @param newState - new state
   */
  private def transitState(currentState: Option[BuildState], newState: BuildState) {
    val shouldUpdateState = currentState != Some(newState)
    val shouldSendNotification = currentState.isDefined && shouldUpdateState
    if (shouldUpdateState) context.become(behaviour(Some(newState)))
    if (shouldSendNotification)
      context.system.eventStream.publish(JenkinsEvent(BuildStateChange(newState.job, newState.state, newState.buildNumber)))
  }

  /**
   * Retrieves state for job. When build info retrieved trying to determine proper state
   * @param job - job instance
   */
  private def retrieveState(job: Job) {
    case class BuildInfoSummary(number: Int, building: Boolean, status: Option[BuildStatus])

    job
      .builds
      .headOption
      .foreach { build => client.buildInfo(job.name, build.number).onSuccess { case info =>
        val state = extractState(BuildInfoSummary(build.number, info.building, info.result))
        self ! BuildState(job.name, build.number, state)
      }
    }

    def extractState(summary: BuildInfoSummary) = summary match {
      case summary@BuildInfoSummary(_, true, None) => Running
      case summary@BuildInfoSummary(_, false, None) => NotBuilt
      case summary@BuildInfoSummary(_, _, Some(BuildStatus.Failure | BuildStatus.Unstable)) => Failed
      case summary@BuildInfoSummary(_, _, Some(BuildStatus.Success)) => Successful
      case summary@BuildInfoSummary(_, _, Some(BuildStatus.Aborted)) => Aborted
    }
  }
}
