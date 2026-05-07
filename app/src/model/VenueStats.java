package model;

/** Total statistics for a single venue across its active publication years */
public class VenueStats {
	private final int firstYear, lastYear, totalPublications, totalAuthors, distinctAuthors;
	private final double avgAuthorsPerArticle, avgArticlesPerYear;

	public VenueStats(int firstYear, int lastYear, int totalPublications,
		int totalAuthors, int distinctAuthors,
		double avgAuthorsPerArticle, double avgArticlesPerYear) {
		this.firstYear = firstYear;
		this.lastYear = lastYear;
		this.totalPublications = totalPublications;
		this.totalAuthors = totalAuthors;
		this.distinctAuthors = distinctAuthors;
		this.avgAuthorsPerArticle = avgAuthorsPerArticle;
		this.avgArticlesPerYear = avgArticlesPerYear;
	}

	public int getFirstYear() { return firstYear; }
	public int getLastYear() { return lastYear; }
	public int getTotalPublications() { return totalPublications; }
	public int getTotalAuthors() { return totalAuthors; }
	public int getDistinctAuthors() { return distinctAuthors; }
	public double getAvgAuthorsPerArticle() { return avgAuthorsPerArticle; }
	public double getAvgArticlesPerYear() { return avgArticlesPerYear; }
}
