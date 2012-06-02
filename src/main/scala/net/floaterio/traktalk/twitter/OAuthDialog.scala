package net.floaterio.traktalk.twitter

import swing._
import event.{ValueChanged, ButtonClicked}
import twitter4j.auth.RequestToken
import org.apache.commons.logging.LogFactory
import java.net.URL
import net.floaterio.traktalk.gui.SwingSupport._
import net.floaterio.traktalk.gui.{LabelWidth, Gap}
import swing.BorderPanel.Position
import java.awt.{Toolkit, Desktop}
import java.awt.datatransfer.StringSelection
import twitter4j.{Twitter, TwitterFactory}

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/01
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */

class OAuthDialog(controller: OAuthDialogController, owner: Window) extends Dialog(owner) {

  modal = true
  setLocationRelativeTo(owner)

  val urlText = new TextArea(3, 30) {
    lineWrap = true
  }

  val loginButton: Button = new Button("Login") {
    enabled = false
    reactions += {
      case ButtonClicked(source) => {
        controller.login
      }
    }
  }
  defaultButton = loginButton

  val pinText = new TextField() {
    reactions += {
      case ValueChanged(source) => {
        loginButton.enabled = this.text.length != 0
      }
    }
  }

  contents = {
    new BoxPanel(Orientation.Vertical) {
      border = defaultBorder
      implicit val g = Gap(5)
      implicit val width = LabelWidth(120)
      contents += gap(g.x)
      contents addWithGap labeled("Autholization", new BorderPanel() {
        add(urlText, Position.Center)
        add(new BoxPanel(Orientation.Vertical) {
          contents addWithGap simpleButton("Open Browser") {
            controller.openBrowser()
          }
          contents addWithGap simpleButton("Copy") {
            controller.copy()
          }
        }, Position.East)
      })
      contents addWithGap labeled("Pin Code", pinText)
      contents addWithGap new FlowPanel(FlowPanel.Alignment.Right)() {
        border = defaultBorder
        contents += loginButton
      }

    }
  }
}

class OAuthDialogController(oAuthSupport: OAuthSupport, owner: Window, twitter: Twitter) {

  import oAuthSupport._

  val log = LogFactory.getLog(getClass)

  lazy val dialog = new OAuthDialog(this, owner)

  import dialog._

  val requestToken: RequestToken = {
    val r = twitter.getOAuthRequestToken()
    dialog.urlText.text = r.getAuthorizationURL
    r
  }

  def openDialog() = {
    dialog.setLocationRelativeTo(owner)
    dialog.open()
  }

  def login = {
    val t = twitter.getOAuthAccessToken(requestToken, dialog.pinText)
    saveAccessToken(t.getToken)
    saveAccessSecret(t.getTokenSecret)
    // twitter.setOAuthAccessToken(t)

    dialog.dispose()
  }

  def openBrowser() = {
    Desktop.getDesktop.browse(new URL(urlText).toURI)
  }

  def copy() = {
    val kit = Toolkit.getDefaultToolkit()
    val clip = kit.getSystemClipboard()
    val ss = new StringSelection(urlText)
    clip.setContents(ss, ss)
  }
}
