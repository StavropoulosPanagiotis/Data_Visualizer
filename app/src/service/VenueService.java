package service;

import model.*;
import repository.VenueRepository;

import java.sql.SQLException;
import java.util.List;

public class VenueService {

	private final VenueRepository repository = new VenueRepository();

	public List<VenueResult> searchVenues(String name, String type, int fromYear, int toYear) throws SQLException { return repository.searchVenues(name, type, fromYear, toYear, 100); }
	public List<VenueYearStat> getVenueYearStats(int venueId, String type, int fromYear, int toYear) throws SQLException { return repository.getVenueYearStats(venueId, type, fromYear, toYear); }
	public VenueStats getVenueStats(int venueId, String type, int fromYear, int toYear) throws SQLException { return repository.getVenueStats(venueId, type, fromYear, toYear); }
	public List<VenueYearDetail> getVenueYearDetail(int venueId, String type, int fromYear, int toYear) throws SQLException { return repository.getVenueYearDetail(venueId, type, fromYear, toYear); }
	public List<VenuePublication> getVenuePublications(int venueId, String type, int fromYear, int toYear) throws SQLException { return repository.getVenuePublications(venueId, type, fromYear, toYear); }
}
