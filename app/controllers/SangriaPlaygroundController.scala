package controllers

import akka.actor.ActorSystem
import models.{SchemaDefinition}
import play.api.Configuration
import play.api.mvc._
import sangria.renderer.SchemaRenderer

class SangriaPlaygroundController(system: ActorSystem, config: Configuration, cc: ControllerComponents) extends AbstractController(cc) {

  val googleAnalyticsCode = config.getOptional[String]("gaCode")
  val defaultGraphQLUrl = config.getOptional[String]("defaultGraphQLUrl").getOrElse(s"http://localhost:${config.getOptional[Int]("http.port").getOrElse(9000)}/graphql")

  def index = Action {
    Ok(views.html.index(googleAnalyticsCode,defaultGraphQLUrl))
  }

  def playground = Action {
    Ok(views.html.playground(googleAnalyticsCode))
  }

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(SchemaDefinition.StarWarsSchema))
  }


}
