package org.github.aalbul.jenkins.domain

import org.joda.time.DateTime
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by nuru on 3/31/14.
 */
case class QueueItem(id: Long, blocked: Boolean, buildable: Boolean, stuck: Boolean, inQueueSince: DateTime, params: String,
                     task: Job, url: String, why: String, @JsonProperty("buildableStartMilliseconds") buildableStart: DateTime)
