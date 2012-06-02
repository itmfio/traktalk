package net.floaterio.traktalk.server

import java.net.ServerSocket
import java.io._
import actors.Actor._
import org.apache.commons.logging.LogFactory
import net.floaterio.traktalk.common.{SendMessage, GlobalEventHandler}
import java.util.concurrent.{Future, Executors}

/**
 * Created with IntelliJ IDEA.
 * User: Izumi
 * Date: 12/05/26
 * Time: 3:17
 * To change this template use File | Settings | File Templates.
 */

class SongNameServer(port: Int) {

  val logger = LogFactory.getLog(getClass)

  private var listener: ServerSocket = null
  private var future: Future[_] = null
  private var songNameListeners = List[(SongNameInfo => Unit)]()

  private val executor = Executors.newSingleThreadExecutor()

  def += (l: (SongNameInfo => Unit)) = {
    songNameListeners = l :: songNameListeners
  }

  val songNameActor = actor {
    loop {
      react {
        case info : SongNameInfo => songNameListeners.reverse.foreach(l => {
          try {
            l.apply(info)
          } catch {
            case e:Exception  => logger.error("error in songname listener", e)
          }
        })
      }
    }
  }

  def runServer = {
    stop

    listener = new ServerSocket(port)
    future = executor.submit(new Runnable {
      def run() {
        while(!future.isCancelled){
          listen
          Thread.sleep(1000)
        }
      }
    })
  }

  def stop = {
    safeClose(listener)
    if (future != null && !future.isCancelled && !future.isDone) {
      future.cancel(false)
      future = null
    }
  }

  private def listen = {

    var in: BufferedReader = null
    var out: BufferedWriter = null

    try {
      logger.info("listen")

      GlobalEventHandler.sendMessage("Start Server")

      val socket = listener.accept()
      in = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))

      var ok = false;

      while (in.ready()) {
        val str = in.readLine()
        if (str.contains("Native Instruments IceCast Uplink")) {
          ok = true
        }
      }

      if (ok) {
        out.write("HTTP/1.0 200 OK\r\n\r\n")
        out.flush()

        GlobalEventHandler ! SendMessage("Connected")

        val pattern = ".*ARTIST=(.*)TITLE=(.*)vorbis".r

        while (!listener.isClosed) {
          val str = in.readLine()
          if (str != null) {
            if (str.contains("ARTIST")) {
              pattern.findFirstMatchIn(str).map(m => {
                val (artist, title) = m.group(1) -> m.group(2)
                logger.info("artist=%s title=%s".format(artist, title))
                songNameActor ! SongNameInfo(artist, title)
              })
            }
          } else {
            logger.info("null string")
            Thread.sleep(1000)
          }
        }

        out.flush()

      } else {
        out.write("HTTP/1.0 404 Not found\r\n\r\n")
        out.flush()
      }
    } catch {
      case e: IOException => {
        // GlobalEventHandler.sendMessage("Connection Error Occured")
        logger.warn("IO Error", e)
      }
    } finally {

      GlobalEventHandler.sendMessage("Stop Server")

      safeClose(in)
      safeClose(out)
    }
  }

  def safeClose[T <: {def close()}](c: T) = {
    if (c != null) {
      try {
        c.close()
      } catch {
        case e: IOException => logger.error("error occured in close", e)
      }
    }
  }

}
