package org.github.aalbul.jenkins

import akka.actor.ActorSystem
import org.github.aalbul.jenkins.http.{HttpClientConf, Auth, HttpClient}
import org.github.aalbul.jenkins.json.JsonSupport

/**
 * Created by nuru on 3/30/14.
 *
 * Main entry point to communicate with Jenkins.
 * Extensible. Additional functionality can be added with the help of mix-ins
 */
class JenkinsClient(protected val system: ActorSystem, protected val config: ClientConfig) extends JsonSupport {
  protected val http = new HttpClient(system, system.dispatcher, HttpClientConf(config.auth))
}

/**
 * Jenkins client configuration
 * @param host - jenkins host. Can be IP or domain name
 * @param port - port number (optional)
 * @param auth - authentication details (optional)
 */
case class ClientConfig(host: String, port: Option[Int], auth: Option[Auth] = None) {
  def url = s"http://$host${port.fold("")(port => s":$port")}"
}