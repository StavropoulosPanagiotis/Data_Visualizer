package model;

/** Publication count for a venue in a single year */
public class VenueYearStat {
	private final String year;
	private final int pubCount;

	public VenueYearStat(String year, int pubCount) {
		this.year = year;
		this.pubCount = pubCount;
	}

	public String getYear() { return year; }
	public int getPubCount() { return pubCount; }
}
