package net.floaterio.traktalk.edit

import swing._
import event.{ValueChanged, EditDone}
import net.floaterio.traktalk.common.GeneralConfiguration
import net.floaterio.traktalk.gui.SwingSupport._
import net.floaterio.traktalk.gui.LabelWidth


/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/01
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */

class EditDialog(controller : EditDialogController, owner: Window) extends Dialog (owner) {

  modal = true
  title = "Edit Settings"

  val templateText = new TextArea(3, 40){
    appendValidation(reactions)
  }
  def appendValidation(r: Reactions) = r += {
      case ValueChanged(source) => {
        controller.validate()
      }
  }
  val serverPort = new TextField(5) {
    appendValidation(reactions)
  }

  val doneButton = simpleButton("Done") {
    controller.done()
  }

  contents = new BoxPanel(Orientation.Vertical) {
    implicit val labelWidth = LabelWidth(100)
    contents += labeled("Template", templateText)
    contents += labeled("Port", serverPort)
    contents += new FlowPanel(FlowPanel.Alignment.Right)(
      doneButton,
      simpleButton("Cancel") {
        controller.cancel()
      }
    )
  }

}

class EditDialogController(config: GeneralConfiguration, owner: Window) {

  val dialog = new EditDialog(this, owner)
  import dialog._

  def openDialog() = {
    templateText.text = config.template
    serverPort.text = config.port.toString
    dialog.setLocationRelativeTo(owner)
    dialog.open()
  }

  def done() = {
    config.saveTemplate(templateText)
    config.savePort(serverPort.text.toInt)
    dispose()
  }

  def cancel () {
    dispose()
  }

  def validate() {
    doneButton.enabled = (validateText() && validatePort())
  }

  def validateText() = {
    templateText.length != 0
  }

  def validatePort() = {
    try {
      if(serverPort.length == 0) {
        false
      } else {
        serverPort.text.toInt
        true
      }
    } catch {
      case e:Exception => false
    }
  }
}
