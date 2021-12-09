import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.config.GatlingConfiguration
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._ 
import scala.language.postfixOps
import scala.util.Random
import java.util.UUID

class RequestSimulationTemplate extends Simulation {
  def generatePersonId() = Random.nextInt(Integer.MAX_VALUE)
  def generateRequestId() = UUID.randomUUID().toString()

  val config = ConfigFactory.load()
  val aUsers = config.getInt("usersCount")
  val aDuring = config.getInt("userDurationInSeconds")
  val baseUrl = config.getString("baseUrl")

  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
 
  val scn = scenario("Request sign documents")
    .exec(_.setAll(("requestId", generateRequestId()),("personId", generatePersonId())))
    .exec(http("sign document")
        .put("/api//documents/signatures")
        .header("content-type", "application/json")
        .body(PebbleFileBody("RequestSimulationTemplate.json"))
    )
    .pause(1)
    

  setUp(scn.inject(rampUsers(aUsers).during(aDuring.seconds)).protocols(httpProtocol))

}