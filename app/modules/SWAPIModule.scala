package modules

import com.softwaremill.macwire.wire
import controllers.SWAPIController
import play.api.BuiltInComponents
import play.api.mvc.ControllerComponents

trait SWAPIModule extends BuiltInComponents {

  lazy val sangriaController = wire[SWAPIController]

  def controllerComponents: ControllerComponents

//  lazy val system: ActorSystem = actorSystem
  //  lazy val config: Config = ConfigFactory.load()
}
