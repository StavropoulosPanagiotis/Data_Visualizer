package service;

import model.*;
import repository.YearRepository;

import java.sql.SQLException;
import java.util.List;

public class YearService {

	private final YearRepository repository = new YearRepository();

	public List<YearStat> getPublicationsPerYear(int fromYear, int toYear) throws SQLException { return repository.getPublicationsPerYear(fromYear, toYear); }
	public YearProfile getYearProfile(int year) throws SQLException { return repository.getYearProfile(year); }
	public List<YearPublication> getYearPublications(int year, String typeFilter, String venueName, String authorName) throws SQLException { return repository.getYearPublications(year, typeFilter, venueName, authorName); }
}
