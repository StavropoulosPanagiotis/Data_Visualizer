package model;

/** Per-year publication totals broken down by journals and conferences */
public class YearStat {
	private final String year;
	private final int total;
	private final int journalCount;
	private final int confCount;

	public YearStat(String year, int total, int journalCount, int confCount) {
		this.year = year;
		this.total = total;
		this.journalCount = journalCount;
		this.confCount = confCount;
	}

	public String getYear() { return year; }
	public int getTotal() { return total; }
	public int getJournalCount() { return journalCount; }
	public int getConfCount() { return confCount; }
}
