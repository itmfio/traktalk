package net.floaterio.traktalk

import common.ConfigSupport
import scala.swing._
import event.{WindowOpened, WindowClosing, ActionEvent}
import javax.swing.UIManager
import org.apache.commons.logging.LogFactory
import twitter.{OAuthDialogController, OAuthDialog}
import net.sf.nimrod.{NimRODTheme, NimRODLookAndFeel}
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

object Traktalk extends SimpleSwingApplication {

  val log = LogFactory.getLog(getClass)

  lazy val config = new ConfigSupport
  lazy val topController = new TopFrameController(config)

  def top = {
    val f = topController.frame
    f.centerOnScreen()
    f
  }

  override def startup(args: Array[String]) {

    log.info("startup application")

    val nt = new NimRODTheme( "Snow.theme")
    val nf = new NimRODLookAndFeel()
    NimRODLookAndFeel.setCurrentTheme(nt)
    UIManager.setLookAndFeel(nf)

    super.startup(args)
  }

}
