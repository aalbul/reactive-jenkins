package org.github.aalbul.jenkins.domain

import org.joda.time.DateTime
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

/**
 * Created by nuru on 4/1/14.
 */
case class ChangeSet(items: List[ChangeSetItem], kind: String)

case class ChangeSetItem(id: String, msg: String, affectedPaths: List[String], commitId: String, author: User,
                         timestamp: DateTime, comment: String, paths: List[ChangeSetPath])

class ChangeOperationType extends TypeReference[ChangeOperation.type]
object ChangeOperation extends Enumeration {
  type ChangeOperation = Value
  val Add = Value("add")
  val Edit = Value("edit")
  val Create = Value("create")
  val Delete = Value("delete")
}

case class ChangeSetPath(@JsonScalaEnumeration(classOf[ChangeOperationType]) editType: ChangeOperation.ChangeOperation, file: String)
