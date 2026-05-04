package model;

public class AuthorResult {
	private final int authorId;
	private final String authorName;
	private final int pubCount;

	public AuthorResult(int authorId, String authorName, int pubCount) {
		this.authorId = authorId;
		this.authorName = authorName;
		this.pubCount = pubCount;
	}

	public int getAuthorId() { return authorId; }
	public String getAuthorName() { return authorName; }
	public int getPubCount() { return pubCount; }
}
