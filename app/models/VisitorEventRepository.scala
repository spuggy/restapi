package models

import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.db.DBApi

import scala.concurrent.Future

object LocationElement {
  implicit val format: Format[LocationElement] = Json.format
}

case class LocationElement(lat: Double, lon: Double)

/*
 *  a visitor location event
 */
case class VisitorEvent(clientId: Long,   // the api client who is using the SDK
                        deviceId: Long,   // the id of the individual device who submitted the event
                        dt: DateTime,     // the timestamp of the event
                        model: String,    // the model of the device e.g iphone 6
                        locationElement: LocationElement)  // the lat lon of the event

@javax.inject.Singleton
class VistorEventRepository @Inject() (dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  import anorm.JodaParameterMetaData._

  private val simple = {
      get[Long]("client_id") ~
      get[Long]("device_id") ~
      get[String]("model") ~
      get[DateTime]("dt") ~
      get[Double]("lat") ~
      get[Double]("lon") map {
        case clientId ~ deviceId ~ model ~ dt ~ lat ~ lon =>
          VisitorEvent(clientId, deviceId, dt, model, LocationElement(lat, lon))
      }
  }

  def list(startDate: DateTime, d1: Double, d2: Double, d3: Double, d4: Double, limitOpt: Option[Int]): Future[Seq[VisitorEvent]] = {

    var limit = limitOpt.getOrElse(20)

    Future {

      db.withConnection { implicit connection =>

        val query =
          SQL"""
          select client_id, device_id, model, dt, location_element[0] as lat,location_element[1] as lon
          from location_event where
          box(point($d1,$d2),point($d3,$d4)) @> location_element and
          dt >= $startDate
          limit ${limit}
      """

        println(query)

        query.as(simple *)

      }
    }(ec)
  }

  def create(locationEvents: Seq[VisitorEvent]): Future[Int] = Future {

    val params = locationEvents.map { le =>
      Seq[NamedParameter]("client_id" -> le.clientId, "device_id" -> le.deviceId, "model" -> le.model, "dt" -> le.dt, "lat" -> le.locationElement.lat, "lon" -> le.locationElement.lon)
    }

    db.withConnection { implicit connection =>
      val batch = BatchSql(
        "INSERT INTO location_event(client_id, device_id, dt, model, location_element ) VALUES ({client_id},{device_id}, {dt}, {model}, POINT({lat},{lon})) ON CONFLICT DO NOTHING", params.head, params: _*
      )
      val res: Array[Int] = batch.execute()
      res.sum
    }
  }(ec)

}
