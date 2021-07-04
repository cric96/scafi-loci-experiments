package it.unibo.loci.scafi

import loci._
import loci.communicator.tcp._
import loci.language.Placement
import rescala.default._
import loci.transmitter.rescala._
import loci.serializer.circe._
import rescala.parrp.ParRPStruct
import rescala.reactives
/**
 * Reference article : Pulverization in Cyber-Physical Systems: Engineering the Self-Organizing Logic Separated from Deployment
 * DOI : https://doi.org/10.3390/fi12110203
 */
@multitier trait LogicalPulverisableSystem {
  type Identifier
  type Sensor
  type ExportMessage
  type State
  type Actuation
  type Pre = (State, Set[ExportMessage], Set[Sensor])
  type Post = (State, ExportMessage, Set[Actuation])
}

@multitier trait SensorComponent {
  self : LogicalPulverisableSystem =>
  @peer type SensorDevice
  def sense(id : Identifier) : Set[Sensor] on SensorDevice
}

@multitier trait ActuatorComponent {
  self : LogicalPulverisableSystem =>
  @peer type ActuatorDevice
  def act(id : Identifier, export : Set[Actuation]) : Unit on ActuatorDevice
}

@multitier trait BehaviourComponent {
  self : LogicalPulverisableSystem =>
  @peer type BehaviourDevice
  def compute(id : Identifier, state : Pre) : Post on BehaviourDevice
}

@multitier trait StateComponent {
  self : LogicalPulverisableSystem =>
  @peer type StateDevice
  def get(id : Identifier) : State on StateDevice
  def update(id : Identifier, state : State) : Unit on StateDevice
}

@multitier trait CommunicationComponent {
  self : LogicalPulverisableSystem =>
  @peer type CommunicationDevice
  def exports(id : Identifier) : Set[ExportMessage] on CommunicationDevice //data
  def put(id : Identifier, export : ExportMessage) : Unit on CommunicationDevice //in
  def comm(id : Identifier, export : ExportMessage) : Unit on CommunicationDevice //out
}

@multitier trait CompleteLogicalSystem extends LogicalPulverisableSystem {
  self: SensorComponent with ActuatorComponent with BehaviourComponent with StateComponent with CommunicationComponent =>
}