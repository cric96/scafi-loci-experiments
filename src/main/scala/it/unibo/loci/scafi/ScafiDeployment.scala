package it.unibo.loci.scafi

import it.unibo.scafi.incarnations.Incarnation
import loci._
import loci.communicator.tcp._
import loci.language.Placement
import rescala.default._
import loci.transmitter.rescala._
import loci.serializer.circe._
import rescala.parrp.ParRPStruct
import rescala.reactives
import java.util.UUID
import LociIncarnation._

@multitier trait NoSensors extends SensorComponent {
  self : LogicalPulverisableSystem =>
  override def sense(id: ID): Set[Sensor] on SensorDevice = Set.empty[Sensor]
}

@multitier trait ConsoleActuators extends ActuatorComponent {
  self : LogicalPulverisableSystem =>
  override def act(id: ID, export: Export): Unit on ActuatorDevice = println(s"id : ${id}, export ${`export`}")
}

@multitier object P2PScafiDeployment extends CompleteScafiSystem
  with NoSensors
  with ConsoleActuators
  with BehaviourComponent
  with CommunicationComponent
  with StateComponent {
  @peer type Peer <: {
    type Tie <: Multiple[Peer]
  }
  @peer type StateDevice <: Peer
  @peer type ActuatorDevice <: Peer
  @peer type CommunicationDevice <: Peer
  @peer type BehaviourDevice <: Peer
  @peer type SensorDevice <: Peer

  val state : Var[State] on Peer = on[Peer] { Var[State]((Map.empty, Set.empty)) }
  val id : ID on Peer = on[Peer] { UUID.randomUUID().hashCode() }

  override def get(id: P2PScafiDeployment.ID): on[State, P2PScafiDeployment.Peer] = placed { state.readValueOnce }

  override def compute(id: P2PScafiDeployment.ID, state: State): on[Export, P2PScafiDeployment.Peer] = {
    val program = new AggregateProgram {
      override def main(): Any = rep(10)(_ + 1)
    }
    val (exports, sensors) = state
    val contextRep = sensors.groupBy { case (name, _) => name }
      .map { case (name, value) => name -> (value : Any) }
    val context = new ContextImpl(id, exports, contextRep, Map.empty)
    val e : Export = program.round(context)
    e
  }

  override def put(id: Int, export: LociIncarnation.Export with LociIncarnation.ExportOps): on[Unit, P2PScafiDeployment.Peer] = on[Peer] { }

  override def comm(id: Int, export: LociIncarnation.Export with LociIncarnation.ExportOps): on[Unit, P2PScafiDeployment.Peer] = on[Peer] {
    remote.call(put(id, export))
  }

  def main() = on[Peer] {
    val sensor = sense(id)
    val state = get(id)
    val export = compute(id, state)
    act(id, export)
    comm(id, export)
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