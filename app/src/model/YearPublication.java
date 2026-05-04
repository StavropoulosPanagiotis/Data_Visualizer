package model;

public class YearPublication {
	private final int publicationId;
	private final String title, type, venue;

	public YearPublication(int publicationId, String title, String type, String venue) {
		this.publicationId = publicationId;
		this.title = title;
		this.type = type;
		this.venue = venue;
	}

	public int getPublicationId() { return publicationId; }
	public String getTitle() { return title; }
	public String getType() { return type; }
	public String getVenue() { return venue; }
}
