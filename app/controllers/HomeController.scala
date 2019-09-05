package controllers

import javax.inject._
import play.api.mvc._


/*
 *  home controller so we can see things are running!
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    Ok("Hello the API is running!")
  }

}
