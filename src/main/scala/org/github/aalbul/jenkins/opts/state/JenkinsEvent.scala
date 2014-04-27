package org.github.aalbul.jenkins.opts.state

/**
 * Created by nuru on 4/27/14.
 */
case class JenkinsEvent(event: Any)
case class BuildStateChange(job: String, state: JobState, buildNumber: Int)

sealed trait JobEvent
case class JobAdded(name: String) extends JobEvent
case class JobDeleted(name: String) extends JobEvent