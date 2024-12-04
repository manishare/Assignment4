package assignment4;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * API Testing Class
 * This class contains temporary solutions and enhanced tests for API endpoints.
 * It documents technical debt and provides a foundation for future improvements.
 */
public class ApiTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * Test a GET request to verify the status code.
     * Temporary solution: Only verifying the status code for now.
     * Future plans: Add assertions for response body and handle different scenarios.
     */
    @Test
    public void testGetRequest() {
        Response response = RestAssured.get(BASE_URL + "/posts/1");
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.getBody());
        Assert.assertTrue(response.getBody().asString().contains("userId"));
        Assert.assertEquals(response.getContentType(), "application/json; charset=utf-8");
    }

    /**
     * Test a GET request to fetch all posts.
     * Enhanced to include validation for response size and ensure data exists.
     */
    @Test
    public void testAllPosts() {
        Response response = RestAssured.get(BASE_URL + "/posts");
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getBody().jsonPath().getList("$").size() > 0);
        // Additional checks on the response body can be added here.
    }

    /**
     * Test a POST request to create a new post.
     * Known issue: Endpoint occasionally fails under heavy load.
     * Temporary implementation lacks detailed error handling.
     */
    @Test
    public void testPostRequest() {
        // Perform POST request
        Response response = RestAssured.given()
            .contentType("application/json")
            .body("{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }")
            .post(BASE_URL + "/posts");

        // Assert POST response
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertNotNull(response.getBody());
        Assert.assertTrue(response.getBody().asString().contains("\"id\":"));

        // Verify the created post details from the POST response
        int postId = response.jsonPath().getInt("id");
        System.out.println("Created Post ID: " + postId);

        // Attempt to fetch the created post
        Response getResponse = RestAssured.get(BASE_URL + "/posts/" + postId);

        // Handle cases where the API doesn't persist data
        if (getResponse.getStatusCode() == 404) {
            System.out.println("Warning: The post was not found. This is likely due to a mock API limitation.");
        } else {
            Assert.assertEquals(getResponse.getStatusCode(), 200);
            Assert.assertTrue(getResponse.getBody().asString().contains("\"id\":" + postId));
        }
    }

}
