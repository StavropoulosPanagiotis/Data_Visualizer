package model;

/** Aggregated career statistics for a single author */
public class AuthorStats {
	private final String authorName;
	private final int firstYear, lastYear, totalPublications, journalCount, confCount;
	private final double avgPerYear;

	public AuthorStats(String authorName, int firstYear, int lastYear,
		int totalPublications, int journalCount, int confCount, double avgPerYear) {
		this.authorName = authorName;
		this.firstYear = firstYear;
		this.lastYear = lastYear;
		this.totalPublications = totalPublications;
		this.journalCount = journalCount;
		this.confCount = confCount;
		this.avgPerYear = avgPerYear;
	}

	public String getAuthorName() { return authorName; }
	public int getFirstYear() { return firstYear; }
	public int getLastYear() { return lastYear; }
	public int getTotalPublications() { return totalPublications; }
	public int getJournalCount() { return journalCount; }
	public int getConfCount() { return confCount; }
	public double getAvgPerYear() { return avgPerYear; }
}
