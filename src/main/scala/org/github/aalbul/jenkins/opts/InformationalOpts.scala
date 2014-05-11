package org.github.aalbul.jenkins.opts

import org.github.aalbul.jenkins.JenkinsClient
import spray.client.pipelining._
import org.github.aalbul.jenkins.domain._
import org.github.aalbul.jenkins.domain.JobList
import org.github.aalbul.jenkins.domain.BuildInfo
import org.github.aalbul.jenkins.domain.UserList
import org.github.aalbul.jenkins.domain.Job
import scala.concurrent.Future

/**
 * Created by nuru on 3/30/14.
 *
 * Mix - in that enables informational commands on Jenkins Client.
 * You can use it to get server / job statuses e.t.c.
 */
trait InformationalOpts { this: JenkinsClient =>
  private implicit val ec = system.dispatcher

  /**
   * Retrieve job list
   */
  def jobList =
    http(Get(s"${config.url}/api/json/")).map(resp => mapper.readValue[JobList](resp.entity.asString))

  /**
   * Retrieve job information according to specified job name
   * @param jobName - name of requested job
   */
  def jobInfo(jobName: String) =
    http(Get(s"${config.url}/job/$jobName/api/json")).map(resp => mapper.readValue[Job](resp.entity.asString))


  /**
   * Retrieve build info
   * @param jobName - job name
   * @param buildId - requested job id
   */
  def buildInfo(jobName: String, buildId: Int) =
    http(Get(s"${config.url}/job/$jobName/$buildId/api/json")).flatMap { resp =>
      if (resp.status.isSuccess) { Future.successful(mapper.readValue[BuildInfo](resp.entity.asString)) }
      else Future.failed(new IllegalArgumentException("Build not found"))
    }

  /**
   * Retrieve user list
   */
  def userList =
    http(Get(s"${config.url}/asynchPeople/api/json")).map(resp => mapper.readValue[UserList](resp.entity.asString))

  /**
   * Retrieve user info
   * @param userName - name of requested user
   */
  def userInfo(userName: String) =
    http(Get(s"${config.url}/user/$userName/api/json")).map(resp => mapper.readValue[UserInfo](resp.entity.asString))

  /**
   * Retrieve main view summary.
   * This is an expensive operation and must be used carefully
   */
  def summary =
    http(Get(s"${config.url}/api/json/?depth=2")).map(resp => mapper.readValue[Summary](resp.entity.asString))
}
