package gui.controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import model.*;
import viewmodel.ChartsViewModel;

import java.util.*;

public class ChartsController {

	@FXML private StackedBarChart<String, Number> publisherChart;

	@FXML private ComboBox<String> catTypeCombo;
	@FXML private TextField catField;
	@FXML private LineChart<String, Number> categoryChart;

	@FXML private TextField scatterSubjectField;
	@FXML private ComboBox<String> xAxisCombo, yAxisCombo;
	@FXML private ScatterChart<Number, Number> scatterChart;

	private final ChartsViewModel viewModel = new ChartsViewModel();
	private Popup loadingPopup;

	@FXML
	public void initialize() {
		catTypeCombo.getItems().addAll("Journal", "Conference");
		catTypeCombo.setValue("Journal");

		List<String> metrics = List.of("SJR Index", "CiteScore", "H-Index", "Total Docs", "Total Docs 3y",
			"Total Refs", "Total Cites 3y", "Citable Docs 3y", "Cites/Doc 2y", "Refs/Doc");
		xAxisCombo.getItems().addAll(metrics);
		xAxisCombo.setValue("SJR Index");
		yAxisCombo.getItems().addAll(metrics);
		yAxisCombo.setValue("CiteScore");

		viewModel.getPublisherStats().addListener((ListChangeListener<PublisherStat>) c -> rebuildPublisherChart());
		viewModel.getCategoryStats().addListener((ListChangeListener<CategoryYearStat>) c -> rebuildCategoryChart());
		viewModel.getScatterData().addListener((ListChangeListener<JournalScatter>) c -> rebuildScatterChart());

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
		Window window = publisherChart.getScene().getWindow();
		loadingPopup.show(window, 0, 0);
		loadingPopup.setX(window.getX() + window.getWidth() / 2 - loadingPopup.getWidth() / 2);
		loadingPopup.setY(window.getY() + window.getHeight() / 2 - loadingPopup.getHeight() / 2);
	}

	private void hideLoading() {
		if (loadingPopup != null) loadingPopup.hide();
	}

	@FXML
	private void handleLoadPublisher() {
		viewModel.loadPublisherStats();
	}

	private void rebuildPublisherChart() {
		publisherChart.getData().clear();
		XYChart.Series<String, Number> q1 = new XYChart.Series<>(); q1.setName("Q1");
		XYChart.Series<String, Number> q2 = new XYChart.Series<>(); q2.setName("Q2");
		XYChart.Series<String, Number> q3 = new XYChart.Series<>(); q3.setName("Q3");
		XYChart.Series<String, Number> q4 = new XYChart.Series<>(); q4.setName("Q4");
		for (PublisherStat stat : viewModel.getPublisherStats()) {
			String pub = stat.getPublisher() == null ? "(none)" : stat.getPublisher();
			q1.getData().add(new XYChart.Data<>(pub, stat.getQ1Count()));
			q2.getData().add(new XYChart.Data<>(pub, stat.getQ2Count()));
			q3.getData().add(new XYChart.Data<>(pub, stat.getQ3Count()));
			q4.getData().add(new XYChart.Data<>(pub, stat.getQ4Count()));
		}
		publisherChart.getData().addAll(q1, q2, q3, q4);
	}

	@FXML
	private void handleLoadCategory() {
		String venueType = "Journal".equals(catTypeCombo.getValue()) ? "journal" : "conference";
		viewModel.loadCategoryStats(venueType, catField.getText().trim());
	}

	private void rebuildCategoryChart() {
		categoryChart.getData().clear();
		XYChart.Series<String, Number> venues = new XYChart.Series<>(); venues.setName("Venues");
		XYChart.Series<String, Number> pubs = new XYChart.Series<>(); pubs.setName("Publications");
		for (CategoryYearStat stat : viewModel.getCategoryStats()) {
			venues.getData().add(new XYChart.Data<>(stat.getYear(), stat.getVenueCount()));
			pubs.getData().add(new XYChart.Data<>(stat.getYear(), stat.getPubCount()));
		}
		categoryChart.getData().addAll(venues, pubs);
	}

	@FXML
	private void handleLoadScatter() {
		viewModel.loadScatterData(scatterSubjectField.getText().trim());
	}

	private void rebuildScatterChart() {
		scatterChart.getData().clear();
		String xMetric = xAxisCombo.getValue();
		String yMetric = yAxisCombo.getValue();
		scatterChart.setTitle(xMetric + " vs " + yMetric);
		scatterChart.getXAxis().setLabel(xMetric);
		scatterChart.getYAxis().setLabel(yMetric);
		Map<String, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();
		for (JournalScatter journalScatter : viewModel.getScatterData()) {
			double xValue, yValue;
			try { xValue = metricValue(journalScatter, xMetric); } catch (Exception e) { continue; }
			try { yValue = metricValue(journalScatter, yMetric); } catch (Exception e) { continue; }
			String quartile = journalScatter.getQuartile() == null ? "?" : journalScatter.getQuartile();
			seriesMap.computeIfAbsent(quartile, key -> {
				XYChart.Series<Number, Number> series = new XYChart.Series<>();
				series.setName(key);
				return series;
			}).getData().add(new XYChart.Data<>(xValue, yValue));
		}
		scatterChart.getData().addAll(seriesMap.values());
	}

	private double metricValue(JournalScatter journalScatter, String metric) {
		String raw = switch (metric) {
			case "SJR Index" -> journalScatter.getSjrIndex();
			case "CiteScore" -> journalScatter.getCitescore();
			case "H-Index" -> journalScatter.getHIndex();
			case "Total Docs" -> journalScatter.getTotalDocs();
			case "Total Docs 3y" -> journalScatter.getTotalDocs3y();
			case "Total Refs" -> journalScatter.getTotalRefs();
			case "Total Cites 3y" -> journalScatter.getTotalCites3y();
			case "Citable Docs 3y" -> journalScatter.getCitableDocs3y();
			case "Cites/Doc 2y" -> journalScatter.getCitesDoc2y();
			case "Refs/Doc" -> journalScatter.getRefsDoc();
			default -> throw new IllegalArgumentException();
		};
		if (raw == null || raw.isEmpty()) throw new NumberFormatException();
		return Double.parseDouble(normalizeNumber(raw));
	}

	private String normalizeNumber(String raw) {
		int lastComma = raw.lastIndexOf(',');
		if (lastComma == -1) return raw;
		int lastDot = raw.lastIndexOf('.');
		if (lastDot > lastComma) return raw.replace(",", "");
		if (raw.length() - lastComma - 1 <= 2) return raw.replace(",", ".");
		return raw.replace(",", "");
	}
}
