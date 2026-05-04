package viewmodel;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.concurrent.Task;
import model.*;
import service.AuthorService;

import java.util.List;

public class AuthorViewModel {

	private final ObservableList<AuthorResult> results = FXCollections.observableArrayList();
	private final ObservableList<AuthorYearStat> yearStats = FXCollections.observableArrayList();
	private final ObservableList<AuthorPublication> publications = FXCollections.observableArrayList();
	private final ObjectProperty<AuthorStats> authorStats = new SimpleObjectProperty<>();

	private final StringProperty searchName = new SimpleStringProperty("");
	private int fromYear = 1900;
	private int toYear = 2026;

	private final BooleanProperty loading = new SimpleBooleanProperty(false);

	private final AuthorService service = new AuthorService();

	public void search() {
		loading.set(true);
		Task<List<AuthorResult>> task = new Task<>() {
			@Override
			protected List<AuthorResult> call() throws Exception {
				return service.searchAuthors(searchName.get(), fromYear, toYear);
			}
		};
		task.setOnSucceeded(e -> { loading.set(false); results.setAll(task.getValue()); authorStats.set(null); yearStats.clear(); publications.clear(); });
		task.setOnFailed(e -> { loading.set(false); results.clear(); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	public void loadProfile(int authorId) {
		loading.set(true);
		Task<ProfileResult> task = new Task<>() {
			@Override
			protected ProfileResult call() throws Exception {
				return new ProfileResult(
					service.getAuthorStats(authorId),
					service.getAuthorYearStats(authorId, fromYear, toYear),
					service.getAuthorPublications(authorId, fromYear, toYear)
				);
			}
		};
		task.setOnSucceeded(e -> {
			loading.set(false);
			ProfileResult result = task.getValue();
			authorStats.set(result.authorStats);
			yearStats.setAll(result.yearStats);
			publications.setAll(result.authorPublications);
		});
		task.setOnFailed(e -> {
			loading.set(false);
			task.getException().printStackTrace();
		});
		new Thread(task).start();
	}

	private static class ProfileResult {
		final AuthorStats authorStats;
		final List<AuthorYearStat> yearStats;
		final List<AuthorPublication> authorPublications;
		ProfileResult(AuthorStats authorStats, List<AuthorYearStat> yearStats, List<AuthorPublication> authorPublications) {
			this.authorStats = authorStats;
			this.yearStats = yearStats;
			this.authorPublications = authorPublications;
		}
	}

	public ObservableList<AuthorResult> getResults() { return results; }
	public ObservableList<AuthorYearStat> getYearStats() { return yearStats; }
	public ObservableList<AuthorPublication> getPublications() { return publications; }
	public ObjectProperty<AuthorStats> authorStatsProperty() { return authorStats; }
	public BooleanProperty loadingProperty() { return loading; }
	public StringProperty searchNameProperty() { return searchName; }
	public void setFromYear(int fromYear) { this.fromYear = fromYear; }
	public void setToYear(int toYear) { this.toYear = toYear; }
}
