package model;

public class AuthorPublication {
	private final int publicationId;
	private final String title;
	private final String year;
	private final String type;
	private final String venue;

	public AuthorPublication(int publicationId, String title, String year, String type, String venue) {
		this.publicationId = publicationId;
		this.title = title;
		this.year = year;
		this.type = type;
		this.venue = venue;
	}

	public int getPublicationId() { return publicationId; }
	public String getTitle() { return title; }
	public String getYear() { return year; }
	public String getType() { return type; }
	public String getVenue() { return venue; }
}
