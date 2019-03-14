package modules

import com.softwaremill.macwire.wire
import controllers.{SWAPIController, SangriaPlaygroundController}
import play.api.BuiltInComponents
import play.api.mvc.ControllerComponents

trait SWAPIModule extends BuiltInComponents {

  lazy val swapiController = wire[SWAPIController]
  lazy val sangriaPlaygroundController = wire[SangriaPlaygroundController]

  def controllerComponents: ControllerComponents

//  lazy val system: ActorSystem = actorSystem
  //  lazy val config: Config = ConfigFactory.load()
}
