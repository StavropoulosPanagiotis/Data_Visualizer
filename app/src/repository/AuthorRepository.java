package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

/**
 * Access layer for author queries
 * All methods use stored procedures
 */
public class AuthorRepository {

	/**
	 * Searches for authors matching the given name within a year range
	 *
	 * @param name partial or full author name to match
	 * @param fromYear start of the publication year range
	 * @param toYear end of the publication year range
	 * @return list of matching authors with their publication counts
	 * @throws SQLException
	 */
	public List<AuthorResult> searchAuthors(String name, int fromYear, int toYear) throws SQLException {
		List<AuthorResult> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL search_authors_procedure(?,?,?)}")) {
			callableStatement.setString(1, name);
			callableStatement.setInt(2, fromYear);
			callableStatement.setInt(3, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new AuthorResult(resultSet.getInt("author_id"),
						resultSet.getString("author_name"),
						resultSet.getInt("publication_count")));
			}
		}
		return results;
	}

	/**
	 * Returns per-year publication breakdown for an author within a year range
	 *
	 * @param authorId the author's DB id
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return list of yearly stats ordered by year
	 * @throws SQLException
	 */
	public List<AuthorYearStat> getAuthorYearStats(int authorId, int fromYear, int toYear) throws SQLException {
		List<AuthorYearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL author_year_stats_procedure(?,?,?)}")) {
			callableStatement.setInt(1, authorId);
			callableStatement.setInt(2, fromYear);
			callableStatement.setInt(3, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()){
				results.add(new AuthorYearStat(resultSet.getString("year"),
						resultSet.getInt("publication_count"),
						resultSet.getInt("journal_count"),
						resultSet.getInt("conference_count")));
			}
		}
		return results;
	}

	/**
	 * Returns all publications for an author within a year range
	 *
	 * @param authorId the author's DB id
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return list of publications with title, year, type and venue
	 * @throws SQLException
	 */
	public List<AuthorPublication> getAuthorPublications(int authorId, int fromYear, int toYear) throws SQLException {
		List<AuthorPublication> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL author_publications_procedure(?,?,?)}")) {
			callableStatement.setInt(1, authorId);
			callableStatement.setInt(2, fromYear);
			callableStatement.setInt(3, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new AuthorPublication(resultSet.getInt("publication_id"),
						resultSet.getString("title"),
						resultSet.getString("year"),
						resultSet.getString("type"),
						resultSet.getString("venue")));
			}
		}
		return results;
	}

	/**
	 * Returns total statistics for an author
	 *
	 * @param authorId the author's DB id
	 * @return the author stats or {@code null} if not found
	 * @throws SQLException
	 */
	public AuthorStats getAuthorStats(int authorId) throws SQLException {
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL author_stats_procedure(?)}")) {
			callableStatement.setInt(1, authorId);
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next()) {
				return new AuthorStats(
						resultSet.getString("author_name"),
						resultSet.getInt("first_year"), resultSet.getInt("last_year"),
						resultSet.getInt("total_publications"),
						resultSet.getInt("journal_count"), resultSet.getInt("conf_count"),
						resultSet.getDouble("avg_per_year")
				);
			}
		}
		return null;
	}
}
