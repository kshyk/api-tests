package restfulbooker;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import restfulbooker.core.Tokenizer;

import java.util.Map;

import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assumptions.assumeThat;
import static restfulbooker.core.EnvHolder.RESTFUL_BOOKER_PASSWORD;
import static restfulbooker.core.EnvHolder.RESTFUL_BOOKER_USERNAME;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class TestBase {
    protected Tokenizer tokenizer;

    @BeforeAll
    protected void healthCheckAndTokenSetup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        var healthCheck = RestAssured.given().get("/ping");
        assumeThat(healthCheck.statusCode())
            .as("`%s` is OFFLINE. Skipping tests...", RestAssured.baseURI)
            .isEqualTo(SC_CREATED);
        tokenizer = Tokenizer.INSTANCE;
        var body = Map.of(
            "username", RESTFUL_BOOKER_USERNAME,
            "password", RESTFUL_BOOKER_PASSWORD
        );
        var authResponse = RestAssured.given().contentType(JSON).body(body).post("/auth");
        assumeThat(authResponse.statusCode())
            .as("Unable to create token. Skipping tests...")
            .isEqualTo(SC_OK);
        tokenizer.setToken(authResponse.jsonPath().getString("token"));
    }
}
