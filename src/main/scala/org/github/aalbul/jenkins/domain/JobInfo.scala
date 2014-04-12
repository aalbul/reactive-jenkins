package org.github.aalbul.jenkins.domain

/**
 * Created by nuru on 3/31/14.
 */
case class JobInfo(description: String, name: String, displayName: String, url: String, buildable: Boolean, inQueue: Boolean,
                   keepDependencies: Boolean, builds: List[Build], firstBuild: Option[Build], lastBuild: Option[Build],
                   lastCompletedBuild: Option[Build], lastFailedBuild: Option[Build], lastStableBuild: Option[Build],
                   lastSuccessfulBuild: Option[Build], lastUnstableBuild: Option[Build], lastUnsuccessfulBuild: Option[Build],
                   nextBuildNumber: Option[Int], queueItem: QueueItem, upstreamProjects: List[Job], downstreamProjects: List[Job],
                   healthReport: List[JobHealth])