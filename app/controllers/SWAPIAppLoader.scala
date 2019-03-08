package controllers

import akka.actor.ActorSystem
import play.api
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire._
import modules.SWAPIModule

class SWAPIAppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): api.Application

  = {
    // set up logger
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
    new SWAPIComponents(context).application
  }
}

class SWAPIComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AssetsComponents
  with I18nComponents
  with play.filters.HttpFiltersComponents
  with SWAPIModule {

  import com.softwaremill.macwire.wire

  override lazy val assets: Assets = wire[Assets]
  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }

}