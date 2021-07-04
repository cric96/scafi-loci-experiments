package it.unibo.loci.scafi

import loci._
import loci.communicator.tcp._
import loci.language.Placement
import rescala.default._
import loci.transmitter.rescala._
import loci.serializer.circe._
import rescala.parrp.ParRPStruct
import rescala.reactives

@multitier trait LogicalPulverisableSystem {
  type ID
  type Sensor
  type Export
  type State
}

@multitier trait SensorComponent {
  self : LogicalPulverisableSystem =>
  @peer type SensorDevice
  def sense(id : ID) : Set[Sensor] on SensorDevice
}

@multitier trait ActuatorComponent {
  self : LogicalPulverisableSystem =>
  @peer type ActuatorDevice
  def act(id : ID, export : Export) : Unit on ActuatorDevice
}

@multitier trait BehaviourComponent {
  self : LogicalPulverisableSystem =>
  @peer type BehaviourDevice
  def compute(id : ID, state : State) : Export on BehaviourDevice
}

@multitier trait StateComponent {
  self : LogicalPulverisableSystem =>
  @peer type StateDevice
  def get(id : ID) : State on StateDevice
}

@multitier trait CommunicationComponent {
  self : LogicalPulverisableSystem =>
  @peer type CommunicationDevice
  def put(id : ID, export : Export) : Unit on CommunicationDevice //in
  def comm(id : ID, export : Export) : Unit on CommunicationDevice //out
}

@multitier trait CompleteLogicalSystem extends LogicalPulverisableSystem {
  self: SensorComponent with ActuatorComponent with BehaviourComponent with StateComponent with CommunicationComponent =>
}