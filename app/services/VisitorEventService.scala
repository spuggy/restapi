package services

import javax.inject.{Inject, Singleton}
import models.{LocationElement, VisitorEvent, VistorEventRepository}
import org.joda.time.DateTime
import play.api.libs.json.{JsArray, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/*
 *  service layer to abstract JSON parsing, validation and the db
 */
@Singleton
class VisitorEventService @Inject() (db: VistorEventRepository)(implicit ec: ExecutionContext) {

  /*
   creates VistorEvents from a json payload returns number of events added
   */
  def create(clientIdStrOpt: Option[String], body: JsValue): Future[Int] = {

    val deviceIdOpt = (body \ "deviceId").asOpt[Long]
    val modelOpt = (body \ "model").asOpt[String]
    val locationElementsOpt = (body \ "locationElements").asOpt[JsArray]

    val clientIdOpt = clientIdStrOpt.flatMap(clientIdStr => Try(clientIdStr.toLong).toOption)

    val visitorEvents = locationElementsOpt.map(_.value.flatMap { locationElementJs =>
      val latOpt = (locationElementJs \ "lat").asOpt[Double]
      val lonOpt = (locationElementJs \ "lon").asOpt[Double]
      val dtOpt = (locationElementJs \ "dt").asOpt[String].map { dtStr =>
        DateTime.parse(dtStr)
      }

      (deviceIdOpt, clientIdOpt, modelOpt, latOpt, lonOpt, dtOpt) match {
        case (Some(deviceId), Some(clientId), Some(model), Some(lat), Some(lon), Some(dt)) =>
          Some(VisitorEvent(clientId = clientId, deviceId = deviceId, dt = dt, model = model, locationElement = LocationElement(lat, lon)))
        case _ => throw new RuntimeException("missing fields")
      }

    }).map(_.toSeq).getOrElse(Seq.empty[VisitorEvent])

    if (visitorEvents.isEmpty) {
      Future.successful(0)
    } else {
      db.create(visitorEvents) map { res =>
        res
      }
    }
  }

  /*
   returns list of vistor events from a startdate and within an area limited by the boundary
   */
  def list(startDateStr: String, boundaryListStr: String, limit: Option[Int]): Future[Seq[VisitorEvent]] = {

    val startDateOpt = Try(DateTime.parse(startDateStr)).toOption
    val boundaryList = boundaryListStr.split(",").flatMap(item => Try(item.toDouble).toOption)

    startDateOpt match {
      case Some(startDate) if boundaryList.size == 4 =>
        db.list(startDate, boundaryList(0), boundaryList(1), boundaryList(2), boundaryList(3), limit)
      case _ => throw new RuntimeException("missing parameters")
    }
  }

}
