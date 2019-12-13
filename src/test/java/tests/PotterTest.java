package tests;

import exceptions.CharacterNotFoundException;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import models.Spell;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Potter API")
@Feature("Spell service")
public class PotterTest {

    private static final String API_KEY = "$2a$10$f.wBgzvPPpAvJi0D1d1MOOC/uTEqHWqG6tGxfa/i2u.7ob7O9JGla";

    @BeforeClass
    public static void config() {
        RestAssured.baseURI = "https://www.potterapi.com/v1";
    }

    @Test
    @Description("Some detailed test description")
    @Severity(SeverityLevel.BLOCKER)
    public void spellServiceShouldBeAlive() {
        given()
            .queryParam("key", API_KEY)
            .basePath("/spells")
            .when().get()
            .then().statusCode(200)
            .and().contentType(JSON);
    }


    @Test
    @Severity(SeverityLevel.TRIVIAL)
    public void itShouldReturn409WhenKeyIsNotFound() {
        given().queryParam("key", "INVALID")
            .basePath("/spells")
            .when().get()
            .then().statusCode(401)
            .contentType(JSON)
            .body("error", is("API Key Not Found"));
    }

    @Test
    public void itShouldReturn409WhenKeyIsNotPresent() {
        given().basePath("/spells")
            .when().get()
            .then().statusCode(409)
            .contentType(JSON)
            .body("error", is("Must pass API key for request"));
    }

    @Test
    public void itShouldContainEffectForEachSpell() {
        List<String> spellEffects = given().basePath("/spells")
            .queryParam("key", API_KEY)
            .when().get()
            .then().extract().path("effect");
        spellEffects.forEach(s -> assertThat(s, not(emptyOrNullString())));
    }

    @Test
    public void itShouldContainExactNumberOfSpells() {
        given().basePath("/spells").queryParam("key", API_KEY)
            .when().get()
            .then().body("", hasSize(151));
    }


    @Test
    public void listOfSpellsShouldContainAvadaKedavra() {
        given()
            .basePath("/spells")
            .queryParam("key", API_KEY)
            .when().get()
            .then().body("spell", hasItem("Avada Kedavra"));
    }

    @Test
    public void itShouldTransformResponseToObject() {
        List<Spell> spellArray = Arrays.asList(given()
            .basePath("/spells")
            .queryParam("key", API_KEY)
            .when().get().then().extract().body().as(Spell[].class));
        spellArray.forEach(spell -> assertThat(spell.getEffect(), is(not(emptyOrNullString()))));
        spellArray.forEach(spell -> assertThat(spell.getSpell(), is(not(emptyOrNullString()))));
    }

    @Test
    public void listCharacters() throws CharacterNotFoundException {
        String characterToFind = "Albus Dumbledore";
        RestAssured.basePath = "/characters";

        List<HashMap<String, String>> characters =
            given().queryParam("key", API_KEY)
                .when().get()
                .then().extract().response().getBody().jsonPath().get();

        String characterId = characters
            .stream()
            .filter(character -> character.get("name").equals(characterToFind))
            .findFirst()
            .orElseThrow(() -> new CharacterNotFoundException(characterToFind))
            .get("_id");

        given().queryParam("key", API_KEY).pathParam("characterId", characterId)
            .when().get("/{characterId}")
            .then().body("deathEater", is(false));
    }

    @Test
    public void itShouldFindHarry() throws CharacterNotFoundException {
        String characterToFind = "Harry Potter";
        RestAssured.basePath = "/characters";

        List<HashMap<String, String>> characters =
            given().queryParam("key", API_KEY)
                .when().get()
                .then().extract().response().getBody().jsonPath().get();

        String characterId = characters
            .stream()
            .filter(character -> character.get("name").equals(characterToFind))
            .findFirst()
            .orElseThrow(() -> new CharacterNotFoundException(characterToFind))
            .get("_id");

        given().queryParam("key", API_KEY).pathParam("characterId", characterId)
            .when().get("/{characterId}")
            .then().body("deathEater", is(false));
    }

    @Test
    public void itShouldReturnDataMatchingTheSchema() {
        given()
            .basePath("/spells")
            .queryParam("key", API_KEY)
            .when().get()
            .then().body(matchesJsonSchema(new File("src/test/resources/schemas/spell_schema.json")));
    }
}
