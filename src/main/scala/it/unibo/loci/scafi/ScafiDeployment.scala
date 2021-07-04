package it.unibo.loci.scafi

import loci._
import loci.communicator.tcp._
import loci.language.Placement
import rescala.default._
import loci.transmitter.rescala._
import loci.serializer.circe._

import java.util.UUID
import LociIncarnation._

@multitier object P2PScafiDeployment extends CompleteScafiSystem
  with SensorComponent
  with ActuatorComponent
  with BehaviourComponent
  with CommunicationComponent
  with StateComponent {
  @peer type Peer <: { type Tie <: Multiple[Peer] }
  @peer type StateDevice <: Peer
  @peer type ActuatorDevice <: Peer
  @peer type CommunicationDevice <: Peer
  @peer type BehaviourDevice <: Peer
  @peer type SensorDevice <: Peer

  val identifier : P2PScafiDeployment.Identifier on Peer = on[Peer] { UUID.randomUUID().hashCode() }
  val exports : Var[Set[ExportMessage]] on Peer = on[Peer] { Var[Set[ExportMessage]](Set.empty) }

  override def compute(id: P2PScafiDeployment.Identifier, state: Pre): Post on Peer = {
    val program = new AggregateProgram {
      override def main(): Any = foldhood(0)(_ + _)(1)
    }
    val (old, exports, sensors) = state
    val contextRep = sensors.groupBy { case (name, _) => name }
      .map { case (name, value) => name -> (value : Any) }
    val context = new ContextImpl(id, exports, contextRep, Map.empty)
    val e : EXPORT = program.round(context)
    (old, id -> e, Set(e.toString))
  }

  override def sense(id: P2PScafiDeployment.Identifier): Set[Sensor] on Peer = on[Peer] { Set.empty[Sensor] }
  override def exports(id: Int): Set[ExportMessage] on Peer = exports.now

  override def put(id: Int, export: ExportMessage): Unit on Peer = {
    exports.transform(exports => exports + export)
  }
  override def get(id : P2PScafiDeployment.Identifier) : State on Peer = on[Peer] { "" }
  override def update(id : P2PScafiDeployment.Identifier, state : State) : Unit on Peer = on[Peer] {}
  override def act(id : P2PScafiDeployment.Identifier, export : Set[Actuation]) : Unit on ActuatorDevice = on[ActuatorDevice] {
    println(s"$id data = ${export}")
  }
  override def comm(id: Int, export: ExportMessage): Unit on Peer = on[Peer] {
    println("Here..")
    remote.call(put(id, export))
  }

  def main() = on[Peer] {
    /*while(true) {
      val sensor = sense(id)
      val localExports = exports(id)
      val state = get(id)
      val computed = compute(id, (state, localExports, sensor))
      act(id, computed._3)
      comm(id, computed._2)
      update(id, computed._1)
      Thread.sleep(1000)
    }*/
  }
}

object Peer extends App {
  multitier.start(new Instance[P2PScafiDeployment.Peer](
    listen[P2PScafiDeployment.Peer](TCP(3245))
  ))
  multitier.start(new Instance[P2PScafiDeployment.Peer](
    connect[P2PScafiDeployment.Peer](TCP("localhost", 3245))
  ))
}