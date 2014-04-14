package org.github.aalbul.jenkins.domain

/**
 * Created by nuru on 4/14/14.
 *
 * Main screen perpesentation
 * May be veeery large (depends on depth parameter)
 */
case class Summary(mode: String, nodeDescription: String, nodeName: String, numExecutors: Int, description: Option[String],
                   jobs: List[Job], primaryView: View, quietingDown: Boolean, slaveAgentPort: Int, useCrumbs: Boolean,
                   useSecurity: Boolean, views: List[View])