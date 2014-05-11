package org.github.aalbul.jenkins.opts

import org.github.aalbul.jenkins.JenkinsClient
import spray.client.pipelining._
import org.github.aalbul.jenkins.util.ConcurrencyUtils._
import org.github.aalbul.jenkins.domain.BuildInfo
import akka.actor.ActorSystem
import scala.concurrent.duration._

/**
 * Created by nuru on 4/3/14.
 *
 * Mix - in that enables ability to control Jenkins jobs
 */
trait ControlOpts { this: JenkinsClient with InformationalOpts =>
  private implicit val ec = system.dispatcher

  /**
   * Start building specified job
   * @param jobName - name of job to build
   * @param maxWaitTime - how long should we wait for the result.
   * @param pollEvery - how often to check the status of job
   */
  def build(jobName: String, maxWaitTime: FiniteDuration, pollEvery: FiniteDuration)(implicit system: ActorSystem) = {
    http(Post(s"${config.url}/job/$jobName/build/"))
      .flatMap { _ =>
        jobInfo(jobName).flatMap { info =>
          val buildNumber = if (info.inQueue) info.nextBuildNumber.get else info.lastBuild.get.number
          waitForCompletion(jobName, buildNumber, maxWaitTime, pollEvery)
        }
      }
  }

  /**
   * Abort specified job
   * @param jobName - name of job
   * @param buildNumber - build number
   * @param maxWaitTime - how long should we wait for the result.
   * @param pollEvery - how often to check the status of job
   */
  def abort(jobName: String, buildNumber: Int, maxWaitTime: FiniteDuration, pollEvery: FiniteDuration)
           (implicit system: ActorSystem) = {
    http(Post(s"${config.url}/job/$jobName/$buildNumber/stop/")).flatMap { _ =>
        waitForCompletion(jobName, buildNumber, maxWaitTime, pollEvery)
    }
  }

  /**
   * Wait for job completion.
   * The future will return when build process will be stopped (Built, Aborted e.t.c.)
   * @param jobName - job name
   * @param buildNumber - build number
   * @param maxWaitTime - how long should we wait for the result.
   * @param pollEvery - how often to check the status of job
   * @return job status future
   */
  def waitForCompletion(jobName: String,
                        buildNumber: Int,
                        maxWaitTime: FiniteDuration = 10.minutes,
                        pollEvery: FiniteDuration = 10.seconds)(implicit system: ActorSystem) = {
    require(maxWaitTime > pollEvery, "Max wait tim cannot be less then poll interval")
    val pollCount = maxWaitTime / pollEvery

    asyncPollFor[BuildInfo](pollEvery, pollCount.toInt) {
      buildInfo(jobName, buildNumber).map { info => if (!info.building) Some(info) else None }
    }
  }
}