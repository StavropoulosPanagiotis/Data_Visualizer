package service;

import model.*;
import repository.AuthorRepository;

import java.sql.SQLException;
import java.util.List;

public class AuthorService {

	private final AuthorRepository repository = new AuthorRepository();

	public List<AuthorResult> searchAuthors(String name, int fromYear, int toYear) throws SQLException { return repository.searchAuthors(name, fromYear, toYear); }
	public List<AuthorYearStat> getAuthorYearStats(int authorId, int fromYear, int toYear) throws SQLException { return repository.getAuthorYearStats(authorId, fromYear, toYear); }
	public List<AuthorPublication> getAuthorPublications(int authorId, int fromYear, int toYear) throws SQLException { return repository.getAuthorPublications(authorId, fromYear, toYear); }
	public AuthorStats getAuthorStats(int authorId) throws SQLException { return repository.getAuthorStats(authorId); }
}
