package org.github.aalbul.jenkins.opts

import org.github.aalbul.jenkins.JenkinsClient
import spray.client.pipelining._

/**
 * Created by nuru on 4/3/14.
 *
 * Mix - in that enables ability to control Jenkins jobs
 */
trait ControlOpts { this: JenkinsClient =>
  private implicit val ec = system.dispatcher

  /**
   * Start building specified job
   * @param jobName - name of job to build
   */
  def build(jobName: String) = http(Post(s"${config.url}/job/$jobName/build/")).map(_ => true)

  /**
   * Abort specified job
   * @param jobName - name of job
   * @param buildNumber - build number
   */
  def abort(jobName: String, buildNumber: Int) =
    http(Post(s"${config.url}/job/$jobName/$buildNumber/stop/")).map(_ => true)
}
