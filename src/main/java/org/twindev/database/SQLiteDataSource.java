package org.twindev.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLiteDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Database file created");
                } else {
                    LOGGER.error("Failed to create database file");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

        try(final Statement statement = getConnection().createStatement()) {

            // language=SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE)");

            LOGGER.info("Database initialized");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SQLiteDataSource() { }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static String setLicense(String username, String license, String plugin) {
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement()) {

            // Insert user into users table if not exists
            statement.execute("INSERT OR IGNORE INTO users (username) VALUES ('" + username + "')");

            // Create a user-specific table if it does not exist
            String userTableName = "user_" + username;
            statement.execute("CREATE TABLE IF NOT EXISTS " + userTableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, license TEXT, plugin TEXT)");

            // Insert the license and plugin into the user-specific table
            statement.execute("INSERT INTO " + userTableName + " (license, plugin) VALUES ('" + license + "', '" + plugin + "')");

            return "License set for " + username;
        } catch (SQLException e) {
            e.printStackTrace();
            return "An error occurred";
        }
    }

    public static String getUserLicenses(String username) {
        StringBuilder licenses = new StringBuilder("Licenses for " + username + ":\n");
        String userTableName = "user_" + username;

        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT license, plugin FROM " + userTableName);

            while (rs.next()) {
                String license = rs.getString("license");
                String plugin = rs.getString("plugin");
                licenses.append("License: ").append(license).append(" | Plugin: ").append(plugin).append("\n");
            }

            if (licenses.toString().equals("Licenses for " + username + ":\n")) {
                return "No licenses found for " + username;
            }

            return licenses.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "An error occurred while retrieving licenses for " + username;
        }
    }

    public static void deleteLicense(String deleteUser, String deletePlugin) {
        String userTableName = "user_" + deleteUser;

        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("DELETE FROM " + userTableName + " WHERE plugin = '" + deletePlugin + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkLicense(String license, String plugin) {
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                String userTableName = "user_" + rs.getString("username");
                ResultSet rs2 = statement.executeQuery("SELECT * FROM " + userTableName + " WHERE license = '" + license + "' AND plugin = '" + plugin + "'");

                if (rs2.next()) {
                    return true;
                }
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
