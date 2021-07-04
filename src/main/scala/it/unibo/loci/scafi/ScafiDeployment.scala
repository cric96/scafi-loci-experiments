package it.unibo.loci.scafi

import it.unibo.loci.scafi.P2PScafiDeployment.{BehaviourDevice, CommunicationDevice}
import it.unibo.scafi.incarnations.Incarnation
import loci._
import loci.communicator.tcp._
import loci.language.Placement
import rescala.default._
import loci.transmitter.rescala._
import loci.serializer.circe._
import rescala.parrp.ParRPStruct
import rescala.reactives

@multitier trait NoSensors extends SensorComponent {
  self : LogicalPulverisableSystem =>
  override def sense(id: ID): Set[Sensor] on SensorDevice = Set.empty[Sensor]
}

@multitier trait NoActuators extends ActuatorComponent {
  self : LogicalPulverisableSystem =>
  override def act(id: ID, export: Export): Unit on ActuatorDevice = {}
}

@multitier object P2PScafiDeployment extends CompleteScafiSystem
  with NoSensors
  with NoActuators
  with BehaviourComponent
  with CommunicationComponent
  with StateComponent {
  @peer type Peer <: StateDevice with ActuatorDevice with CommunicationDevice with BehaviourDevice with SensorDevice {
    type Tie <: Multiple[Peer]
  }
  override def connections(id: P2PScafiDeployment.ID): Set[ID] on CommunicationDevice = on[CommunicationDevice] { Set.empty[ID] }

  override def get(id: P2PScafiDeployment.ID): on[State, P2PScafiDeployment.StateDevice] = ???

  override def compute(id: P2PScafiDeployment.ID, state: State): on[Export, P2PScafiDeployment.BehaviourDevice] = ???

  override def fire(id: Int, export: LociIncarnation.Export with LociIncarnation.ExportOps): on[Unit, P2PScafiDeployment.CommunicationDevice] = ???
}

@multitier object ThinThickScafiDeployment extends CompleteScafiSystem
  with NoSensors
  with NoActuators
  with BehaviourComponent
  with CommunicationComponent
  with StateComponent {
  @peer type Device <: StateDevice with ActuatorDevice with BehaviourDevice with SensorDevice { }

  @peer type Broker <: CommunicationDevice
  override type BehaviourDevice = this.type

  override def compute(id: Int, state: (Int, Iterable[LociIncarnation.Export with LociIncarnation.ExportOps])): on[LociIncarnation.Export with LociIncarnation.ExportOps, ThinThickScafiDeployment.type] = ???

  override def connections(id: Int): on[Set[Int], ThinThickScafiDeployment.type] = ???

  override def fire(id: Int, export: LociIncarnation.Export with LociIncarnation.ExportOps): on[Unit, ThinThickScafiDeployment.type] = ???

  override def get(id: Int): on[(Int, Iterable[LociIncarnation.Export with LociIncarnation.ExportOps]), ThinThickScafiDeployment.type] = ???

}
