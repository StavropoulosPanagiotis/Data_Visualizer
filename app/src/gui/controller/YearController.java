package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import model.*;
import viewmodel.YearViewModel;

/**
 * JavaFX controller for the Year view.
 * <p> Responsible for showing publication statistics through
 * a year range, displaying per-year totals in a table, and showing
 * a filtered publication list and profile statistics for a selected year.
 * <p> Communicates with {@link YearViewModel}
 *
 */
public class YearController {

	@FXML private Spinner<Integer> fromSpinner, toSpinner;

	@FXML private TableView<YearStat> resultTable;
	@FXML private TableColumn<YearStat, String> yearCol;
	@FXML private TableColumn<YearStat, Integer> totalCol;
	@FXML private TableColumn<YearStat, Integer> journalCol;
	@FXML private TableColumn<YearStat, Integer> confCol;

	@FXML private Label statTotal, statJournals, statConferences, statTotalAuthors, statDistinctAuthors;

	@FXML private ComboBox<String> typeFilterCombo;
	@FXML private TextField venueFilterField, authorFilterField;

	@FXML private TableView<YearPublication> yearPubTable;
	@FXML private TableColumn<YearPublication, String> yearPubTitleCol;
	@FXML private TableColumn<YearPublication, String> yearPubTypeCol;
	@FXML private TableColumn<YearPublication, String> yearPubVenueCol;

	private final YearViewModel viewModel = new YearViewModel();
	private int selectedYear = -1;
	private Popup loadingPopup;

	@FXML
	public void initialize() {
		fromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2026, 1900));
		toSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2026, 2026));

		yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
		totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
		journalCol.setCellValueFactory(new PropertyValueFactory<>("journalCount"));
		confCol.setCellValueFactory(new PropertyValueFactory<>("confCount"));
		resultTable.setItems(viewModel.getResults());

		typeFilterCombo.getItems().addAll("All", "Journal", "Conference");
		typeFilterCombo.setValue("All");

		yearPubTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		yearPubTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		yearPubVenueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
		yearPubTable.setItems(viewModel.getPublications());

		resultTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			if (selected != null) {
				selectedYear = Integer.parseInt(selected.getYear());
				loadYearDetail();
			}
		});

		viewModel.yearProfileProperty().addListener((obs, old, profile) -> {
			if (profile == null) { clearStats(); return; }
			statTotal.setText(String.valueOf(profile.getTotalPublications()));
			statJournals.setText(String.valueOf(profile.getDistinctJournals()));
			statConferences.setText(String.valueOf(profile.getDistinctConferences()));
			statTotalAuthors.setText(String.valueOf(profile.getTotalAuthors()));
			statDistinctAuthors.setText(String.valueOf(profile.getDistinctAuthors()));
		});

		viewModel.loadingProperty().addListener((obs, old, loading) -> {
			if (loading) showLoading(); else hideLoading();
		});
	}

	private void showLoading() {
		if (loadingPopup == null) {
			ProgressIndicator indicator = new ProgressIndicator();
			indicator.getStyleClass().add("loading-indicator");
			Label label = new Label("Please wait...");
			VBox box = new VBox(indicator, label);
			box.getStyleClass().add("loading-box");
			loadingPopup = new Popup();
			loadingPopup.getContent().add(box);
		}
		Window window = fromSpinner.getScene().getWindow();
		loadingPopup.show(window, 0, 0);
		loadingPopup.setX(window.getX() + window.getWidth() / 2 - loadingPopup.getWidth() / 2);
		loadingPopup.setY(window.getY() + window.getHeight() / 2 - loadingPopup.getHeight() / 2);
	}

	private void hideLoading() {
		if (loadingPopup != null) loadingPopup.hide();
	}

	private void clearStats() {
		statTotal.setText(""); statJournals.setText(""); statConferences.setText("");
		statTotalAuthors.setText(""); statDistinctAuthors.setText("");
	}

	private void loadYearDetail() {
		if (selectedYear == -1) return;
		String sel = typeFilterCombo.getValue();
		String typeFilter = "Journal".equals(sel) ? "journal" : "Conference".equals(sel) ? "conference" : "";
		String venueName = venueFilterField.getText().trim();
		String authorName = authorFilterField.getText().trim();
		viewModel.loadYearDetail(selectedYear, typeFilter, venueName, authorName);
	}

	@FXML private void handleShow() {
		viewModel.setFromYear(fromSpinner.getValue());
		viewModel.setToYear(toSpinner.getValue());
		viewModel.load();
	}

	@FXML private void handleFilter() {
		loadYearDetail();
	}
}
