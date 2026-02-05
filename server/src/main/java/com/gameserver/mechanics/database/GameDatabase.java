package com.gameserver.mechanics.database;

import com.gameserver.config.AppConfig;
import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages access to the game database.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Initializing and managing the HikariCP connection pool</li>
 *   <li>Running database migrations via Flyway at startup</li>
 *   <li>Providing JDBC connections to the rest of the application</li>
 * </ul>
 * </p>
 *
 * <p>
 * The database is initialized eagerly at application startup and is expected
 * to live for the lifetime of the process. Shutdown must be handled explicitly
 * to ensure the connection pool is closed cleanly.
 * </p>
 */
@Slf4j
public final class GameDatabase {

    private final GameDatabaseConfig configuration;
    private HikariDataSource source;

    @Inject
    public GameDatabase(AppConfig config) {
        this.configuration = new GameDatabaseConfig(
            config.dbUrl(),
            config.dbUsername(),
            config.dbPassword(),
            config.dbMaxConnections()
        );
        init();
    }

    public boolean migrate(String... locations) {
        try {
            Flyway flyway = Flyway.configure().dataSource(
                "jdbc:postgresql://" + configuration.url(), configuration.username(), configuration.password())
                .locations(locations)
                .baselineOnMigrate(true)
                .load();

            flyway.migrate();
        } catch (Exception e) {
            log.error("Failed to migrate database", e);
            return false;
        }

        return true;
    }

    public void shutdown() {
        if (source == null) return;

        source.close();
        source = null;
        log.info("GameDatabase connection pool closed");
    }

    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    private void init() {
        HikariConfig hikariConfig = getHikariConfig();
        source = new HikariDataSource(hikariConfig);
    }

    private HikariConfig getHikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://" + configuration.url());
        hikariConfig.setUsername(configuration.username());
        hikariConfig.setPassword(configuration.password());
        hikariConfig.setMaximumPoolSize(Math.max(10, configuration.maxConnections()));
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("GameDatabase-Hikari");
        return hikariConfig;
    }

}
