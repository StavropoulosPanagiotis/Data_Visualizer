package model;

/** Publication and author counts for a venue in a single year */
public class VenueYearDetail {
	private final String year;
	private final int pubCount, totalAuthors, distinctAuthors;

	public VenueYearDetail(String year, int pubCount, int totalAuthors, int distinctAuthors) {
		this.year = year;
		this.pubCount = pubCount;
		this.totalAuthors = totalAuthors;
		this.distinctAuthors = distinctAuthors;
	}

	public String getYear() { return year; }
	public int getPubCount() { return pubCount; }
	public int getTotalAuthors() { return totalAuthors; }
	public int getDistinctAuthors() { return distinctAuthors; }
}
