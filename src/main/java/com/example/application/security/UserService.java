package com.example.application.security;

import com.example.application.supabase.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final SupabaseService supabaseService;

    @Autowired
    public UserService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public void saveUser(String username) {
        String jsonBody = String.format("{\"username\":\"%s\",\"role\":\"user\"}", username);
        try {
            supabaseService.post("Users", jsonBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user to Supabase", e);
        }
    }

    public void updateUser(String username, String firstName, String lastName, byte[] profileImage) {
        try {
            org.json.JSONObject update = new org.json.JSONObject();
            if (firstName != null) update.put("firstName", firstName);
            if (lastName != null) update.put("lastName", lastName);
            if (profileImage != null) update.put("profileImage", java.util.Base64.getEncoder().encodeToString(profileImage));
            if (update.length() == 0) return;
            supabaseService.patch("Users", "username=eq." + username, update.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Supabase", e);
        }
    }

    public org.json.JSONObject findUserByUsername(String username) {
        try {
            String response = supabaseService.get("Users", "username=eq." + username);
            org.json.JSONArray arr = new org.json.JSONArray(response);
            return arr.length() > 0 ? arr.getJSONObject(0) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by username in Supabase", e);
        }
    }
}
