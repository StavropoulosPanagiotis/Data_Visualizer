package model;

public class VenueResult {
	private final int venueId;
	private final String title;
	private final String type;
	private final String rank;
	private final int pubCount;

	public VenueResult(int venueId, String title, String type, String rank, int pubCount) {
		this.venueId = venueId;
		this.title = title;
		this.type = type;
		this.rank = rank;
		this.pubCount = pubCount;
	}

	public int getVenueId() { return venueId; }
	public String getTitle() { return title; }
	public String getType() { return type; }
	public String getRank() { return rank; }
	public int getPubCount() { return pubCount; }
}
