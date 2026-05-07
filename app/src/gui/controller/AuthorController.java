package gui.controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import model.*;
import viewmodel.AuthorViewModel;

/**
 * JavaFX controller for the Author view
 * <p>Responsible for searching authors by name and year range,
 * displaying search results, and showing statistics and publication
 * history for a selected author
 * <p>Communicates with {@link AuthorViewModel}
 *
 */
public class AuthorController {

	@FXML private TextField nameField;
	@FXML private Spinner<Integer> fromSpinner, toSpinner;

	@FXML private TableView<AuthorResult> resultTable;
	@FXML private TableColumn<AuthorResult, String> nameCol;
	@FXML private TableColumn<AuthorResult, Integer> countCol;

	@FXML private Label statName, statYears, statTotal, statJournals, statConf, statAvg;
	@FXML private LineChart<String, Number> lineChart;
	@FXML private TableView<AuthorPublication> pubTable;
	@FXML private TableColumn<AuthorPublication, String> pubTitleCol;
	@FXML private TableColumn<AuthorPublication, String> pubYearCol;
	@FXML private TableColumn<AuthorPublication, String> pubTypeCol;
	@FXML private TableColumn<AuthorPublication, String> pubVenueCol;

	private final AuthorViewModel viewModel = new AuthorViewModel();
	private Popup loadingPopup;

	@FXML
	public void initialize() {
		fromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2026, 1900));
		toSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2026, 2026));

		nameCol.setCellValueFactory(new PropertyValueFactory<>("authorName"));
		countCol.setCellValueFactory(new PropertyValueFactory<>("pubCount"));
		resultTable.setItems(viewModel.getResults());
		nameField.textProperty().bindBidirectional(viewModel.searchNameProperty());

		pubTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		pubYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
		pubTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		pubVenueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
		pubTable.setItems(viewModel.getPublications());

		resultTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			if (selected != null) {
				viewModel.setFromYear(fromSpinner.getValue());
				viewModel.setToYear(toSpinner.getValue());
				viewModel.loadProfile(selected.getAuthorId());
			}
		});

		viewModel.authorStatsProperty().addListener((obs, old, stats) -> {
			if (stats == null) { clearStats(); return; }
			statName.setText(stats.getAuthorName());
			statYears.setText(stats.getFirstYear() + " – " + stats.getLastYear());
			statTotal.setText(String.valueOf(stats.getTotalPublications()));
			statJournals.setText(String.valueOf(stats.getJournalCount()));
			statConf.setText(String.valueOf(stats.getConfCount()));
			statAvg.setText(String.format("%.2f", stats.getAvgPerYear()));
		});

		viewModel.getYearStats().addListener((ListChangeListener<AuthorYearStat>) c -> rebuildLineChart());

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
		Window window = nameField.getScene().getWindow();
		loadingPopup.show(window, 0, 0);
		loadingPopup.setX(window.getX() + window.getWidth() / 2 - loadingPopup.getWidth() / 2);
		loadingPopup.setY(window.getY() + window.getHeight() / 2 - loadingPopup.getHeight() / 2);
	}

	private void hideLoading() {
		if (loadingPopup != null) loadingPopup.hide();
	}

	private void clearStats() {
		statName.setText(""); statYears.setText(""); statTotal.setText("");
		statJournals.setText(""); statConf.setText(""); statAvg.setText("");
	}

	private void rebuildLineChart() {
		lineChart.getData().clear();
		XYChart.Series<String, Number> journals = new XYChart.Series<>();
		journals.setName("Journals");
		XYChart.Series<String, Number> conf = new XYChart.Series<>();
		conf.setName("Conferences");
		for (AuthorYearStat stat : viewModel.getYearStats()) {
			journals.getData().add(new XYChart.Data<>(stat.getYear(), stat.getJournalCount()));
			conf.getData().add(new XYChart.Data<>(stat.getYear(), stat.getConfCount()));
		}
		lineChart.getData().addAll(journals, conf);
	}

	/**
	 * Triggered by the search button, reads the spinner values,
	 * updates the view model's year range and initializes author search
	 *
	 */
	@FXML
	private void handleSearch() {
		viewModel.setFromYear(fromSpinner.getValue());
		viewModel.setToYear(toSpinner.getValue());
		viewModel.search();
	}
}
