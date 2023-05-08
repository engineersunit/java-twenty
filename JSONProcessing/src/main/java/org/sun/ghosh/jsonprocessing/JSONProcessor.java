package org.sun.ghosh.jsonprocessing;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;


/**
 * <a href="https://www.oracle.com/technical-resources/articles/java/json.html">
 * Java API for JSON Processing: An Introduction to JSON</a>
 */
public class JSONProcessor {

    private final static String OUTPUT_FILE_NAME =
            "JSONProcessing/src/main/java/org/sun/ghosh/" +
                    "jsonprocessing/MyData.csv";
    private final static String INPUT_FILE_NAME =
            "JSONProcessing/src/main/java/org/sun/ghosh/" +
                    "jsonprocessing/MyData.json";
    private final static String ATTRIBUTE_NAME_ARRAY = "attributes";
    private final static String ATTRIBUTE_NAME_LOGIC_1 = "kind";
    private final static String VALUE_LOGIC_1 = "SENSOR";
    private final static String ATTRIBUTE_NAME_LOGIC_2 = "dataType";
    private final static String VALUE_LOGIC_2 = "INTEGER";
    private final static String ATTRIBUTE_NAME_1 = "name";
    private final static String ATTRIBUTE_NAME_2 = "dataType";

    public static void main(String[] args) {
        /*
        Object Model APIs

        It is a high-level API that provides immutable object models
        for JSON object and array structures.

        These JSON structures are represented as object models using
        the Java types JsonObject and JsonArray.
        */

        // Create a JSON from attribute names and values
        createJSONFromAttributeNamesValues();

        // Create a JSON from a given String
        createJSONFromString();

        // Create a JSON from a given File's content
        createJSONFromFile();

        // Query keys and values from a given JSON File's content and
        // create a CSV output file
        transformJSONFileToCSV();

        /*
        The Streaming APIs
        The streaming API is similar to the Streaming API for XML (StAX) and
        consists of the interfaces JsonParser and JsonGenerator.
        */

        /*
        JsonParser contains methods to parse JSON data using the streaming model.
         */

        /*try (InputStream is = Files.newInputStream(Paths.get(FILE_NAME));
             JsonParser parser = Json.createParser(is)) {
            while (parser.hasNext()) {
                Event e = parser.next();
                if (e == Event.KEY_NAME) {
                    switch (parser.getString()) {
                        case ATTRIBUTE_NAME_1:
                            parser.next();
                            System.out.print(parser.getString());
                            System.out.print(": ");
                            break;
                        case ATTRIBUTE_NAME_2:
                            parser.next();
                            System.out.println(parser.getString());
                            System.out.println("---------");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */


        /*
        JsonGenerator contains methods to write JSON data to an output source.
         */
    }

    private static void transformJSONFileToCSV() {
        try (InputStream is = Files.newInputStream(Paths.get(INPUT_FILE_NAME));
             JsonReader rdr = Json.createReader(is);
             BufferedWriter bw = Files.newBufferedWriter(Paths.get(OUTPUT_FILE_NAME), CREATE, TRUNCATE_EXISTING)) {

            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray(ATTRIBUTE_NAME_ARRAY);
            int countOfOutputAttributes = 0;
            for (JsonObject result : results.getValuesAs(JsonObject.class)) {
                // Create a csv output
                if (VALUE_LOGIC_1.equals(
                        result.getString(
                                ATTRIBUTE_NAME_LOGIC_1)) &&
                        VALUE_LOGIC_2.equals(
                                result.getString(
                                        ATTRIBUTE_NAME_LOGIC_2))) {
                    String toOutput = String.format(
                            "%s,%s\n",
                            result.getString(ATTRIBUTE_NAME_1),
                            result.getString(ATTRIBUTE_NAME_2));
                    bw.write(toOutput);
                    System.out.print(toOutput);
                    countOfOutputAttributes++;
                }
            }
            System.out.println(String.format("%d attributes were generated.", countOfOutputAttributes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject createJSONFromFile() {
        JsonObject profileJson;
        // Create a JSON from a given File's content

        try (JsonReader jsonFileReader = Json.createReader(
                Files.newBufferedReader(Paths.get(INPUT_FILE_NAME)))) {
            profileJson = jsonFileReader.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Created a JSON from a given File's content");
        System.out.println(profileJson);

        return profileJson;
    }

    private static JsonObject createJSONFromAttributeNamesValues() {
        JsonObject profileJson = Json.createObjectBuilder()
                .add("name", "Engineer Sunit")
                .add("yearsOfExperience", BigDecimal.valueOf(15))
                .add("lookingForJobCurrently", Boolean.FALSE).build();
        String result = profileJson.toString();

        System.out.println(profileJson.getClass());
        System.out.println("Created a JSON from raw attribute names and values");
        System.out.println(result);

        return profileJson;
    }

    private static JsonObject createJSONFromString() {
        JsonObject profileJson;
        try (JsonReader jsonReader = Json.createReader(
                new StringReader(
                        "{\"name\":\"Sunit\"," +
                                "\"yearsOfExperience\":15," +
                                "\"lookingForJobCurrently\":false}"))) {
            profileJson = jsonReader.readObject();

            System.out.println(jsonReader.getClass());
            System.out.println("Created a JSON from a given String");
            System.out.println(profileJson);
        }
        return profileJson;
    }
}