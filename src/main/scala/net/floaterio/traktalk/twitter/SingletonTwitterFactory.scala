package net.floaterio.traktalk.twitter

import twitter4j.TwitterFactory

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/02
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */

object SingletonTwitterFactory {

  val instance = new TwitterFactory()

  def newTwitter() = {
    instance.getInstance()
  }

  def newTwitter(consumerKey: String, consumerSecret: String) = {
    val t = instance.getInstance()
    t.setOAuthConsumer(consumerKey, consumerSecret)
    t
  }

  def newTwitterWithConfig(config: OAuthSupport) = {
    newTwitter(config.consumerKey, config.consumerSecret)
  }

}
