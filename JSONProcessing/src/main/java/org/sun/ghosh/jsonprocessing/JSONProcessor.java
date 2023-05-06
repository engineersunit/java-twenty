package org.sun.ghosh.jsonprocessing;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import java.math.BigDecimal;

public class JSONProcessor {
    public static void main(String[] args) {

        JsonObject json = Json.createObjectBuilder()
                .add("name", "Falco")
                .add("age", BigDecimal.valueOf(3))
                .add("biteable", Boolean.FALSE).build();
        String result = json.toString();

        System.out.println(result);

        // Read back
        JsonReader jsonReader = Json.createReader(new StringReader("{\"name\":\"Falco\",\"age\":3,\"bitable\":false}"));
        JsonObject jobj = jsonReader.readObject();
        System.out.println(jobj);

    }
}