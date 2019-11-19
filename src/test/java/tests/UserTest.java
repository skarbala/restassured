package tests;

import io.restassured.RestAssured;
import models.UserToAdd;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;

public class UserTest {

    @BeforeClass
    public static void configuration() {
        RestAssured.baseURI = "https://reqres.in/api/users/";
    }

    @Test
    public void itShouldListAllTheUsers() {
        given().baseUri("https://reqres.in/api/users/")
            .when().get()
            .then().statusCode(200)
            .and().contentType(JSON);
    }

    @Test
    public void itShouldContainEmailForEachUser() {
        List<String> emails = when().get().then().extract().path("data.email");
        assertThat(emails).isNotEmpty();
        emails.forEach(email -> assertThat(email).isNotEmpty().isNotBlank());
    }

    @Test
    public void itShouldContainFirstNameForEachUser() {
        List<String> firstNames = when().get().then().extract().path("data.first_name");
        assertThat(firstNames).isNotEmpty();
        firstNames.forEach(firstName -> assertThat(firstName).isNotEmpty().isNotBlank());
    }

    @Test
    public void itShouldReturnPageNumber() {
        when().get().then().body("page", is(1));
    }

    @Test
    public void itShouldReturnId() {
        when().get().then().body("data.id[0]", instanceOf(Integer.class));
    }

    @Test
    public void itShouldReturnNumberOfPages() {
        when().get().then().body("total", greaterThan(0));
    }

    @Test
    public void itShouldReturn404WhenUserNotFound() {
        int invalidUserId = 23;
        given().pathParam("userId", invalidUserId).when().get("/{userId}").then().statusCode(404);
    }

    @Test
    public void itShouldAddNewUser() {
        UserToAdd userToAdd = new UserToAdd("martin", "tester");
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("name", "martin");
//        payload.put("job", "tester");
        given().contentType(JSON).body(userToAdd)
            .when().post()
            .then().body("id", is(not(empty())));
    }
}
