package com.example.application.security;

import com.example.application.supabase.SupabaseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class UserService {
    public static final String USER_PROFILE_TABLE = "user_profile";
    private static final String PROFILE_IMAGES_BUCKET = "profile-images";
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
        updateUser(username, name, profileImage, null);
    }

    public void updateUser(String username, String name, byte[] profileImage, String contentType) {
        try {
            JSONObject update = new JSONObject();

            if (name != null) {
                update.put("name", name);
            }

            if (profileImage != null) {
                JSONObject currentUser = findUserByUsername(username);
                if (currentUser != null && currentUser.has("image_url")) {
                    String oldImageUrl = currentUser.optString("image_url");

                    if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                        try {
                            String fileName = extractFileNameFromUrl(oldImageUrl);

                            if (fileName != null) {
                                supabaseService.deleteFile(PROFILE_IMAGES_BUCKET, fileName);
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to delete old profile image: " + e.getMessage());
                        }
                    }
                }

                String fileName = username + "_profile_" + System.currentTimeMillis() + getFileExtension(contentType);
                String imageUrl = supabaseService.uploadFile(PROFILE_IMAGES_BUCKET, fileName, profileImage, contentType != null ? contentType : "image/jpeg");
                update.put("image_url", imageUrl);
            }

            if (update.length() == 0) {
                return;
            }

            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            supabaseService.patch(USER_PROFILE_TABLE, "username=eq." + encodedUsername, update.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Supabase", e);
        }
    }

    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;

        String[] parts = url.split("/");

        return parts.length > 0 ? parts[parts.length - 1] : null;
    }

    private String getFileExtension(String contentType) {
        if (contentType == null) return ".jpg";

        switch (contentType.toLowerCase()) {
            case "image/png": return ".png";
            case "image/gif": return ".gif";
            case "image/webp": return ".webp";
            case "image/heic": return ".heic";
            case "image/jpeg":
            case "image/jpg":
            default: return ".jpg";
        }
    }

    public JSONObject findUserByUsername(String username) {
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            String response = supabaseService.get(USER_PROFILE_TABLE, "username=ilike." + encodedUsername);
            JSONArray arr = new JSONArray(response);

            return arr.length() > 0 ? arr.getJSONObject(0) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by username in Supabase", e);
        }
    }
}
