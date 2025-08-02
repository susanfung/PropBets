package com.example.application.security;

import com.example.application.supabase.SupabaseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final String USER_PROFILE_TABLE = "user_profile";
    private final SupabaseService supabaseService;

    @Autowired
    public UserService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public void saveUser(String username) {
        String jsonBody = String.format("{\"username\":\"%s\",\"role\":\"user\"}", username);
        try {
            supabaseService.post(USER_PROFILE_TABLE, jsonBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user to Supabase", e);
        }
    }

    public void updateUser(String username, String name, byte[] profileImage) {
        try {
            JSONObject update = new JSONObject();
            if (name != null) {
                update.put("name", name);
            }
            if (profileImage != null) {
                update.put("profileImage", java.util.Base64.getEncoder().encodeToString(profileImage));
            }
            if (update.length() == 0) {
                return;
            }
            supabaseService.patch(USER_PROFILE_TABLE, "username=eq." + username, update.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Supabase", e);
        }
    }

    public JSONObject findUserByUsername(String username) {
        try {
            String response = supabaseService.get(USER_PROFILE_TABLE, "username=ilike." + username);
            JSONArray arr = new JSONArray(response);
            return arr.length() > 0 ? arr.getJSONObject(0) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by username in Supabase", e);
        }
    }
}
