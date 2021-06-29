package it.unibo.loci.scafi

import loci._

/**
 * Reference article : Pulverization in Cyber-Physical Systems: Engineering the Self-Organizing Logic Separated from Deployment
 * DOI : https://doi.org/10.3390/fi12110203
 */
@multitier trait DeviceDescriptor {
  /**
   * Logical components used to define a device in a cyber-physical system.
   */
  // γ
  @peer type Behaviour <: { type Tie <: Single[State] }
  // α
  @peer type Actuators
  // σ
  @peer type Sensors <: { type Tie <: Single[State] }
  // k
  @peer type State <: { type Tie <: Single[Actuators] with Single[Communication] with Single[Behaviour] }
  // x
  @peer type Communication <: { type Tie <: Multiple[Communication] with Single[State] }
}

//trait Deployment
//
//object AvailableDeployments {
//  @multitier object P2PDeployment extends Deployment with DeviceDescriptor {
//    @peer type Node <: { type Tie <: Behaviour with Actuators with Sensors with State with Communication }
//  }
//  @multitier object CloudMixedDeployment extends Deployment with DeviceDescriptor {
//    @peer type ThinCloud <: { type Tie <: State with Communication }
//    @peer type CloudWithBehavior <: { type Tie <: ThinCloud with Behaviour }
//    @peer type ThinDevice <: { type Tie <: Actuators with Sensors with Single[CloudWithBehavior] }
//    // Ask guido about how to make sure that both thin and thick devices have missing components in the cloud
//    @peer type ThickDevice <: { type Tie <: Actuators with Sensors with Behaviour with Single[ThinCloud] }
//  }
//}
