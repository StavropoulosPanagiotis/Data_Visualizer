package model;

/** Venue and publication counts per year for a given category and venue type */
public class CategoryYearStat {
	private final String year;
	private final int venueCount, pubCount;

	public CategoryYearStat(String year, int venueCount, int pubCount) {
		this.year = year;
		this.venueCount = venueCount;
		this.pubCount = pubCount;
	}

	public String getYear() { return year; }
	public int getVenueCount() { return venueCount; }
	public int getPubCount() { return pubCount; }
}
