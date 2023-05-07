package org.sun.ghosh.jsonprocessing;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * <a href="https://www.oracle.com/technical-resources/articles/java/json.html">
 *     Java API for JSON Processing: An Introduction to JSON</a>
 */
public class JSONProcessor {
    public static void main(String[] args) {

        // Create a JSON from attribute names and values
        JsonObject profileJson = Json.createObjectBuilder()
                .add("name", "Engineer Sunit")
                .add("yearsOfExperience", BigDecimal.valueOf(15))
                .add("lookingForJobCurrently", Boolean.FALSE).build();
        String result = profileJson.toString();

        System.out.println(profileJson.getClass());
        System.out.println("Created a JSON from raw attribute names and values");
        System.out.println(result);

        // Create a JSON from a given String
        JsonReader jsonReader = Json.createReader(
                new StringReader(
                        "{\"name\":\"Sunit\"," +
                          "\"yearsOfExperience\":15," +
                          "\"lookingForJobCurrently\":false}"));
        profileJson = jsonReader.readObject();
        System.out.println(jsonReader.getClass());
        System.out.println("Created a JSON from a given String");
        System.out.println(profileJson);


        // Create a JSON from a given File's content
        final String fileName =
                "JSONProcessing/src/main/java/org/sun/ghosh/" +
                        "jsonprocessing/MyProfile.json";
        try (JsonReader jsonFileReader = Json.createReader(
                Files.newBufferedReader(Paths.get(fileName)))){
            profileJson = jsonFileReader.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Created a JSON from a given File's content");
        System.out.println(profileJson);

    }
}