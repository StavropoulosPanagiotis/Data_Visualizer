package service;

import model.*;
import repository.ChartRepository;

import java.sql.SQLException;
import java.util.List;

public class ChartService {

	private final ChartRepository repository = new ChartRepository();

	public List<PublisherStat> getPublisherStats() throws SQLException { return repository.getPublisherStats(); }
	public List<CategoryYearStat> getCategoryYearStats(String venueType, String category) throws SQLException { return repository.getCategoryYearStats(venueType, category); }
	public List<JournalScatter> getJournalScatter(String subjectArea) throws SQLException { return repository.getJournalScatter(subjectArea); }
}
