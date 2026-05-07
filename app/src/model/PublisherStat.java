package model;

/** Journal counts broken down by quartile (Q1–Q4) for a single publisher */
public class PublisherStat {
	private final String publisher;
	private final int journalCount, q1Count, q2Count, q3Count, q4Count;

	public PublisherStat(String publisher, int journalCount,
		int q1Count, int q2Count, int q3Count, int q4Count) {
		this.publisher = publisher;
		this.journalCount = journalCount;
		this.q1Count = q1Count;
		this.q2Count = q2Count;
		this.q3Count = q3Count;
		this.q4Count = q4Count;
	}

	public String getPublisher() { return publisher; }
	public int getJournalCount() { return journalCount; }
	public int getQ1Count() { return q1Count; }
	public int getQ2Count() { return q2Count; }
	public int getQ3Count() { return q3Count; }
	public int getQ4Count() { return q4Count; }
}
