package com.example.application;

import org.jooq.SQLDialect;

import com.example.application.security.SecurityFilter;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;
import org.jooq.impl.DSL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "propbets")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public VaadinServiceInitListener securityFilter() {
        return new SecurityFilter();
    }

    @Bean
    public DSLContext dslContext() throws Exception {
        String url = System.getenv("SUPABASE_JDBC_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        Connection conn = DriverManager.getConnection(url, user, password);

        return DSL.using(conn, SQLDialect.POSTGRES);
    }
}
