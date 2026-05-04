package db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages a single shared JDBC connection to the MySQL database.
 * Connection parameters are loaded from {@code db.ini} on the classpath.
 */
public class DBConnection {

	private static Connection connection;

	/**
	 * Loads and registers the MySQL JDBC driver.
	 * Must be called once before any call to {@link #get()}.
	 *
	 * @throws SQLException if the MySQL driver class cannot be found
	 */
	public static void init() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("MySQL driver not found.", e);
		}
	}

	/**
	 * Returns the shared database connection, creating it if necessary.
	 * Reads {@code db.url}, {@code db.user}, and {@code db.password} from {@code db.ini}.
	 *
	 * @return an open {@link Connection} to the database
	 * @throws SQLException if the connection cannot be established
	 */
	public static synchronized Connection get() throws SQLException {
		if (connection == null || connection.isClosed()) {
			try {
				Properties props = new Properties();
				InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.ini");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder filtered = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.trim().startsWith("[")) filtered.append(line).append("\n");
				}
				props.load(new java.io.StringReader(filtered.toString()));
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

	/**
	 * Closes the shared database connection if it is open.
	 */
	public static void close() {
		if (connection != null) {
			try { connection.close(); } catch (SQLException ignored) {}
		}
	}
}
