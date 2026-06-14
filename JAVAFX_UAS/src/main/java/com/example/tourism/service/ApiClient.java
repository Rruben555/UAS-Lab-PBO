package com.example.tourism.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.example.tourism.model.dto.ApiResponseDTO;
import com.example.tourism.util.SessionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * API Client untuk komunikasi dengan backend Spring Boot
 */
@Slf4j
public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * GET request tanpa authentication
     */
    public static <T> T get(String endpoint, Type responseType) {
        return get(endpoint, responseType, false);
    }

    /**
     * GET request dengan opsional authentication
     */
    public static <T> T get(String endpoint, Type responseType, boolean requireAuth) {
        try {
            HttpGet request = new HttpGet(BASE_URL + endpoint);
            request.setHeader("Accept", "application/json");

            if (requireAuth && SessionManager.getInstance().isLoggedIn()) {
                request.setHeader("Authorization", "Bearer " + SessionManager.getInstance().getToken());

            }

            return HTTP_CLIENT.execute(request, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    if (response.getCode() >= 200 && response.getCode() < 300) {
                        return GSON.fromJson(result.toString(), responseType);
                    } else {
                        log.error("HTTP Error: {} - {}", response.getCode(), result);
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            log.error("GET request failed for endpoint: {}", endpoint, e);
            return null;
        }
    }

    /**
     * POST request tanpa authentication
     */
    public static <T> T post(String endpoint, Object body, Type responseType) {
        return post(endpoint, body, responseType, false);
    }

    /**
     * POST request dengan opsional authentication
     */
    public static <T> T post(String endpoint, Object body, Type responseType, boolean requireAuth) {
        try {
            HttpPost request = new HttpPost(BASE_URL + endpoint);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");

            if (requireAuth && SessionManager.getInstance().isLoggedIn()) {
                request.setHeader("Authorization", "Bearer " + SessionManager.getInstance().getToken());
            }

            String jsonBody = GSON.toJson(body);
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            return HTTP_CLIENT.execute(request, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    if (response.getCode() >= 200 && response.getCode() < 300) {
                        return GSON.fromJson(result.toString(), responseType);
                    } else {
                        log.error("HTTP Error: {} - {}", response.getCode(), result);
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            log.error("POST request failed for endpoint: {}", endpoint, e);
            return null;
        }
    }

    /**
     * PUT request dengan authentication
     */
    public static <T> T put(String endpoint, Object body, Type responseType) {
        try {
            HttpPut request = new HttpPut(BASE_URL + endpoint);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");

            if (SessionManager.getInstance().isLoggedIn()) {
                request.setHeader("Authorization", "Bearer " + SessionManager.getInstance().getToken());
            }

            String jsonBody = GSON.toJson(body);
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            return HTTP_CLIENT.execute(request, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    if (response.getCode() >= 200 && response.getCode() < 300) {
                        return GSON.fromJson(result.toString(), responseType);
                    } else {
                        log.error("HTTP Error: {} - {}", response.getCode(), result);
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            log.error("PUT request failed for endpoint: {}", endpoint, e);
            return null;
        }
    }

    /**
     * DELETE request dengan authentication
     */
    public static <T> T delete(String endpoint, Type responseType) {
        try {
            HttpDelete request = new HttpDelete(BASE_URL + endpoint);
            request.setHeader("Accept", "application/json");

            if (SessionManager.getInstance().isLoggedIn()) {
                request.setHeader("Authorization", "Bearer " + SessionManager.getInstance().getToken());
            }

            return HTTP_CLIENT.execute(request, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    if (response.getCode() >= 200 && response.getCode() < 300) {
                        return GSON.fromJson(result.toString(), responseType);
                    } else {
                        log.error("HTTP Error: {} - {}", response.getCode(), result);
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            log.error("DELETE request failed for endpoint: {}", endpoint, e);
            return null;
        }
    }

    public static String uploadImage(String endpoint, File file) {
        try {
            org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder builder =
                    org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file,
                    org.apache.hc.core5.http.ContentType.DEFAULT_BINARY, file.getName());

            HttpPost request = new HttpPost(BASE_URL + endpoint);
            request.setHeader("Accept", "application/json");

            if (SessionManager.getInstance().isLoggedIn()) {
                request.setHeader("Authorization", "Bearer " + SessionManager.getInstance().getToken());
            }

            request.setEntity(builder.build());

            return HTTP_CLIENT.execute(request, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);

                    if (response.getCode() >= 200 && response.getCode() < 300) {
                        log.info("Image uploaded: {}", result);
                        return result.toString();
                    } else {
                        log.error("Upload failed: {} - {}", response.getCode(), result);
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            log.error("Upload image failed", e);
            return null;
        }
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static Gson getGson() {
        return GSON;
    }
}