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
import viewmodel.VenueViewModel;

public class VenueController {

	@FXML private TextField nameField;
	@FXML private ComboBox<String> typeCombo;
	@FXML private Spinner<Integer> fromSpinner, toSpinner;

	@FXML private TableView<VenueResult> resultTable;
	@FXML private TableColumn<VenueResult, String> titleCol;
	@FXML private TableColumn<VenueResult, String> typeCol;
	@FXML private TableColumn<VenueResult, String> rankCol;
	@FXML private TableColumn<VenueResult, Integer> countCol;

	@FXML private Label statFirstYear, statLastYear, statTotal, statTotalAuthors, statDistinctAuthors, statAvgAuthors, statAvgArticles;

	@FXML private LineChart<String, Number> lineChart;
	@FXML private TableView<VenuePublication> pubTable;
	@FXML private TableColumn<VenuePublication, String> pubTitleCol;
	@FXML private TableColumn<VenuePublication, String> pubYearCol;
	@FXML private TableColumn<VenuePublication, Integer> pubAuthorCountCol;

	private final VenueViewModel viewModel = new VenueViewModel();
	private Popup loadingPopup;

	@FXML
	public void initialize() {
		fromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2100, 2000));
		toSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2100, 2024));

		typeCombo.getItems().addAll("All", "Journal", "Conference");
		typeCombo.setValue("All");

		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
		countCol.setCellValueFactory(new PropertyValueFactory<>("pubCount"));
		resultTable.setItems(viewModel.getResults());
		nameField.textProperty().bindBidirectional(viewModel.searchNameProperty());

		pubTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		pubYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
		pubAuthorCountCol.setCellValueFactory(new PropertyValueFactory<>("authorCount"));
		pubTable.setItems(viewModel.getPublications());

		resultTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			if (selected != null) {
				viewModel.setFromYear(fromSpinner.getValue());
				viewModel.setToYear(toSpinner.getValue());
				viewModel.loadProfile(selected.getVenueId(), selected.getType());
			}
		});

		viewModel.venueStatsProperty().addListener((obs, old, stats) -> {
			if (stats == null) { clearStats(); return; }
			statFirstYear.setText(String.valueOf(stats.getFirstYear()));
			statLastYear.setText(String.valueOf(stats.getLastYear()));
			statTotal.setText(String.valueOf(stats.getTotalPublications()));
			statTotalAuthors.setText(String.valueOf(stats.getTotalAuthors()));
			statDistinctAuthors.setText(String.valueOf(stats.getDistinctAuthors()));
			statAvgAuthors.setText(String.format("%.2f", stats.getAvgAuthorsPerArticle()));
			statAvgArticles.setText(String.format("%.2f", stats.getAvgArticlesPerYear()));
		});

		viewModel.getYearDetails().addListener((ListChangeListener<VenueYearDetail>) c -> rebuildLineChart());

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
		statFirstYear.setText(""); statLastYear.setText(""); statTotal.setText("");
		statTotalAuthors.setText(""); statDistinctAuthors.setText("");
		statAvgAuthors.setText(""); statAvgArticles.setText("");
	}

	private void rebuildLineChart() {
		lineChart.getData().clear();
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Publications");
		for (VenueYearDetail detail : viewModel.getYearDetails())
			series.getData().add(new XYChart.Data<>(detail.getYear(), detail.getPubCount()));
		lineChart.getData().add(series);
	}

	@FXML
	private void handleSearch() {
		String selected = typeCombo.getValue();
		String type = "Journal".equals(selected) ? "journal" : "Conference".equals(selected) ? "conference" : "";
		viewModel.setType(type);
		viewModel.setFromYear(fromSpinner.getValue());
		viewModel.setToYear(toSpinner.getValue());
		viewModel.search();
	}
}
