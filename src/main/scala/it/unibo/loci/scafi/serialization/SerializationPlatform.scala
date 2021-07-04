package it.unibo.loci.scafi.serialization

import it.unibo.scafi.distrib.actor.serialization.{BasicJsonAnySerialization, BasicSerializers}
import it.unibo.scafi.distrib.actor.serialization.BasicSerializers.{mapAnyFormat, mapAnyWrites, mapFormat}
import it.unibo.scafi.incarnations.Incarnation
import play.api.libs.json.{Format, JsArray, JsNumber, JsObject, JsResult, JsString, JsSuccess, JsValue, Reads, Writes}

trait SerializationPlatform {
  self: Incarnation =>
  import BasicSerializers._
  private val jsonAnyHelpers = new BasicJsonAnySerialization {}
  implicit val formatSlot: Format[Slot] = new Format[Slot] {
    override def writes(o: Slot): JsValue = o match {
      case Nbr(i) => JsObject(Map("type" -> JsString("nbr"), "index" -> JsNumber(i)))
      case Rep(i) => JsObject(Map("type" -> JsString("rep"), "index" -> JsNumber(i)))
      case FunCall(i, funId) =>
        JsObject(Map("type" -> JsString("funcall"), "index" -> JsNumber(i), "funId" -> jsonAnyHelpers.anyToJs(funId)))
      case FoldHood(i) => JsObject(Map("type" -> JsString("foldhood"), "index" -> JsNumber(i)))
      case Scope(key) => JsObject(Map("type" -> JsString("scope"), "key" -> jsonAnyHelpers.anyToJs(key)))
    }
    @annotation.nowarn
    override def reads(json: JsValue): JsResult[Slot] = JsSuccess {
      json match {
        case jo @ JsObject(underlying) if underlying.contains("type") =>
          jo.value("type") match {
            case JsString("nbr") => Nbr(jo.value("index").as[BigDecimal].toInt)
            case JsString("rep") => Rep(jo.value("index").as[BigDecimal].toInt)
            case JsString("funcall") => FunCall(jo.value("index").as[BigDecimal].toInt, jo.value("funId").as[String])
            case JsString("foldhood") => FoldHood(jo.value("index").as[BigDecimal].toInt)
            case JsString("scope") => Scope(jo.value("key").as[String])
          }
      }
    }
  }

  implicit val formatPath: Format[Path] = new Format[Path] {
    override def writes(p: Path): JsValue = JsArray(p.path.map(s => formatSlot.writes(s)))
    override def reads(json: JsValue): JsResult[Path] =
      JsSuccess(factory.path(json.validate[List[Slot]].get: _*))
  }

  implicit def formatExportMap[T: Format]: Format[Map[Path, T]] = mapFormat[Path, T]

  val readsExp: Reads[EXPORT] = (json: JsValue) =>
    JsSuccess(
      factory.export(
        json.as[Map[Path, Any]](mapAnyFormat).toSeq: _*
      )
    )
  val writesExp: Writes[EXPORT] = (o: EXPORT) => mapAnyWrites[Path].writes(o.paths)
}
