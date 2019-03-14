package controllers

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import cats.syntax.either._
import io.circe._, io.circe.parser._
import models.{CharacterRepo, SchemaDefinition}
import play.api.libs.circe.Circe
import play.api.mvc._
import sangria.execution.Executor
import sangria.execution.deferred.DeferredResolver
import sangria.macros._
import sangria.marshalling.circe._
import sangria.renderer.SchemaRenderer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SangriaCirceController(val controllerComponents: ControllerComponents) extends BaseController with Circe {

  case class Bar(bar: Int)

  case class Foo(foo: String, bar: Bar)

  val bar = Bar(1)
  val foo = Foo("foo", bar)

  def index = Action {
    Ok(foo.asJson)
  }

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(SchemaDefinition.StarWarsSchema))
  }


  def executeQuery = Action.async {

    val query =
      graphql"""
               query HeroAndFriends {
                 hero {
                   name
                   friends {
                     name
                   }
                 }
               }
  """

    val result: Future[Json] =
      Executor.execute(SchemaDefinition.StarWarsSchema, query, new CharacterRepo, deferredResolver = DeferredResolver.fetchers(SchemaDefinition.characters))
    val futureResult: Future[Result] = result.map { data => Ok(data) }
    futureResult
  }

}
