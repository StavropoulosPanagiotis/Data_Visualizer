package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

/**
 * Access layer for year queries
 * All methods use stored procedures
 */
public class YearRepository {

	/**
	 * Returns per-year publication totals broken down by journals and conferences
	 *
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return list of yearly stats
	 */
	public List<YearStat> getPublicationsPerYear(int fromYear, int toYear) throws SQLException {
		List<YearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL publications_per_year_procedure(?,?)}")) {
			callableStatement.setInt(1, fromYear);
			callableStatement.setInt(2, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new YearStat(resultSet.getString("year"),
						resultSet.getInt("total"),
						resultSet.getInt("journal_count"),
						resultSet.getInt("conference_count")));
			}
		}
		return results;
	}

	/**
	 * Returns total publication and author statistics for a single year
	 *
	 * @param year the year to query
	 * @return the year's profile, or {@code null} if not found
	 */
	public YearProfile getYearProfile(int year) throws SQLException {
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL year_profile_procedure(?)}")) {
			callableStatement.setInt(1, year);
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next()) {
				return new YearProfile(
						resultSet.getInt("total_publications"),
						resultSet.getInt("distinct_journals"),
						resultSet.getInt("distinct_conferences"),
						resultSet.getInt("total_authors"),
						resultSet.getInt("distinct_authors")
				);
			}
		}
		return null;
	}

	/**
	 * Returns publications for a given year, optionally filtered by type, venue, and author
	 *
	 * @param year the year to query
	 * @param typeFilter {@code "journal"}, {@code "conference"}, or empty string for all
	 * @param venueName  partial venue name filter, or empty string to skip
	 * @param authorName partial author name filter, or empty string to skip
	 * @return list of matching publications
	 */
	public List<YearPublication> getYearPublications(int year, String typeFilter, String venueName, String authorName) throws SQLException {
		List<YearPublication> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL year_publications_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, year);
			callableStatement.setString(2, typeFilter);
			callableStatement.setString(3, venueName);
			callableStatement.setString(4, authorName);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new YearPublication(resultSet.getInt("publication_id"),
						resultSet.getString("title"),
						resultSet.getString("type"),
						resultSet.getString("venue")));
			}
		}
		return results;
	}
}
