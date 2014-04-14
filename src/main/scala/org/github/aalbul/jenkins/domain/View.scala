package org.github.aalbul.jenkins.domain

/**
 * Created by nuru on 4/14/14.
 */
case class View(name: String, url: String, description: Option[String], jobs: List[JobOverview])
