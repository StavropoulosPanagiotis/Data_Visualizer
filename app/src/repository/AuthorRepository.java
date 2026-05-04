package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

public class AuthorRepository {

	public List<AuthorResult> searchAuthors(String name, int fromYear, int toYear, int limit) throws SQLException {
		List<AuthorResult> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL search_authors_procedure(?,?,?,?)}")) {
			callableStatement.setString(1, name); callableStatement.setInt(2, fromYear); callableStatement.setInt(3, toYear); callableStatement.setInt(4, limit);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new AuthorResult(resultSet.getInt("author_id"), resultSet.getString("author_name"), resultSet.getInt("publication_count")));
		}
		return results;
	}

	public List<AuthorYearStat> getAuthorYearStats(int authorId, int fromYear, int toYear) throws SQLException {
		List<AuthorYearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL author_year_stats_procedure(?,?,?)}")) {
			callableStatement.setInt(1, authorId); callableStatement.setInt(2, fromYear); callableStatement.setInt(3, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new AuthorYearStat(resultSet.getString("year"), resultSet.getInt("publication_count"), resultSet.getInt("journal_count"), resultSet.getInt("conference_count")));
		}
		return results;
	}

	public List<AuthorPublication> getAuthorPublications(int authorId, int fromYear, int toYear) throws SQLException {
		List<AuthorPublication> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL author_publications_procedure(?,?,?)}")) {
			callableStatement.setInt(1, authorId); callableStatement.setInt(2, fromYear); callableStatement.setInt(3, toYear);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next())
				results.add(new AuthorPublication(resultSet.getInt("publication_id"), resultSet.getString("title"), resultSet.getString("year"), resultSet.getString("type"), resultSet.getString("venue")));
		}
		return results;
	}

	public AuthorStats getAuthorStats(int authorId) throws SQLException {
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL author_stats_procedure(?)}")) {
			callableStatement.setInt(1, authorId);
			ResultSet resultSet = callableStatement.executeQuery();
			if (resultSet.next())
				return new AuthorStats(
					resultSet.getString("author_name"),
					resultSet.getInt("first_year"), resultSet.getInt("last_year"),
					resultSet.getInt("total_publications"),
					resultSet.getInt("journal_count"), resultSet.getInt("conf_count"),
					resultSet.getDouble("avg_per_year")
				);
		}
		return null;
	}
}
