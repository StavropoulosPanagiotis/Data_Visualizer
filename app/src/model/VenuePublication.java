package model;

/** A single publication entry in a venue's publication list, including its author count */
public class VenuePublication {
	private final int publicationId, authorCount;
	private final String title, year;

	public VenuePublication(int publicationId, String title, String year, int authorCount) {
		this.publicationId = publicationId;
		this.title = title;
		this.year = year;
		this.authorCount = authorCount;
	}

	public int getPublicationId() { return publicationId; }
	public String getTitle() { return title; }
	public String getYear() { return year; }
	public int getAuthorCount() { return authorCount; }
}
