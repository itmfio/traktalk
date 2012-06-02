package net.floaterio.traktalk.gui

import swing.Component
import collection.mutable.Buffer
import SwingSupport._

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/02
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */

class ComponentBuilder(val contents: Buffer[Component]) {

  def addWithGap(component: Component)(implicit g: Gap) = {
    contents += component
    contents += gap(g.x)
  }

}

