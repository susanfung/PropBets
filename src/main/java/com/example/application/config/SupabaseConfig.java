package com.example.application.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static final String SUPABASE_URL = getEnvVar("SUPABASE_URL", "");
    public static final String SUPABASE_API_KEY = getEnvVar("SUPABASE_API_KEY", "");

    private static String getEnvVar(String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value != null ? value : defaultValue;
    }
}
