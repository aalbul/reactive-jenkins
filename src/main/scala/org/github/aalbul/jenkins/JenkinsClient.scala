package org.github.aalbul.jenkins

import akka.actor.{ActorSystem, Actor}
import org.github.aalbul.jenkins.http.{HttpClientConf, Auth, HttpClient}
import org.github.aalbul.jenkins.json.JsonSupport
import scala.concurrent.ExecutionContext
import org.github.aalbul.jenkins.error.ErrorHandler

/**
 * Created by nuru on 3/30/14.
 *
 * Main entry point to communicate with Jenkins.
 * Extensible. Additional functionality can be added with the help of mix-ins
 */
class JenkinsClient(val config: ClientConfig) extends Actor with JsonSupport with ErrorHandler {
  protected val http = new HttpClient(context.system, context.dispatcher, HttpClientConf(config.auth))

  override def receive = {
    case _ =>
  }
}

/**
 * Jenkins client configuration
 * @param host - jenkins host. Can be IP or domain name
 * @param port - port number (optional)
 * @param auth - authentication details (optional)
 */
case class ClientConfig(host: String, port: Option[Int], auth: Option[Auth] = None) {
  def url = s"http://$host${port.map(port => s":$port").getOrElse("")}"
}