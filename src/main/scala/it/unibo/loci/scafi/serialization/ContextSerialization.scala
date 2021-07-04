package it.unibo.loci.scafi.serialization

import it.unibo.scafi.incarnations.Incarnation

//TODO
trait ContextSerialization {
  self : SerializationPlatform with Incarnation with ExportSerialization =>
}
