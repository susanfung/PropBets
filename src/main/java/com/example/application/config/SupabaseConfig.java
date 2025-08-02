package com.example.application.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class SupabaseConfig {
    static Dotenv dotenv = Dotenv.load();

    public static final String SUPABASE_URL = Optional.ofNullable(dotenv.get("SUPABASE_URL")).orElse("");
    public static final String SUPABASE_API_KEY = Optional.ofNullable(dotenv.get("SUPABASE_API_KEY")).orElse("");
}
