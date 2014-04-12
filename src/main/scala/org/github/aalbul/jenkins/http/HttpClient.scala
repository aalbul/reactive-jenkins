package org.github.aalbul.jenkins.http

import scala.concurrent.{ExecutionContext, Future}
import spray.client.pipelining._
import spray.http.BasicHttpCredentials
import akka.actor.ActorSystem
import spray.http.HttpRequest
import spray.http.HttpResponse

/**
 * Created by nuru on 3/31/14.
 *
 * Http client configurator class
 */
class HttpClient(actorSystem: ActorSystem, ec: ExecutionContext, conf: HttpClientConf = HttpClientConf())
  extends (HttpRequest => Future[HttpResponse]) {

  private val pipeline: HttpRequest => Future[HttpResponse] = {
    val sr = sendReceive(actorSystem, ec)
    conf.auth.map {
      case UserPasswordAuth(name, pass) => addCredentials(BasicHttpCredentials(name, pass)) ~> sr
    }.getOrElse(sr)
  }

  override def apply(request: HttpRequest): Future[HttpResponse] = pipeline(request)
}

case class HttpClientConf(auth: Option[Auth] = None)