package model;

/** Total publication and author statistics for a single year */
public class YearProfile {
	private final int totalPublications, distinctJournals, distinctConferences, totalAuthors, distinctAuthors;

	public YearProfile(int totalPublications, int distinctJournals, int distinctConferences,
		int totalAuthors, int distinctAuthors) {
		this.totalPublications = totalPublications;
		this.distinctJournals = distinctJournals;
		this.distinctConferences = distinctConferences;
		this.totalAuthors = totalAuthors;
		this.distinctAuthors = distinctAuthors;
	}

	public int getTotalPublications() { return totalPublications; }
	public int getDistinctJournals() { return distinctJournals; }
	public int getDistinctConferences() { return distinctConferences; }
	public int getTotalAuthors() { return totalAuthors; }
	public int getDistinctAuthors() { return distinctAuthors; }
}
