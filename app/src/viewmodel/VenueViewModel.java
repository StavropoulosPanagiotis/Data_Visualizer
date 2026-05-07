package viewmodel;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.concurrent.Task;
import model.*;
import service.VenueService;

import java.util.List;

/** ViewModel for the Venue view. Holds observable state and runs service calls */
public class VenueViewModel {

	private final ObservableList<VenueResult> results = FXCollections.observableArrayList();
	private final ObservableList<VenueYearDetail> yearDetails = FXCollections.observableArrayList();
	private final ObservableList<VenuePublication> publications = FXCollections.observableArrayList();
	private final ObjectProperty<VenueStats> venueStats = new SimpleObjectProperty<>();

	private final StringProperty searchName = new SimpleStringProperty("");
	private String type = "";
	private int fromYear = 1900;
	private int toYear = 2026;

	private final BooleanProperty loading = new SimpleBooleanProperty(false);

	private final VenueService service = new VenueService();

	public void search() {
		loading.set(true);
		Task<List<VenueResult>> task = new Task<>() {
			@Override protected List<VenueResult> call() throws Exception {
				return service.searchVenues(searchName.get(), type, fromYear, toYear);
			}
		};
		task.setOnSucceeded(e -> { loading.set(false); results.setAll(task.getValue()); venueStats.set(null); yearDetails.clear(); publications.clear(); });
		task.setOnFailed(e -> { loading.set(false); results.clear(); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	public void loadProfile(int venueId, String venueType) {
		loading.set(true);
		Task<ProfileBundle> task = new Task<>() {
			@Override protected ProfileBundle call() throws Exception {
				return new ProfileBundle(
					service.getVenueStats(venueId, venueType, fromYear, toYear),
					service.getVenueYearDetail(venueId, venueType, fromYear, toYear),
					service.getVenuePublications(venueId, venueType, fromYear, toYear)
				);
			}
		};
		task.setOnSucceeded(e -> {
			loading.set(false);
			ProfileBundle bundle = task.getValue();
			venueStats.set(bundle.stats);
			yearDetails.setAll(bundle.yearDetails);
			publications.setAll(bundle.publications);
		});
		task.setOnFailed(e -> { loading.set(false); task.getException().printStackTrace(); });
		new Thread(task).start();
	}

	private static class ProfileBundle {
		final VenueStats stats;
		final List<VenueYearDetail> yearDetails;
		final List<VenuePublication> publications;
		ProfileBundle(VenueStats stats, List<VenueYearDetail> yearDetails, List<VenuePublication> publications) {
			this.stats = stats; this.yearDetails = yearDetails; this.publications = publications;
		}
	}

	public ObservableList<VenueResult> getResults() { return results; }
	public ObservableList<VenueYearDetail> getYearDetails() { return yearDetails; }
	public ObservableList<VenuePublication> getPublications() { return publications; }
	public ObjectProperty<VenueStats> venueStatsProperty() { return venueStats; }
	public BooleanProperty loadingProperty() { return loading; }
	public StringProperty searchNameProperty() { return searchName; }
	public void setType(String type) { this.type = type; }
	public void setFromYear(int year) { this.fromYear = year; }
	public void setToYear(int year) { this.toYear = year; }
}
