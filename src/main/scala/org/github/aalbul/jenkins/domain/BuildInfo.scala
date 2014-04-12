package org.github.aalbul.jenkins.domain

import org.joda.time.{DateTime, Duration}
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

/**
 * Created by nuru on 4/1/14.
 */
case class BuildInfo(id: String, artifacts: List[Artifact], building: Boolean, keepLog: Boolean, description: String,
                     fullDisplayName: String, duration: Duration, estimatedDuration: Duration, timestamp: DateTime,
                     url: String, builtOn: String, changeSet: ChangeSet, culprits: List[User],
                     @JsonScalaEnumeration(classOf[BuildStatusType]) result: BuildStatus.BuildStatus)

class BuildStatusType extends TypeReference[BuildStatus.type]
object BuildStatus extends Enumeration {
  type BuildStatus = Value
  val Success = Value("SUCCESS")
  val Unstable = Value("UNSTABLE")
  val Failure = Value("FAILURE")
  val NotBuilt = Value("NOT_BUILT")
  val Aborted = Value("ABORTED")
}