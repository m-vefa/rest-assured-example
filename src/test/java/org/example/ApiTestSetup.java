package org.example;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import static org.hamcrest.Matchers.lessThan;

public class ApiTestSetup {

    private static final String BASE_URI = "https://reqres.in/";
    private static final String CONTENT_TYPE = "application/json";
    private static final String ACCEPT_HEADER = "application/json";
    private static final long MAX_RESPONSE_TIME_MS = 10000L;

    @BeforeClass
    public void setup() {
        RestAssured.requestSpecification = createRequestSpecification();
        RestAssured.responseSpecification = createResponseSpecification();
    }

    private RequestSpecification createRequestSpecification() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", CONTENT_TYPE)
                .setBaseUri(BASE_URI)
                .addHeader("Accept", ACCEPT_HEADER)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    private ResponseSpecification createResponseSpecification() {
        return new ResponseSpecBuilder()
                .expectResponseTime(lessThan(MAX_RESPONSE_TIME_MS))
                .build();
    }
}
