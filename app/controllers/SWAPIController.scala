package controllers

import akka.actor.ActorSystem
//import play.api.libs.json._
import play.api.Configuration
import play.api.mvc._
import sangria.execution._
import sangria.parser.{QueryParser, SyntaxError}
//import sangria.marshalling.playJson._
import models.{CharacterRepo, SchemaDefinition}
import sangria.execution.deferred.DeferredResolver
import sangria.renderer.SchemaRenderer
import sangria.slowlog.SlowLog
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import io.circe.Json
import cats.syntax.either._
import io.circe._, io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import sangria.macros._
import sangria.marshalling.circe._
import sangria.renderer.SchemaRenderer
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


class SWAPIController(system: ActorSystem, config: Configuration, cc: ControllerComponents) extends AbstractController(cc) with Circe {

  import system.dispatcher


  def graphql(query: String, variables: Option[String], operation: Option[String]): Action[AnyContent] = Action.async { request ⇒
    executeQuery(query, variables map parseVariables, operation, isTracingEnabled(request))
  }

  def graphqlBody = Action.async(parse.json) { request ⇒
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]

    val variables = (request.body \ "variables").asOpt[String]

    executeQuery(query, variables map parseVariables , operation, isTracingEnabled(request))
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else io.circe.parser.parse(variables).getOrElse(Json.Null)

  private def executeQuery(query: String, variables: Option[Json], operation: Option[String], tracing: Boolean) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        Executor.execute(SchemaDefinition.StarWarsSchema, queryAst, new CharacterRepo,
          operationName = operation,
          variables = variables getOrElse Json.obj(),
          deferredResolver = DeferredResolver.fetchers(SchemaDefinition.characters),
          exceptionHandler = exceptionHandler,
          queryReducers = List(
            QueryReducer.rejectMaxDepth[CharacterRepo](15),
            QueryReducer.rejectComplexQueries[CharacterRepo](4000, (_, _) ⇒ TooComplexQueryError)),
          middleware = if (tracing) SlowLog.apolloTracing :: Nil else Nil).map { data => Ok(data) }
      //          .recover {
      //            case error1: QueryAnalysisError ⇒ BadRequest  → Future {
      //              // Call some blocking API
      //              Ok(error1.resolveError)
      //            }
      //            case error2: ErrorWithResolver ⇒ InternalServerError →  Future {
      //              // Call some blocking API
      //              Ok(error2.resolveError)
      //            }
      //          }


      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) =>
        Future.successful(BadRequest(Json.obj(
          "syntaxError" → error.getMessage.asJson,
          "locations" → Json.arr(Json.obj(
            "line" → error.originalError.position.line.asJson,
            "column" → error.originalError.position.column.asJson)))))

      case Failure(error) ⇒
        throw error
    }

  def isTracingEnabled(request: Request[_]) = request.headers.get("X-Apollo-Tracing").isDefined


  lazy val exceptionHandler = ExceptionHandler {
    case (_, error@TooComplexQueryError) ⇒ HandledException(error.getMessage)
    case (_, error@MaxQueryDepthReachedError(_)) ⇒ HandledException(error.getMessage)
  }

  case object TooComplexQueryError extends Exception("Query is too expensive.")

}
