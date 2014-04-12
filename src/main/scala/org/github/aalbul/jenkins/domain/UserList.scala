package org.github.aalbul.jenkins.domain

import org.joda.time.DateTime

/**
 * Created by nuru on 4/1/14.
 */
case class UserList(users: List[UserListItem])
case class UserListItem(lastChange: Option[DateTime], project: Option[Job], user: User)
