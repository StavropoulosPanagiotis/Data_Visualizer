package model;

public class AuthorYearStat {
	private final String year;
	private final int pubCount;
	private final int journalCount;
	private final int confCount;

	public AuthorYearStat(String year, int pubCount, int journalCount, int confCount) {
		this.year = year;
		this.pubCount = pubCount;
		this.journalCount = journalCount;
		this.confCount = confCount;
	}

	public String getYear() { return year; }
	public int getPubCount() { return pubCount; }
	public int getJournalCount() { return journalCount; }
	public int getConfCount() { return confCount; }
}
