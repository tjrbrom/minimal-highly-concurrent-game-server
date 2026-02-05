package com.server.config;

/**
 * Root application.properties configuration.
 * Loaded once at startup and injected where needed.
 */
public record AppConfig(
        int httpPort,
        int httpInstances,
        String dbUrl,
        String dbUsername,
        String dbPassword,
        int dbMaxConnections,
        String flywayLocations,
        long vertxBlockedThreadCheckIntervalMs,
        long vertxMaxEventLoopExecuteTimeMs
) {}
