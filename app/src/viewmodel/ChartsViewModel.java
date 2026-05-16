package viewmodel;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.concurrent.Task;
import model.*;
import service.ChartService;

import java.util.List;

/** ViewModel for the Charts view. Holds observable state and runs service calls */
public class ChartsViewModel {

	private final ObservableList<PublisherStat> publisherStats = FXCollections.observableArrayList();
	private final ObservableList<CategoryYearStat> categoryStats = FXCollections.observableArrayList();
	private final ObservableList<JournalScatter> scatterData = FXCollections.observableArrayList();

	private final BooleanProperty loading = new SimpleBooleanProperty(false);

	private final ChartService service = new ChartService();

	public void loadPublisherStats() {
		loading.set(true);
		Task<List<PublisherStat>> task = new Task<>() {
			@Override
			protected List<PublisherStat> call() throws Exception {
				return service.getPublisherStats();
			}
		};
		task.setOnSucceeded(e -> { loading.set(false); publisherStats.setAll(task.getValue()); });
		task.setOnFailed(e -> { loading.set(false); publisherStats.clear(); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	public void loadCategoryStats(String venueType, String category) {
		loading.set(true);
		Task<List<CategoryYearStat>> task = new Task<>() {
			@Override protected List<CategoryYearStat> call() throws Exception {
				return service.getCategoryYearStats(venueType, category);
			}
		};
		task.setOnSucceeded(e -> { loading.set(false); categoryStats.setAll(task.getValue()); });
		task.setOnFailed(e -> { loading.set(false); categoryStats.clear(); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	public void loadScatterData(String subjectArea) {
		loading.set(true);
		Task<List<JournalScatter>> task = new Task<>() {
			@Override protected List<JournalScatter> call() throws Exception {
				return service.getJournalScatter(subjectArea);
			}
		};
		task.setOnSucceeded(e -> { loading.set(false); scatterData.setAll(task.getValue()); });
		task.setOnFailed(e -> { loading.set(false); scatterData.clear(); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	public ObservableList<PublisherStat> getPublisherStats() { return publisherStats; }
	public ObservableList<CategoryYearStat> getCategoryStats() { return categoryStats; }
	public ObservableList<JournalScatter> getScatterData() { return scatterData; }
	public BooleanProperty loadingProperty() { return loading; }
}
