package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

/**
 * Access layer for venue queries
 * All methods use stored procedures
 */
public class VenueRepository {

	/**
	 * Searches for venues matching the given name, type, and year range
	 *
	 * @param name partial or full venue name to match
	 * @param type {@code "journal"}, {@code "conference"}, or empty string for all
	 * @param fromYear start of the publication year range
	 * @param toYear end of the publication year range
	 * @param limit maximum number of results to return
	 * @return list of matching venues with type, rank, and publication count
	 */
	public List<VenueResult> searchVenues(String name, String type, int fromYear, int toYear) throws SQLException {
		List<VenueResult> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL search_venues_procedure(?,?,?,?)}")) {
			callableStatement.setString(1, name);
			callableStatement.setString(2, type);
			callableStatement.setInt(3, fromYear);
			callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new VenueResult(resultSet.getInt("venue_id"),
						resultSet.getString("title"),
						resultSet.getString("type"),
						resultSet.getString("rank"),
						resultSet.getInt("publication_count")));
			}
		}
		return results;
	}

	/**
	 * Returns per-year publication counts for a venue within a year range
	 *
	 * @param venueId the venue's DB id
	 * @param type venue type
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return list of yearly publication counts
	 */
	public List<VenueYearStat> getVenueYearStats(int venueId, String type, int fromYear, int toYear) throws SQLException {
		List<VenueYearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_year_stats_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId);
			callableStatement.setString(2, type);
			callableStatement.setInt(3, fromYear);
			callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new VenueYearStat(resultSet.getString("year"), resultSet.getInt("publication_count")));
			}
		}
		return results;
	}

	/**
	 * Returns total statistics for a venue within a year range
	 *
	 * @param venueId the venue's DB id
	 * @param type venue type
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return the venue's stats, or {@code null} if not found
	 */
	public VenueStats getVenueStats(int venueId, String type, int fromYear, int toYear) throws SQLException {
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_stats_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId);
			callableStatement.setString(2, type);
			callableStatement.setInt(3, fromYear);
			callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next()) {
				return new VenueStats(
						resultSet.getInt("first_year"), resultSet.getInt("last_year"),
						resultSet.getInt("total_publications"),
						resultSet.getInt("total_authors"), resultSet.getInt("distinct_authors"),
						resultSet.getDouble("avg_authors_per_article"), resultSet.getDouble("avg_articles_per_year")
				);
			}
		}
		return null;
	}

	/**
	 * Returns per-year publication and author counts for a venue within a year range
	 *
	 * @param venueId the venue's DB id
	 * @param type venue type
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return list of per-year detail records
	 */
	public List<VenueYearDetail> getVenueYearDetail(int venueId, String type, int fromYear, int toYear) throws SQLException {
		List<VenueYearDetail> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_year_detail_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId);
			callableStatement.setString(2, type);
			callableStatement.setInt(3, fromYear);
			callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new VenueYearDetail(resultSet.getString("year"),
						resultSet.getInt("publication_count"),
						resultSet.getInt("total_authors"),
						resultSet.getInt("distinct_authors")));
			}
		}
		return results;
	}

	/**
	 * Returns all publications for a venue within a year range
	 *
	 * @param venueId the venue's DB id
	 * @param type venue type
	 * @param fromYear start of the year range
	 * @param toYear end of the year range
	 * @return list of publications with title, year, and author count
	 */
	public List<VenuePublication> getVenuePublications(int venueId, String type, int fromYear, int toYear) throws SQLException {
		List<VenuePublication> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_publications_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId);
			callableStatement.setString(2, type);
			callableStatement.setInt(3, fromYear);
			callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new VenuePublication(resultSet.getInt("publication_id"),
						resultSet.getString("title"),
						resultSet.getString("year"),
						resultSet.getInt("author_count")));
			}
		}
		return results;
	}
}
