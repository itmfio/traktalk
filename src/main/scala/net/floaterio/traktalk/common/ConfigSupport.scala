package net.floaterio.traktalk.common

import net.floaterio.traktalk.twitter.OAuthSupport
import java.io.File
import org.apache.commons.logging.LogFactory
import org.apache.commons.configuration.{Configuration, PropertiesConfiguration}

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/01
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */

class ConfigSupport {

  val log = LogFactory.getLog(getClass)

  val path = {
    val home = System.getProperty("user.home")
    val dir = new File(home + File.separator + ".traktalk")
    if (!dir.exists()) {
      log.info("create user dir %s".format(dir.getPath))
      dir.mkdir()
    }

    dir.getPath + File.separator + "conf.properties"
  }

  lazy val root = new PropertiesConfiguration(new File(path))
  lazy val general = new GeneralConfiguration(root.subset("general"))
  lazy val oAuth = new OAuthSupport(root.subset("oAuth"))

  def save = root.save()

}

class GeneralConfiguration(config: Configuration) {

  private val tp = "template"
  def template = config.getString(tp, """Playing on UST! "%TITLE%" (%ARTIST%)""")
  def saveTemplate(template: String) = config.setProperty(tp, template)

  private val pt = "port"
  def port = config.getString(pt, "9000").toInt
  def savePort(port: Int) = config.setProperty(pt, port.toString)

}

class WindowConfiguration {

}
