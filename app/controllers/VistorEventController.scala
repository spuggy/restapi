package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.VisitorEventService

import scala.concurrent.ExecutionContext

/*
 *  main controller to handle api calls
 */
@Singleton
class VistorEventController @Inject()(cc: ControllerComponents, vistorEventService: VisitorEventService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import models.Constants._

  def list(startDate: String, boundary: String, limit: Option[Int]) = Action.async { implicit request =>

    val clientIdOpt = request.headers.get(CLIENT_ID)

    vistorEventService.list(startDate, boundary, limit) map { locationEvents =>
      Ok(Json.toJson(locationEvents.map(_.locationElement)))
    }

  }

  def create = Action.async  { implicit request =>

    val clientIdOpt = request.headers.get(CLIENT_ID)

    val body = request.body.asJson.get

    vistorEventService.create(clientIdOpt, body) map { insertCount =>
      Ok(insertCount.toString)
    }

  }

}
