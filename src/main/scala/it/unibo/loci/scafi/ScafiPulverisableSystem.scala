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

@multitier trait ScafiPulverisableSystem extends LogicalPulverisableSystem {
  override type ID = LociIncarnation.ID
  override type Sensor = Set[(String, String)]
  //override type Sensor = Map[LSNS, Any]
  override type Export = LociIncarnation.EXPORT
  override type State = (Map[ID, LociIncarnation.EXPORT], Sensor) //right representation is Context
  //override type State = Context
}

@multitier trait CompleteScafiSystem extends ScafiPulverisableSystem with CompleteLogicalSystem {
  self : SensorComponent with ActuatorComponent with BehaviourComponent with StateComponent with CommunicationComponent =>
}

