package org.github.aalbul.jenkins.opts

import akka.actor.{ActorRef, Actor}
import org.github.aalbul.jenkins.JenkinsClient
import spray.client.pipelining._
import org.github.aalbul.jenkins.domain._
import org.github.aalbul.jenkins.domain.JobList
import org.github.aalbul.jenkins.opts.InformationalOpts.GetUserInfo
import org.github.aalbul.jenkins.domain.BuildInfo
import org.github.aalbul.jenkins.domain.UserList
import org.github.aalbul.jenkins.opts.InformationalOpts.GetUserList
import org.github.aalbul.jenkins.opts.InformationalOpts.GetJobInfo
import org.github.aalbul.jenkins.opts.InformationalOpts.GetJobList
import org.github.aalbul.jenkins.opts.InformationalOpts.GetBuildInfo
import org.github.aalbul.jenkins.domain.JobInfo

/**
 * Created by nuru on 3/30/14.
 *
 * Mix - in that enables informational commands on Jenkins Client.
 * You can use it to get server / job statuses e.t.c.
 */
object InformationalOpts {
  case object GetJobList
  case class GetJobInfo(jobName: String)
  case class GetBuildInfo(jobName: String, buildId: Int)
  case object GetUserList
  case class GetUserInfo(userName: String)
}

trait InformationalOpts extends Actor { this: JenkinsClient =>
  private implicit val ec = context.dispatcher

  abstract override def receive = behaviour orElse super.receive

  private val behaviour: Actor.Receive = {
    case GetJobList => jobList(sender())
    case GetJobInfo(jobName) => jobInfo(jobName, sender())
    case GetBuildInfo(jobName, buildId) => buildInfo(jobName, buildId, sender())
    case GetUserList => userList(sender())
    case GetUserInfo(userName) => userInfo(userName, sender())
  }

  /**
   * Retrieve job list and send it to requester
   * @param sender - actor that is waiting for response
   */
  private def jobList(sender: ActorRef) {
    http(Get(s"${config.url}/api/json/")).onSuccess { case response =>
      sender.tell(mapper.readValue[JobList](response.entity.asString), self)
    }
  }

  /**
   * Retrieve job information according to specified job name
   * @param jobName - name of requested job
   * @param sender - requester actor ref
   */
  private def jobInfo(jobName: String, sender: ActorRef) {
    http(Get(s"${config.url}/job/$jobName/api/json")).onSuccess { case response =>
      sender.tell(mapper.readValue[JobInfo](response.entity.asString), self)
    }
  }

  /**
   * Retrieve build info
   * @param jobName - job name
   * @param buildId - requested job id
   * @param sender - requester actor ref
   */
  private def buildInfo(jobName: String, buildId: Int, sender: ActorRef) {
    http(Get(s"${config.url}/job/$jobName/$buildId/api/json")).onSuccess { case response =>
      sender.tell(mapper.readValue[BuildInfo](response.entity.asString), self)
    }
  }

  /**
   * Retrieve user list
   * @param sender - requester actor ref
   */
  private def userList(sender: ActorRef) {
    http(Get(s"${config.url}/asynchPeople/api/json")).onSuccess { case response =>
      sender.tell(mapper.readValue[UserList](response.entity.asString), self)
    }
  }

  /**
   * Retrieve user info
   * @param userName - name of requested user
   * @param sender - requester actor ref
   */
  private def userInfo(userName: String, sender: ActorRef) {
    http(Get(s"${config.url}/user/$userName/api/json")).onSuccess { case response =>
      sender.tell(mapper.readValue[UserInfo](response.entity.asString), self)
    }
  }
}
