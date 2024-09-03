package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserListTests extends ApiTestSetup {

    private Response response;

    @BeforeClass
    public void setupResponse() {
        response = RestAssured.get("/api/users?page=2");
    }

    @Test
    public void shouldReturnStatusCode200() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be " + 200);
    }

    @Test
    public void shouldReturnContentTypeAsJson() {
        Assert.assertEquals(response.getContentType(), "application/json; charset=utf-8",
                "Content-Type should be " + "application/json; charset=utf-8");
    }

    @Test
    public void shouldHaveValidResponseBodyStructure() {
        Assert.assertFalse(response.jsonPath().getList("data").isEmpty(), "Response body should contain " + "data");
        Assert.assertNotNull(response.jsonPath().get("support"), "Response should contain " + "support");
    }

    private void assertJsonValue(String jsonPath, Object expectedValue) {
        Assert.assertEquals(response.jsonPath().get(jsonPath), expectedValue, jsonPath + " should be " + expectedValue);
    }

    @Test
    public void shouldReturnCorrectPaginationDetails() {
        assertJsonValue("page", 2);
        assertJsonValue("per_page", 6);
        assertJsonValue("total", 12);
        assertJsonValue("total_pages", 2);
    }

    @Test
    public void shouldValidateUserDataStructure() {
        List<Map<String, ?>> users = response.jsonPath().getList("data");
        users.forEach(this::assertValidUser);
    }

    private void assertValidUser(Map<String, ?> user) {
        List<String> requiredFields = Arrays.asList("id", "email", "first_name", "last_name", "avatar");
        requiredFields.forEach(field -> Assert.assertNotNull(user.get(field), "User " + field + " should not be null"));
    }

    @Test
    public void shouldMatchUserEmails() {
        List<String> expectedEmails = Arrays.asList(
                "michael.lawson@reqres.in",
                "lindsay.ferguson@reqres.in",
                "tobias.funke@reqres.in",
                "byron.fields@reqres.in",
                "george.edwards@reqres.in",
                "rachel.howell@reqres.in"
        );
        List<String> actualList = response.jsonPath().getList("data.email");
        Assert.assertEquals(actualList, expectedEmails, "data.email" + " should match the expected values");
    }

    @Test
    public void shouldHaveValidUserAvatars() {
        List<String> avatars = response.jsonPath().getList("data.avatar");
        avatars.forEach(avatar -> Assert.assertTrue(avatar.startsWith("https://reqres.in/img/faces/"), "Avatar URL should be valid"));
    }

    @Test
    public void shouldReturnCorrectSupportDetails() {
        assertJsonValue("support.url", "https://reqres.in/#support-heading");
        assertJsonValue("support.text", "To keep ReqRes free, contributions towards server costs are appreciated!");
    }

    @Test
    public void shouldReturnResponseWithin2Seconds() {
        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < (long) 2000, "Response time should be less than " + (long) 2000 + " ms");
    }

}
