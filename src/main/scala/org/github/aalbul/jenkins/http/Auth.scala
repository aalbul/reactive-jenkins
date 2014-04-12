package org.github.aalbul.jenkins.http

/**
 * Created by nuru on 4/3/14.
 *
 * Authentication support
 */
sealed abstract class Auth

/**
 * Authentication with the help of user/password credentials
 * @param user - user name
 * @param password - password
 */
case class UserPasswordAuth(user: String, password: String) extends Auth
