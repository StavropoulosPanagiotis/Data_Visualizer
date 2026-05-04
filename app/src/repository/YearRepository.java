package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

public class YearRepository {

	public List<YearStat> getPublicationsPerYear(int fromYear, int toYear) throws SQLException {
		List<YearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL publications_per_year_procedure(?,?)}")) {
			callableStatement.setInt(1, fromYear); callableStatement.setInt(2, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new YearStat(resultSet.getString("year"), resultSet.getInt("total"), resultSet.getInt("journal_count"), resultSet.getInt("conference_count")));
		}
		return results;
	}

	public YearProfile getYearProfile(int year) throws SQLException {
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL year_profile_procedure(?)}")) {
			callableStatement.setInt(1, year);
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next())
				return new YearProfile(
					resultSet.getInt("total_publications"),
					resultSet.getInt("distinct_journals"),
					resultSet.getInt("distinct_conferences"),
					resultSet.getInt("total_authors"),
					resultSet.getInt("distinct_authors")
				);
		}
		return null;
	}

	public List<YearPublication> getYearPublications(int year, String typeFilter, String venueName, String authorName) throws SQLException {
		List<YearPublication> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL year_publications_procedure(?,?,?,?)}")) {
			callableStatement.setInt(1, year); callableStatement.setString(2, typeFilter); callableStatement.setString(3, venueName); callableStatement.setString(4, authorName);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new YearPublication(resultSet.getInt("publication_id"), resultSet.getString("title"), resultSet.getString("type"), resultSet.getString("venue")));
		}
		return results;
	}
}
