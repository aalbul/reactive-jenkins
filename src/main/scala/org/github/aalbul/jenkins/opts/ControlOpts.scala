package org.github.aalbul.jenkins.opts

import org.github.aalbul.jenkins.JenkinsClient
import akka.actor.{ActorRef, Actor}
import org.github.aalbul.jenkins.opts.ControlOpts._
import spray.client.pipelining._
import spray.http.{StatusCodes, HttpResponse}
import org.github.aalbul.jenkins.opts.ControlOpts.PerformABuild
import org.github.aalbul.jenkins.opts.ControlOpts.AbortBuild

/**
 * Created by nuru on 4/3/14.
 *
 * Mix - in that enables ability to control Jenkins jobs
 */
object ControlOpts {
  case class PerformABuild(jobName: String)
  case class AbortBuild(jobName: String, buildNumber: Int)

  case class UnableToPerformABuild(jobName: String, reason: String)

  case class BuildJobRequested(jobName: String)
  case class AbortJobRequested(jobName: String, buildNumber: Int)
}

trait ControlOpts extends Actor { this: JenkinsClient =>
  private implicit val ec = context.dispatcher

  abstract override def receive = behaviour orElse super.receive

  private val behaviour: Actor.Receive = {
    case msg: PerformABuild => build(msg, sender())
    case msg: AbortBuild => abort(msg, sender())
  }

  /**
   * Start building specified job
   * @param msg - message that contains job information
   * @param sender - sender to reply to
   */
  private def build(msg: PerformABuild, sender: ActorRef) {
    val future = http(Post(s"${config.url}/job/${msg.jobName}/build/"))
    withErrorHandler(future, msg, sender)
    future.onSuccess{ case _ => sender.tell(BuildJobRequested(msg.jobName), self) }
  }

  /**
   * Abort specified job
   * @param msg - message that contains job information
   * @param sender - sender to reply to
   */
  private def abort(msg: AbortBuild, sender: ActorRef) {
    val future = http(Post(s"${config.url}/job/${msg.jobName}/${msg.buildNumber}/stop/"))
    withErrorHandler(future, msg, sender)
    future.onSuccess { case _ => sender.tell(AbortJobRequested(msg.jobName, msg.buildNumber), self) }
  }
}
