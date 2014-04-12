package org.github.aalbul.jenkins.domain

/**
 * Created by nuru on 4/3/14.
 */
object Errors {
  sealed abstract class ErrorMessage
  case class ResourceNotFound(reactionOn: Any) extends ErrorMessage
  case class NotEnoughPermissions(reactionOn: Any) extends ErrorMessage
}
