package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

public class VenueRepository {

	public List<VenueResult> searchVenues(String name, String type, int fromYear, int toYear, int limit) throws SQLException {
		List<VenueResult> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL search_venues_procedure(?,?,?,?,?)}")) {
			callableStatement.setString(1, name); callableStatement.setString(2, type); callableStatement.setInt(3, fromYear); callableStatement.setInt(4, toYear); callableStatement.setInt(5, limit);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new VenueResult(resultSet.getInt("venue_id"), resultSet.getString("title"), resultSet.getString("type"), resultSet.getString("rank"), resultSet.getInt("publication_count")));
		}
		return results;
	}

	public List<VenueYearStat> getVenueYearStats(int venueId, String type, int fromYear, int toYear) throws SQLException {
		List<VenueYearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_year_stats_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId); callableStatement.setString(2, type); callableStatement.setInt(3, fromYear); callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new VenueYearStat(resultSet.getString("year"), resultSet.getInt("publication_count")));
		}
		return results;
	}

	public VenueStats getVenueStats(int venueId, String type, int fromYear, int toYear) throws SQLException {
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_stats_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId); callableStatement.setString(2, type); callableStatement.setInt(3, fromYear); callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next())
				return new VenueStats(
					resultSet.getInt("first_year"), resultSet.getInt("last_year"),
					resultSet.getInt("total_publications"),
					resultSet.getInt("total_authors"), resultSet.getInt("distinct_authors"),
					resultSet.getDouble("avg_authors_per_article"), resultSet.getDouble("avg_articles_per_year")
				);
		}
		return null;
	}

	public List<VenueYearDetail> getVenueYearDetail(int venueId, String type, int fromYear, int toYear) throws SQLException {
		List<VenueYearDetail> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_year_detail_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId); callableStatement.setString(2, type); callableStatement.setInt(3, fromYear); callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new VenueYearDetail(resultSet.getString("year"), resultSet.getInt("publication_count"), resultSet.getInt("total_authors"), resultSet.getInt("distinct_authors")));
		}
		return results;
	}

	public List<VenuePublication> getVenuePublications(int venueId, String type, int fromYear, int toYear) throws SQLException {
		List<VenuePublication> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL venue_publications_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, venueId); callableStatement.setString(2, type); callableStatement.setInt(3, fromYear); callableStatement.setInt(4, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new VenuePublication(resultSet.getInt("publication_id"), resultSet.getString("title"), resultSet.getString("year"), resultSet.getInt("author_count")));
		}
		return results;
	}
}
