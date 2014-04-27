package org.github.aalbul.jenkins.opts

import org.github.aalbul.jenkins.JenkinsClient
import akka.actor.Props
import org.github.aalbul.jenkins.opts.state.StateMonitorManagerActor
import scala.concurrent.duration._

/**
* Created by nuru on 4/21/14.
*/
trait StateChangeNotifierOpts { this: JenkinsClient with InformationalOpts =>
  private implicit val ec = system.dispatcher

  val manager = system.actorOf(Props(new StateMonitorManagerActor(this)))
  val timer = system.scheduler.schedule(0.second, 10.seconds, new Runnable {
    override def run(): Unit = summary.onSuccess { case summary => manager ! summary }
  })
}
