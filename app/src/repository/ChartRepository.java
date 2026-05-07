package repository;

import db.DBConnection;
import model.*;

import java.sql.*;
import java.util.*;

/**
 * Access layer for chart queries
 * All methods use stored procedures
 */
public class ChartRepository {

	/**
	 * Returns journal counts per publisher by quartile
	 *
	 * @return list of publisher stats
	 * @throws SQLException
	 */
	public List<PublisherStat> getPublisherStats() throws SQLException {
		List<PublisherStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL publisher_stats_procedure()}")) {
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new PublisherStat(
						resultSet.getString("publisher"),
						resultSet.getInt("journal_count"),
						resultSet.getInt("q1_count"),
						resultSet.getInt("q2_count"),
						resultSet.getInt("q3_count"),
						resultSet.getInt("q4_count")
				));
			}
		}
		return results;
	}

	/**
	 * Returns venue and publication counts per year for a given venue type and category
	 *
	 * @param venueType {@code "journal"} or {@code "conference"}
	 * @param category the category to filter by
	 * @return list of per-year stats
	 */
	public List<CategoryYearStat> getCategoryYearStats(String venueType, String category) throws SQLException {
		List<CategoryYearStat> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL category_year_stats_procedure(?,?)}")) {
			callableStatement.setString(1, venueType);
			callableStatement.setString(2, category);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new CategoryYearStat(resultSet.getString("year"),
						resultSet.getInt("venue_count"),
						resultSet.getInt("publication_count")));
			}
		}
		return results;
	}

	/**
	 * Returns data for journals matching the given subject area,
	 * used in the scatter chart
	 *
	 * @param subjectArea the subject area keyword to filter by
	 * @return list of journal scatter records
	 */
	public List<JournalScatter> getJournalScatter(String subjectArea) throws SQLException {
		List<JournalScatter> results = new ArrayList<>();
		try (CallableStatement callableStatement = DBConnection.get().prepareCall("{CALL journal_scatter_procedure(?)}")) {
			callableStatement.setString(1, subjectArea);
			ResultSet resultSet = callableStatement.executeQuery();
			while (resultSet.next()) {
				results.add(new JournalScatter(
						resultSet.getString("title"), resultSet.getString("quartile"), resultSet.getString("subject_area"),
						resultSet.getString("sjr_index"), resultSet.getString("citescore"), resultSet.getString("h_index"),
						resultSet.getString("total_docs"), resultSet.getString("total_docs_3y"), resultSet.getString("total_refs"),
						resultSet.getString("total_cites_3y"), resultSet.getString("citable_docs_3y"),
						resultSet.getString("cites_doc_2y"), resultSet.getString("refs_doc")
				));
			}
		}
		return results;
	}
}
