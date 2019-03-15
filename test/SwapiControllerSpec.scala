import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import controllers.{Assets, AssetsComponents, SWAPIComponents, SangriaCirceController}
import modules.SWAPIModule
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.{PlaySpec, _}
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
import io.circe.optics.JsonPath._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import cats.syntax.either._
import com.stephenn.scalatest.jsonassert.JsonMatchers
import io.circe._
import io.circe.parser._

import scala.concurrent.Future


class SwapiControllerSpec extends PlaySpec with OneAppPerSuiteWithComponents with JsonMatchers {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val controller = new SangriaCirceController(Helpers.stubControllerComponents(playBodyParsers = Helpers.stubPlayBodyParsers(materializer)))




  "SangriaCirceController Page#index" should {
    "should be valid" in {
      val _foo = root.foo.string
      val result: Future[Result] = controller.index.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      //      bodyText mustBe "{\"foo\":\"foo\",\"bar\":{\"bar\":1}}"
      val expected =
        """ {"foo":"foo","bar":{"bar":1}} """
      bodyText should matchJson(expected)

    }
    "Page#renderSchema should  be valid" in {
      val result: Future[Result] = controller.renderSchema.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText should not be empty
    }
    "Page#executeQuery should  be valid" in {
      val result: Future[Result] = controller.executeQuery.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      val expected = """ {"data":{"hero":{"name":"R2-D2","friends":[{"name":"Luke Skywalker"},{"name":"Han Solo"},{"name":"Leia Organa"}]}}} """
      bodyText should matchJson(expected)
    }
  }


  override def components: BuiltInComponents = new SWAPIComponents(context)
}
