import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import controllers.{Assets, AssetsComponents, SWAPIComponents, SangriaCirceController}
import modules.SWAPIModule
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play._
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.ApplicationLoader.Context
import play.api.{BuiltInComponents, BuiltInComponentsFromContext}
import play.api.i18n.I18nComponents
import play.api.mvc.{ControllerComponents, Result, Results}
import play.api.routing.Router
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._
import router.Routes
import Matchers._

import scala.concurrent.Future

class SangriaCirceBasicSpec extends PlaySpec with OneAppPerSuiteWithComponents {
  //  def controllerComponents: ControllerComponents =

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val controller = new SangriaCirceController(Helpers.stubControllerComponents(playBodyParsers = Helpers.stubPlayBodyParsers(materializer)))
  "SangriaCirceController Page#index" should {
    "should be valid" in {

      val result: Future[Result] = controller.index.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "{\"foo\":\"foo\",\"bar\":{\"bar\":1}}"
    }
    "Page#renderSchema should  be valid" in {
      val result: Future[Result] = controller.renderSchema.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText should not be empty
    }
    "Page#executeQuery should  be valid" in {
      val result: Future[Result] = controller.executeQuery.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      println(" Heros " + bodyText )
      bodyText should not be empty
    }
  }


  override def components: BuiltInComponents = new SWAPIComponents(context)
}
