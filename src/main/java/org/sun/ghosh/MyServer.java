package org.sun.ghosh;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class MyServer {

    private static final int HTTP_SERVER_PORT = 65353;
    private static final String MY_REST_API_PATH =
            "/sunghosh/restapi/resources/latest/";
    private static final String MY_PROFILE_RESOURCE_PATH =
            "profile";
    private static HttpServer myServer;

    public static void main(String[] args) {
        try {
            myServer = HttpServer.create(
                    new InetSocketAddress(
                            HTTP_SERVER_PORT),
                    0);
            HttpContext profileContext = myServer
                    .createContext(
                            MY_REST_API_PATH + MY_PROFILE_RESOURCE_PATH);
            profileContext.setHandler(
                    MyServer::handleProfileRequests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        myServer.start();
    }

    private static void handleProfileRequests(HttpExchange exchange) throws IOException {
        String response;
        int responseCode = 200;
        switch (exchange.getRequestMethod().toUpperCase()) {
            case "GET" -> {
                System.out.println("GET HTTP Method call is made");
                JsonObject profileJson = getMyProfileJson();
                response = profileJson.toString();
            }
            case "POST" -> {
                System.out.println("POST HTTP Method call is made");
                InputStream inputStream = exchange.getRequestBody();
                String requestPayload = new BufferedReader(
                        new InputStreamReader(inputStream))
                        .lines().collect(
                                Collectors.joining("\n"));
                System.out.println("Request Payload:\n" + requestPayload);
                JsonObject requestProfile = Json.createReader(
                        new StringReader(requestPayload))
                        .readObject();
                JsonObject profileJson = Json.createObjectBuilder(requestProfile)
                        .add("id", 1)
                        .build();
                response = profileJson.toString();
                System.out.println("Response Payload:\n" + response);
            }

            default -> {
                response =
                        "Unexpected HTTP method invoked: " +
                                exchange.getRequestMethod();
                responseCode = 400;
            }
        }

        exchange.sendResponseHeaders(responseCode,
                response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private static JsonObject getMyProfileJson() {
        JsonObject profileJson = Json.createObjectBuilder()
                .add("name", "Engineer Sunit")
                .add("yearsOfExperience", BigDecimal.valueOf(15))
                .add("lookingForJobCurrently", Boolean.FALSE).build();
        return profileJson;
    }
}
