package com.gameserver.mechanics.database;

/**
 * Immutable configuration object for the game database.
 * Contains URL, credentials, and maximum connection pool size.
 */
public record GameDatabaseConfig(
        String url,
        String username,
        String password,
        int maxConnections
) {
    public GameDatabaseConfig {
        if (maxConnections <= 0)
            maxConnections = 10;
    }
}
