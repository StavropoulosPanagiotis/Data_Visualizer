package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages a JDBC connection to the MySQL database
 * Connection parameters are loaded from {@code db.ini}
 */
public class DBConnection {

	private static Connection connection;

	public static void init() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("MySQL driver not found.", e);
		}
	}

	public static synchronized Connection get() throws SQLException {
		if (connection == null || connection.isClosed()) {
			try {
				Properties props = new Properties();
				InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.ini");
				props.load(in);
				connection = DriverManager.getConnection(
					props.getProperty("db.url"),
					props.getProperty("db.user"),
					props.getProperty("db.password")
				);
			} catch (Exception e) {
				throw new SQLException("Could not connect to database: " + e.getMessage(), e);
			}
		}
		return connection;
	}

	public static void close() {
		if (connection != null) {
			try { connection.close(); } catch (SQLException ignored) {}
		}
	}
}
