package net.floaterio.traktalk.gui

import java.awt.Dimension
import swing.BorderPanel.Position
import swing.event.ButtonClicked
import collection.mutable.Buffer
import swing._
import javax.swing.border.EmptyBorder

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/05/26
 * Time: 2:14
 * To change this template use File | Settings | File Templates.
 */

object SwingSupport {

  val defaultGap = 5

  def d(x: Int, y: Int) = new Dimension(x, y)

  def gap(x: Int) = Swing.RigidBox(d(x, x))

  implicit def textComponentToText(c: TextComponent) = c.text

  def simpleButton(name: String)(func: => Unit) = {
    new Button(name) {
      reactions += {
        case ButtonClicked(s) => {
          func
        }
      }
    }
  }

  implicit def toBuilder(contents: Buffer[Component]) = new ComponentBuilder(contents)

  def defaultBorder = new EmptyBorder(defaultGap, defaultGap, defaultGap, defaultGap)

  def labeled(label: String, component: Component)(implicit w: LabelWidth) = {
    val l = new Label(label) {
      xAlignment = Alignment.Right
      border = defaultBorder
    }
    val dim = l.preferredSize
    l.preferredSize = d(w.x, dim.height)

    new BorderPanel {
      add(l, Position.West)
      add(new BorderPanel(){
        border = defaultBorder
        add(component, Position.Center)
      }, Position.Center)
    }
  }
}

case class Gap(x: Int)
case class LabelWidth(x: Int)

