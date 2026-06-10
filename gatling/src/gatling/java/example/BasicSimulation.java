package example;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BasicSimulation extends Simulation {
    private static final String BASE_URL = "http://localhost:8080";

    private static final Iterator<Map<String, Object>> pointFeeder = new Iterator<>() {
        private final Random rand = new Random();

        @Override
        public boolean hasNext() {
            return true;
        }

        private final double[] rValues = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};

        @Override
        public Map<String, Object> next() {
            return Map.of(
                    "x", rand.nextDouble() * 10 - 5,
                    "y", rand.nextDouble() * 10 - 5,
                    "r", rValues[rand.nextInt(rValues.length)],
                    "username", "test"
            );
        }
    };

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private final ScenarioBuilder scn = scenario("Load Test")
            .exec(http("Login Request")
                    .post("/auth/sign-in")
                    .body(StringBody("{\"username\": \"test\", \"password\": \"test\"}"))
                    .check(
                            jsonPath("$.token").saveAs("jwtToken")
                    )
            )
            .repeat(100).on(
                    feed(pointFeeder).exec(http("Submit Request")
                            .post("/home/submit")
                            .header("Authorization", session -> "Bearer " + session.getString("jwtToken"))
                            .body(StringBody("{\"x\": #{x}, \"y\": #{y}, \"r\": #{r}}"))
                    )
            );

    {
        setUp(scn.injectOpen(rampUsersPerSec(0).to(5).during(10), constantUsersPerSec(5).during(120)))
                .protocols(httpProtocol);
    }
}