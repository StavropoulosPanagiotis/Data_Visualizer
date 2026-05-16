package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Constructs the main application window containing
 * the 5 primary views: Home, Authors, Venues, Years, and Charts
 *
 */
public class MainWindow {

 private final TabPane root;

 public MainWindow() throws Exception {
  root = new TabPane();
  Tab homeTab = new Tab("Home", load("/gui/view/HomeView.fxml"));
  Tab authorsTab = new Tab("Authors", load("/gui/view/AuthorView.fxml"));
  Tab venuesTab = new Tab("Venues", load("/gui/view/VenueView.fxml"));
  Tab yearsTab = new Tab("Years", load("/gui/view/YearView.fxml"));
  Tab chartsTab = new Tab("Charts", load("/gui/view/ChartsView.fxml"));
  for (Tab tab : new Tab[]{homeTab, authorsTab, venuesTab, yearsTab, chartsTab})
   tab.setClosable(false);
  root.getTabs().addAll(homeTab, authorsTab, venuesTab, yearsTab, chartsTab);
 }

 private Parent load(String fxml) throws Exception {
  return FXMLLoader.load(getClass().getResource(fxml));
 }

 public TabPane getRoot() { return root; }
}
