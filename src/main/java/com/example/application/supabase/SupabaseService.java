package com.example.application.supabase;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.example.application.config.SupabaseConfig.SUPABASE_API_KEY;
import static com.example.application.config.SupabaseConfig.SUPABASE_URL;

@Service
public class SupabaseService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String buildRestUrl(String table) {
        return SUPABASE_URL + "/rest/v1/" + table;
    }

    public String get(String table, String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(buildRestUrl(table) + "?" + query))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .header("Accept", "application/json")
                                         .GET()
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String post(String table, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(buildRestUrl(table)))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String patch(String table, String query, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(buildRestUrl(table) + "?" + query))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .header("Content-Type", "application/json")
                                         .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String delete(String table, String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(buildRestUrl(table) + "?" + query))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String buildStorageUrl(String bucket, String fileName) {
        return SUPABASE_URL + "/storage/v1/object/" + bucket + "/" + fileName;
    }

    public String uploadFile(String bucket, String fileName, byte[] fileData, String contentType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(buildStorageUrl(bucket, fileName)))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .header("Content-Type", contentType)
                                         .header("x-upsert", "true")
                                         .POST(HttpRequest.BodyPublishers.ofByteArray(fileData))
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return getPublicUrl(bucket, fileName);
        } else {
            throw new RuntimeException("Failed to upload file: " + response.body());
        }
    }

    public void deleteFile(String bucket, String fileName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(buildStorageUrl(bucket, fileName)))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete file: " + response.body());
        }
    }

    public String getPublicUrl(String bucket, String fileName) {
        return SUPABASE_URL + "/storage/v1/object/public/" + bucket + "/" + fileName;
    }
}
