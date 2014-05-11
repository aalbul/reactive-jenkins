package org.github.aalbul.jenkins.opts.state

/**
 * Created by nuru on 4/27/14.
 *
 * List of available build states
 */
sealed trait JobState
case object NotBuilt extends JobState
case object Failed extends JobState
case object Successful extends JobState
case object Running extends JobState
case object Aborted extends JobState