package com.example.application.supabase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SupabaseService {
    private static final String SUPABASE_URL = Optional.ofNullable(System.getenv("SUPABASE_URL")).orElse("");
    private static final String SUPABASE_API_KEY = Optional.ofNullable(System.getenv("SUPABASE_API_KEY")).orElse("");
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String get(String table, String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(SUPABASE_URL + table + "?" + query))
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
                                         .uri(URI.create(SUPABASE_URL + table))
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
                                         .uri(URI.create(SUPABASE_URL + table + "?" + query))
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
                                         .uri(URI.create(SUPABASE_URL + table + "?" + query))
                                         .header("apikey", SUPABASE_API_KEY)
                                         .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
