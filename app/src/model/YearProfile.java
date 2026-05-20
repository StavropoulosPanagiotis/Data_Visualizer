package model;

/** Total publication and author statistics for a single year */
public class YearProfile {
	private final int totalPublications, journalCount, confCount, totalAuthors, distinctAuthors;

	public YearProfile(int totalPublications, int journalCount, int confCount,
		int totalAuthors, int distinctAuthors) {
		this.totalPublications = totalPublications;
		this.journalCount = journalCount;
		this.confCount = confCount;
		this.totalAuthors = totalAuthors;
		this.distinctAuthors = distinctAuthors;
	}

	public int getTotalPublications() { return totalPublications; }
	public int getJournalCount() { return journalCount; }
	public int getConfCount() { return confCount; }
	public int getTotalAuthors() { return totalAuthors; }
	public int getDistinctAuthors() { return distinctAuthors; }
}
