-- ------------------------------------------------------------
-- User: DataVisualizer
-- ------------------------------------------------------------

CREATE USER IF NOT EXISTS 'DataVisualizerUser'@'localhost' IDENTIFIED BY 'DataVisualizer';

GRANT ALL PRIVILEGES ON `data_visualizer`.* TO 'DataVisualizerUser'@'localhost';

FLUSH PRIVILEGES;
