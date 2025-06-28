package com.example.application.config;

import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class SupabaseConfig {
    public static final String SUPABASE_URL = Optional.ofNullable(System.getenv("SUPABASE_URL")).orElse("");
    public static final String SUPABASE_API_KEY = Optional.ofNullable(System.getenv("SUPABASE_API_KEY")).orElse("");
}
