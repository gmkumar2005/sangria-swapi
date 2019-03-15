import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import controllers._
import modules.SWAPIModule
import org.scalatest.{FunSpec, GivenWhenThen, Matchers, WordSpec}
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
import com.stephenn.scalatest.jsonassert.JsonMatchers
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import cats.syntax.eq._ //for the === implementation
//import cats.syntax.eq._ //for the === implementation
import io.circe.optics.JsonPath._
import cats._
import cats.implicits._
import cats.instances._
import cats.instances.long._
import cats.instances.option._
import scala.concurrent.Future
import cats.data._

class SangriaCirceBasicSpec extends PlaySpec with OneAppPerSuiteWithComponents with JsonMatchers with GivenWhenThen {
  //  def controllerComponents: ControllerComponents =

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val controller = new SWAPIController(actorSystem, components.configuration, Helpers.stubControllerComponents(playBodyParsers = Helpers.stubPlayBodyParsers(materializer)))

  "A SWAPIController" should {
    "run basic queries" in {
      Given("a query to find hero and his friends")
      val herosandfriendsquery =
        """ query HeroAndFriends {
          |  hero {
          |    name
          |    friends {
          |      name
          |    }
          |  }
          |} """.stripMargin

      When("Graphql query is executed which returns a json response")
      val result: Future[Result] = controller.graphql(herosandfriendsquery, None, None).apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      val expectedResponse =
        """ {
          |  "data": {
          |    "hero": {
          |      "name": "R2-D2",
          |      "friends": [
          |        {
          |          "name": "Han Solo"
          |        },
          |         {
          |          "name": "Luke Skywalker"
          |        },
          |        {
          |          "name": "Leia Organa"
          |        }
          |      ]
          |    }
          |  }
          |}""".stripMargin


      Then("Hero name should be R2-D2")
      val expectedResponseJson = parse(expectedResponse) getOrElse Json.Null
      val actualResponseJson = parse(bodyText) getOrElse Json.Null
      val _HeroName = root.data.hero.name.string
      val expectedHeroName: Option[String] = Some("R2-D2")
      val actualHeroName: Option[String] = _HeroName.getOption(actualResponseJson)
      expectedHeroName should be(actualHeroName)
      And("R2-D2 should have three friends")
      val actualFriendsNames: List[String] = root.data.hero.friends.each.name.string.getAll(actualResponseJson)
      actualFriendsNames.size should be(3)
    }

    "support basic fragments" in {
      Given("a fragments to query common human and droid fields")
      When("Graphql query for human 1003 and droid 2001 is executed which returns a json response")
      val fragmentQry =
        """ query FragmentExample {
          |  human(id: "1003") {
          |    ...Common
          |    homePlanet
          |  }
          |
          |  droid(id: "2001") {
          |    ...Common
          |    primaryFunction
          |  }
          |}
          |
          |fragment Common on Character {
          |  name
          |  appearsIn
          |}""".stripMargin

      val result: Future[Result] = controller.graphql(fragmentQry, None, None).apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      val expectedResponse =
        """ {
          |  "data": {
          |    "human": {
          |      "name": "Leia Organa",
          |      "appearsIn": [
          |        "NEWHOPE",
          |        "EMPIRE",
          |        "JEDI"
          |      ],
          |      "homePlanet": "Alderaan"
          |    },
          |    "droid": {
          |      "name": "R2-D2",
          |      "appearsIn": [
          |        "NEWHOPE",
          |        "EMPIRE",
          |        "JEDI"
          |      ],
          |      "primaryFunction": "Astromech"
          |    }
          |  }
          |}""".stripMargin

      Then("Human 1003 name should be Leia Organa")
      val expectedHumanName = Some("Leia Organa")
      val actualHumanName = root.data.human.name.string.getOption(parse(bodyText) getOrElse Json.Null)
      expectedHumanName should be(actualHumanName)
      And("The droid 2001 name should be R2-D2")
      val expectedDriodName = Some("R2-D2")
      val actualDroidName = root.data.droid.name.string.getOption(parse(bodyText) getOrElse Json.Null)
      expectedDriodName should be(actualDroidName)
    }

    "support basic queries with variables" in {
      Given("a Human by id query ")
      val humanbyidqry =
        """ query VariableExample($humanId: String!){
          |  human(id: $humanId) {
          |    name,
          |    homePlanet,
          |    friends {
          |      name
          |    }
          |  }
          |}""".stripMargin
      val graphqlvars =
        """ {
          |  "humanId": "1001"
          |}""".stripMargin
      val expectedJaon =
        """ {
          |  "data": {
          |    "human": {
          |      "name": "Darth Vader",
          |      "homePlanet": "Tatooine",
          |      "friends": [
          |        {
          |          "name": "Wilhuff Tarkin"
          |        }
          |      ]
          |    }
          |  }
          |}""".stripMargin

      When("Graphql query for HumanID 1001")
      val result: Future[Result] = controller.graphql(humanbyidqry, Some(graphqlvars), None).apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      Then("The human name has to be Darth Vader")
      val expectedHumanName = Some("Darth Vader")
      val actualHumanName = root.data.human.name.string.getOption(parse(bodyText) getOrElse Json.Null)
      expectedHumanName should be(actualHumanName)
    }
  }


  override def components: BuiltInComponents = new SWAPIComponents(context)

}
