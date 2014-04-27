package org.github.aalbul.jenkins.opts.state

import akka.actor.{Props, Actor, ActorRef}
import org.github.aalbul.jenkins.domain.Summary
import org.github.aalbul.jenkins.opts.InformationalOpts

/**
* Created by nuru on 4/21/14.
*/
class StateMonitorManagerActor(client: InformationalOpts) extends Actor {
  override def receive = behaviour(Map())

  def behaviour(jobs: Map[String, ActorRef]): Receive = {
    case s: Summary =>
      val state = handleJobDiff(jobs, s)
      broadcastSummary(s, state)
  }

  /**
   * Find collisions in current state and summary and publish an appropriate events into event buss
   * @param jobs - job list
   * @param s - received summary
   */
  private def handleJobDiff(jobs: Map[String, ActorRef], s: Summary) = {
    val newJobs = s.jobs.filter(job => !jobs.contains(job.name))
    newJobs.foreach(job => context.system.eventStream.publish(JenkinsEvent(JobAdded(job.name))))

    val deleted = jobs.filter { case (key, _) => !s.jobs.exists(_.name == key) }
    deleted.foreach { case (name, actor) =>
      context.stop(actor)
      context.system.eventStream.publish(JenkinsEvent(JobDeleted(name)))
    }

    if (newJobs.size != 0 || deleted.size != 0) {
      val newState = jobs ++ newJobs.map(job => job.name -> context.actorOf(Props(new StateMonitorActor(client)))) -- deleted.keys
      context.become(behaviour(newState))
      newState
    } else jobs
  }

  /**
   * Broadcast the content of summary to monitor actors
   * @param summary - summary instance
   * @param jobs - map of job-name -> monitor ref
   */
  private def broadcastSummary(summary: Summary, jobs: Map[String, ActorRef]) {
    summary.jobs.foreach { job =>
      jobs(job.name) ! job
    }
  }
}