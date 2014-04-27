package org.github.aalbul.jenkins.domain

/**
 * Created by nuru on 4/27/14.
 */
object BuildStatus extends Enumeration {
  type BuildStatus = Value
  val Success = Value("SUCCESS")
  val Unstable = Value("UNSTABLE")
  val Failure = Value("FAILURE")
  val NotBuilt = Value("NOT_BUILT")
  val Aborted = Value("ABORTED")
}
