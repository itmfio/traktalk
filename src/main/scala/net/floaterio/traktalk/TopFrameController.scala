package net.floaterio.traktalk

import common.{AddSwingListener, GlobalEventHandler, ConfigSupport}
import edit.EditDialogController
import javax.swing.SwingUtilities
import server.{SongNameInfo, SongNameServer}
import org.apache.commons.logging.LogFactory
import java.util.Date
import java.text.SimpleDateFormat
import org.apache.commons.lang.StringUtils
import swing.event.{WindowClosing, WindowOpened}
import twitter.{SingletonTwitterFactory, OAuthDialogController}
import twitter4j.auth.AccessToken
import twitter4j.{Twitter, TwitterException, TwitterFactory}

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/05/26
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */

class TopFrameController(config: ConfigSupport) {

  val log = LogFactory.getLog(getClass)

  lazy val server = new SongNameServer(config.general.port)

  var twitter:Twitter = newTwitter
  var authorized = false

  def newTwitter = SingletonTwitterFactory.newTwitterWithConfig(config.oAuth)

  lazy val frame: TopFrame = new TopFrame(this)

  import frame._

  frame.reactions += {

    case e: WindowOpened => {
      login()
    }
    case e: WindowClosing => {
      log.info("save configuration")
      config.save
    }
  }

  GlobalEventHandler ! AddSwingListener(text => {
    addStatus(text)
  })

  server += (info => {
    invokeLater {
      tweetText.text = formatByTemplate(config.general.template, info)
      addStatus("%s - %s".format(info.title, info.artist))
    }
  })

  // Must be accessed by single thread
  val dateFormat = new SimpleDateFormat("HH:mm:ss")

  def addStatus(text: String) = {
    val dateStr = dateFormat.format(new Date())
    val newText = "%s %s\n".format(dateStr, text) + statusText.text
    statusText.text = newText
    val p = statusText.peer
    p.setCaretPosition(p.getDocument.getLength)
  }

  def refreshTitle() = {
    safeWithTwitter("Login Error"){
      addStatus("login as %s".format(twitter.getScreenName))
    }
  }

  def tweet() = {
    tweetText.editable = false
    safeWithTwitter("Tweet Error") {
      twitter.updateStatus(tweetText.text)
      tweetText.text = ""
    }
    tweetText.editable = true
  }

  def startServer() =  {
    server.runServer
  }

  def stopServer() = {
    server.stop
  }

  def editTemplate() = {
    val controller = new EditDialogController(config.general, frame)
    controller.openDialog()
  }

  def invokeLater(f: => Unit) = {
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        f
      }
    })
  }

  def safeWithTwitter(errorMsg: String)(f: => Unit) = {
    try {
      f
      true
    } catch {
      case e:Exception => {
        addStatus(errorMsg)
        false
      }
    }
  }

  def formatByTemplate(text:String, info: SongNameInfo) = {
    val s1 = StringUtils.replace(text, "%ARTIST%", info.artist)
    StringUtils.replace(s1, "%TITLE%", info.title)
  }

  def loginOrLogout() {
    if (authorized) {
      logout()
    } else {
      login()
    }
  }

  def logout() = {
    twitter = newTwitter
    config.oAuth.saveAccessToken("")
    config.oAuth.saveAccessSecret("")
    authorized = false
    addStatus("Logout")
    resetButtonTitle
  }

  def login() = {
    val at = config.oAuth.accessToken
    val as = config.oAuth.accessSecret
    if (StringUtils.isNotBlank(at) && StringUtils.isNotBlank(as)) {
      twitter.setOAuthAccessToken(new AccessToken(at, as))
    } else {
      log.info("login as new account...")
      new OAuthDialogController(config.oAuth, frame, twitter).openDialog()
    }
    if (refreshTitle()) {
      authorized = true
    } else {
      logout()
    }
    resetButtonTitle
  }

  def resetButtonTitle = {
    log.info("reset button title")
    if (authorized) {
      frame.login.text = "Logout"
    } else {
      frame.login.text = "Login"
    }
  }

}
