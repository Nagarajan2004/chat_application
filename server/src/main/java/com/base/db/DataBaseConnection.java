package com.base.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class provides a method to establish a connection to the database.
 */
public class DataBaseConnection {
    // Database URL
    private static final String URL = "jdbc:mysql://localhost:3306/chat";
    // Database username
    private static final String USERNAME = "root";
    // Database password
    private static final String PASSWORD = "root"; // change the password

    // Connection object
    private static Connection connection;

    /**
     * Returns a Connection object to the database.
     * If a connection already exists, it returns the existing connection.
     * Otherwise, it establishes a new connection.
     *
     * @return Connection object to the database
     * @throws Exception if a database access error {@linkplain java.sql.SQLException} occurs or the driver class is not found {@linkplain ClassNotFoundException}
     */
    public static Connection getConnection() throws Exception {
//        if (connection != null) return connection;
        Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
        return DriverManager.getConnection(URL, USERNAME, PASSWORD); // Establish connection
    }
}