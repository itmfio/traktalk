package net.floaterio.traktalk.common

import actors.Actor
import collection.mutable.ListBuffer
import org.apache.commons.logging.LogFactory
import javax.swing.SwingUtilities

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/06/01
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */

object GlobalEventHandler extends Actor{

  this.start()

  val log = LogFactory.getLog(getClass)

  val listeners = ListBuffer[String => Unit]()
  val swingListeners = ListBuffer[String => Unit]()

  def act = {
    loop {
      react {
        case e: AddListener => {
          listeners += e.function
        }
        case e: AddSwingListener => {
          swingListeners += e.function
        }
        case e: SendMessage => {
          listeners.foreach  { l =>
            doIgnoreException {
              l.apply(e.text)
            }
          }
          swingListeners.foreach { l =>
            SwingUtilities.invokeLater(new Runnable {
              def run() {
                doIgnoreException {
                  l.apply(e.text)
                }
              }
            })
          }
        }
      }
    }
  }

  def doIgnoreException(f: => Unit) {
    try {
       f
    } catch {
      case e: Exception => {
        log.error("error occured", e)
      }
    }
  }

  def sendMessage(text : String) = {
     this ! SendMessage(text)
  }

}

case class AddListener(function: String => Unit)
case class AddSwingListener(function: String => Unit)
case class SendMessage(text: String)
