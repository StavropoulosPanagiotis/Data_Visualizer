package viewmodel;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.concurrent.Task;
import model.*;
import service.YearService;

import java.util.List;

/** ViewModel for the Year view. Holds observable state and runs service calls */
public class YearViewModel {

	private final ObservableList<YearStat> results = FXCollections.observableArrayList();
	private final ObservableList<YearPublication> publications = FXCollections.observableArrayList();
	private final ObjectProperty<YearProfile> yearProfile = new SimpleObjectProperty<>();

	private int fromYear = 1900;
	private int toYear = 2026;

	private final BooleanProperty loading = new SimpleBooleanProperty(false);

	private final YearService service = new YearService();

	public void load() {
		loading.set(true);
		Task<List<YearStat>> task = new Task<>() {
			@Override protected List<YearStat> call() throws Exception {
				return service.getPublicationsPerYear(fromYear, toYear);
			}
		};
		task.setOnSucceeded(e -> { loading.set(false); results.setAll(task.getValue()); yearProfile.set(null); publications.clear(); });
		task.setOnFailed(e -> { loading.set(false); results.clear(); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	public void loadYearDetail(int year, String typeFilter, String venueName, String authorName) {
		loading.set(true);
		Task<YearDetailBundle> task = new Task<>() {
			@Override protected YearDetailBundle call() throws Exception {
				return new YearDetailBundle(
					service.getYearProfile(year),
					service.getYearPublications(year, typeFilter, venueName, authorName)
				);
			}
		};
		task.setOnSucceeded(e -> {
			loading.set(false);
			YearDetailBundle bundle = task.getValue();
			yearProfile.set(bundle.profile);
			publications.setAll(bundle.publications);
		});
		task.setOnFailed(e -> { loading.set(false); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	private static class YearDetailBundle {
		final YearProfile profile;
		final List<YearPublication> publications;
		YearDetailBundle(YearProfile profile, List<YearPublication> publications) {
			this.profile = profile; this.publications = publications;
		}
	}

	public ObservableList<YearStat> getResults() { return results; }
	public ObservableList<YearPublication> getPublications() { return publications; }
	public ObjectProperty<YearProfile> yearProfileProperty() { return yearProfile; }
	public BooleanProperty loadingProperty() { return loading; }
	public void setFromYear(int year) { this.fromYear = year; }
	public void setToYear(int year) { this.toYear = year; }
}
