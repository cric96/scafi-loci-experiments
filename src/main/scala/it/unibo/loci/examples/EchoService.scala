package it.unibo.loci.examples

import loci._
import loci.communicator.{Connector, Listener}
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import loci.messaging.ConnectionsBase
import rescala.default._

import java.io.File

@multitier object Chat {
  @peer type Server <: { type Tie <: Multiple[Client] }
  @peer type Client <: { type Tie <: Single[Server] }

  val message : Evt[String] on Client = on[Client] { Evt[String] }

  val publicMessage = on[Server] sbj { client: Remote[Client] =>
    message.asLocalFromAllSeq collect {
      case (remote, message) if remote == client => message
    }
  }

  def main() = on[Client] {
    publicMessage.asLocal observe println

    for (line <- scala.io.Source.stdin.getLines)
      message.fire(line)
  }
}

class ServerWithProtocol(protocol: Listener[ConnectionsBase.Protocol]) {
  multitier start new Instance[Chat.Server](listen[Chat.Client](protocol))
}
//object ServerWithProtocol {
//  def apply(protocol: Listener[ConnectionsBase.Protocol]) =
//}

class ServerTCP(port: Int = 43053) extends ServerWithProtocol(TCP(port))

class ClientWithProtocol(protocol: Connector[ConnectionsBase.Protocol]) {
  multitier start new Instance[Chat.Client](connect[Chat.Server](protocol))
}
//object ClientWithProtocol {
//  def apply(protocol: Connector[ConnectionsBase.Protocol]) =
//}

class ClientTCP(host: String = "localhost", port: Int = 43053) extends ClientWithProtocol(TCP(host, port))

object LocalhostDemo {
  def main(args: Array[String]): Unit = {
    def threadOf(code: () => Unit): Thread = new Thread(() => code())
    threadOf(() => println(new ServerTCP())).start()
    Thread.sleep(100)
    threadOf(() => println(new ClientTCP())).start()
  }
}
