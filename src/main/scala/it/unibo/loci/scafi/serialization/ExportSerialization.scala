package it.unibo.loci.scafi.serialization

import io.circe._
import it.unibo.scafi.incarnations.Incarnation
import loci.transmitter.IdenticallyTransmittable
import play.api.libs.json.{Json => PlayJSon}
trait ExportSerialization {
  self : SerializationPlatform with Incarnation =>
  private val rawDataField = "raw"

  implicit val exportTransmittable: IdenticallyTransmittable[EXPORT] = IdenticallyTransmittable()

  implicit val exportEncoder: Encoder[EXPORT] = (a: EXPORT) => {
    val decoded = writesExp.writes(a)
    val dataRaw = PlayJSon.toBytes(decoded).map(a => Json.fromInt(a.toInt))
    val json = Json.obj(rawDataField -> Json.fromValues(dataRaw))
    json
  }
  implicit val exportDecoder: Decoder[EXPORT] = (c: HCursor) => {
    //unsafe, fix
    val rawData = (c.value \\ rawDataField).head.asArray.get
    val raw = rawData.map(a => a.asNumber).collect { case Some(json) => json.toByte.get }.toArray
    readsExp.reads(PlayJSon.parse(raw)).asEither match {
      case Left(_) => Left(DecodingFailure.fromThrowable(new IllegalArgumentException("Fail!"), List.empty))
      case Right(value) => Right[DecodingFailure, EXPORT](value)
    }
  }
}
