package net.floaterio.traktalk.twitter

import org.apache.commons.configuration.Configuration
import twitter4j.{TwitterFactory, Twitter}
import twitter4j.auth.RequestToken

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/01
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */

class OAuthSupport(config: Configuration) {

  // Put your application consumer key and secret here
  val consumerKey = "xxxxx"
  val consumerSecret = "xxxxx"

  val at = "accessToken"
  def accessToken: String = config.getString(at)
  def saveAccessToken(token: String) = config.setProperty(at, token)

  val as = "accessSecret"
  def accessSecret: String = config.getString(as)
  def saveAccessSecret(secret: String) = config.setProperty(as, secret)

}
