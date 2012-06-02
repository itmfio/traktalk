package net.floaterio.traktalk

import swing._
import event._
import gui.SwingSupport._
import scala.swing.MainFrame
import swing.BorderPanel.Position
import swing.ScrollPane.BarPolicy


/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/05/26
 * Time: 2:08
 * To change this template use File | Settings | File Templates.
 */

class TopFrame(controller: TopFrameController) extends MainFrame {

  title = "Traktalk"
  val width = 400

  val tweetText: TextArea = new TextArea() {
    lineWrap = true
    wordWrap = true
    preferredSize = d(width, 150)
    reactions += {
      case ValueChanged(s) => {
        val l = text.length
        countLabel.text = (140 - l).toString
        tweetBtn.enabled = l > 0 && l <= 140
      }
      case e: KeyPressed => {
//        if (e.key == Key.Enter) {
//          controller.tweet()
//        }
      }
    }
  }

  val statusText = new TextArea() {
    lineWrap = true
    wordWrap = true
//    editable = false
    preferredSize = d(width, 200)
  }

  val infoText = new TextField() {
    editable = false
  }

  val serverBtn:Button = new Button("Start Server") {
    var start = false
    reactions += {
      case ButtonClicked(source) => {
        start = !start
        if (start) {
          text = "Stop Server"
          controller.startServer()
        } else {
          text = "Start Server"
          controller.stopServer()
        }
      }
    }
  }

  val login = simpleButton("Login") {
    controller.loginOrLogout()
  }

  val tweetBtn = simpleButton("Tweet") {
    controller.tweet()
  }

  defaultButton = tweetBtn

  val countLabel = new Label("140")

  val buttonPanel = new BorderPanel() {
    add(new FlowPanel(FlowPanel.Alignment.Left)() {
      contents += serverBtn
      contents += login
      contents += simpleButton("Setting...") {
        controller.editTemplate()
      }
    }, Position.West)
    add(new FlowPanel(FlowPanel.Alignment.Right)() {
      contents += countLabel
      contents += tweetBtn
    }, Position.East)
  }

  contents = new BorderPanel() {
    add(buttonPanel, Position.North)
    add(new SplitPane(Orientation.Horizontal){
      topComponent = tweetText
      val scroll = new ScrollPane(statusText) {
//        verticalScrollBarPolicy = BarPolicy.Always
        peer.setAutoscrolls(true)
      }
      bottomComponent = scroll

    }, Position.Center)

//    add(infoText, Position.South)

  }
}
