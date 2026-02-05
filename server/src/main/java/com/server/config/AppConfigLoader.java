package com.server.config;

import com.server.mechanics.GameModule;

import java.io.InputStream;
import java.util.Properties;

public final class AppConfigLoader {

    private AppConfigLoader() {}

    public static AppConfig load() {

        Properties p = new Properties();

        try (InputStream in =
                 GameModule.class
                     .getClassLoader()
                     .getResourceAsStream("application.properties")) {

            if (in == null)
                throw new IllegalStateException("application.properties not found");

            p.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cli.config", e);
        }

        int instances =
            "auto".equalsIgnoreCase(p.getProperty("http.instances", "auto"))
                ? Runtime.getRuntime().availableProcessors()
                : Integer.parseInt(p.getProperty("http.instances"));

        return new AppConfig(
                Integer.parseInt(p.getProperty("http.port", "3000")),
                instances,
                p.getProperty("db.url"),
                p.getProperty("db.username"),
                p.getProperty("db.password"),
                Integer.parseInt(p.getProperty("db.maxConnections", "10")),
                p.getProperty("flyway.locations"),
                Long.parseLong(p.getProperty("vertx.blockedThreadCheckIntervalMs", "1000")),
                Long.parseLong(p.getProperty("vertx.maxEventLoopExecuteTimeMs", "500"))
        );
    }

}
