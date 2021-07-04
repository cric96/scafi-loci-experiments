package it.unibo.loci.scafi

import it.unibo.loci.scafi.serialization.{ExportSerialization, SerializationPlatform}
import it.unibo.scafi.incarnations.AbstractTestIncarnation

object LociIncarnation extends AbstractTestIncarnation with SerializationPlatform with ExportSerialization