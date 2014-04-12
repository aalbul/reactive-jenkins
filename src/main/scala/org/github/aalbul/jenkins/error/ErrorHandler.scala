package org.github.aalbul.jenkins.error

import scala.concurrent.Future
import spray.http.HttpResponse
import akka.actor.{Actor, ActorRef}
import spray.http.StatusCodes._
import org.github.aalbul.jenkins.domain.Errors.{NotEnoughPermissions, ResourceNotFound}

/**
 * Created by nuru on 4/3/14.
 *
 * Error handler mix-in to react on common errors in a uniform way
 */
trait ErrorHandler { this: Actor =>

  def withErrorHandler(future: Future[HttpResponse], sourceMessage: Any, sender: ActorRef) = {
    future.onSuccess {
      case HttpResponse(NotFound, _, _, _) => sender.tell(ResourceNotFound(sourceMessage), self)
      case HttpResponse(Forbidden, _, _, _) => sender.tell(NotEnoughPermissions(sourceMessage), self)
      case _@a => println(a)
    } (context.system.dispatcher)
  }
}
