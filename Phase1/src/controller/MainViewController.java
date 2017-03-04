package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import application.Main;
import application.TestBench;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DiscreteMap;
import model.Grid;
import model.Heuristic;
import model.ConsistentHeuristic;
import model.Alt1Heuristic;
import model.Alt2Heuristic;
import model.Alt3Heuristic;
import model.Alt4Heuristic;
import model.PathFinder;
import model.AStarFinder;
import model.AStarWeightedFinder;
import model.AStarSequentialFinder;
import model.AStarIntegratedFinder;
import model.Cell;
import model.UniformCostFinder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputDialog;


public class MainViewController {

	private final int NUM_ROWS = 120;
	private final int NUM_COLUMNS = 160;

	@FXML
	private Button generateButton;
	@FXML
	private Button solveButton;
	@FXML
	private Button newStartGoalButton;
	@FXML
	private Button clearPathButton;
	@FXML
	private MenuItem openMenuItem;
	@FXML
	private MenuItem saveMenuItem;
	@FXML
	private MenuItem closeMenuItem;
	@FXML
	private MenuItem runExperimentMenuItem;
	@FXML
	private Canvas mapCanvas;
	@FXML
	private SplitPane splitPane;
	@FXML
	private AnchorPane leftPane;
	@FXML
	private Pane pane;
	@FXML
	private Label xCoordLabel;
	@FXML
	private Label yCoordLabel;
	@FXML
	private Label gValueLabel;
	@FXML
	private Label hValueLabel;
	@FXML
	private Label fValueLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label weightLabel;
	@FXML
	private Label timeLabel;
	@FXML
	private Label costLabel;
	@FXML
	private Label expandedLabel;
	@FXML
	private ComboBox<String> algorithmBox;
	@FXML
	private ComboBox<String> heuristicBox;

	StringProperty x;
	StringProperty y;
	StringProperty g;
	StringProperty h;
	StringProperty f;
	StringProperty type;
	StringProperty weight;
	StringProperty time;
	StringProperty expanded;
	StringProperty cost;


	private Main main;
	private DiscreteMap map;
	private Grid grid;
	private GraphicsContext gc;
	private Stage primaryStage;
	private ArrayList<Rectangle> rectangles;
	private PathFinder pathFinder;
	private Heuristic heuristic;


	public MainViewController(){

	}

	public void start(Stage stage){
		this.primaryStage = stage;
	}

	@FXML
	private void initialize(){

		ObservableList<String> options1 =
			    FXCollections.observableArrayList(
			        "A*",
			        "Weighted A*",
			        "Uniform-Cost",
			        "A* Sequential",
			        "A* Integrated"
			    );
		algorithmBox.setItems(options1);
		algorithmBox.getSelectionModel().selectFirst();

		ObservableList<String> options2 =
			    FXCollections.observableArrayList(
			        "Consistent",
			        "Alt 1",
			        "Alt 2",
			        "Alt 3",
			        "Alt 4"
			    );
		heuristicBox.setItems(options2);
		heuristicBox.getSelectionModel().selectFirst();

		this.grid = new Grid();
		this.gc = mapCanvas.getGraphicsContext2D();
		grid.drawGrid(map, gc);
		rectangles = grid.createClickCells(NUM_COLUMNS, NUM_ROWS);
		pane.getChildren().addAll(rectangles);
		//System.out.println("children number: " + pane.getChildren().size());

		x = new SimpleStringProperty();
		y = new SimpleStringProperty();
		g = new SimpleStringProperty();
		h = new SimpleStringProperty();
		f = new SimpleStringProperty();
		type = new SimpleStringProperty();

		xCoordLabel.textProperty().bind(x);
		yCoordLabel.textProperty().bind(y);
		gValueLabel.textProperty().bind(g);
		hValueLabel.textProperty().bind(h);
		fValueLabel.textProperty().bind(f);
		typeLabel.textProperty().bind(type);

		weight = new SimpleStringProperty();
		weightLabel.textProperty().bind(weight);
		time = new SimpleStringProperty();
		timeLabel.textProperty().bind(time);
		expanded = new SimpleStringProperty();
		expandedLabel.textProperty().bind(expanded);
		cost = new SimpleStringProperty();
		costLabel.textProperty().bind(cost);


		assignEventHandlers();

	}

	private void assignEventHandlers(){

		for (int i = 0; i < rectangles.size(); i++){
			Rectangle rect = rectangles.get(i);
			rect.setOnMousePressed((event) ->{

				//System.out.println("rectangle pressed: " + rect.getX() + " " + rect.getY() + " " +  rect.getLayoutX() + " " + rect.getLayoutY());
				int xCoord =(int)(rect.getX() / 8) - 1;
				int yCoord = (int)(rect.getY() / 8) - 1;

				if (map != null){
					Cell[][] cells = map.getCells();
					Cell cell = cells[xCoord][yCoord];
					x.setValue(Integer.toString(cell.getXCoord()));
					y.setValue(Integer.toString(cell.getYCoord()));
					g.setValue(String.format( "%.5f", cell.getG()));
					h.setValue(String.format( "%.5f", cell.getH()));
					f.setValue(String.format( "%.5f", cell.getF()));
					type.setValue(cell.getTypeString());

				}
			});
		}

		generateButton.setOnAction((event) -> {
			map = new DiscreteMap();
			if (map != null){
				grid.drawGrid(map, gc);
			}

		});

		newStartGoalButton.setOnAction((event) -> {
			if (map != null){
				map.setNewStartGoal();
				grid.drawGrid(map, gc);
			}
		});

		solveButton.setOnAction((event) -> {
			if (map != null){
				grid.drawGrid(map, gc);
				String algo = algorithmBox.getValue();
				pathFinder = null;
				heuristic = selectHeuristic();
				if (algo == null || heuristic == null){
					return;
				}
				if (algo.equals("A*")){
					pathFinder = new AStarFinder(map, heuristic);
				}
				else if (algo.equals("Weighted A*")){
					TextInputDialog dialog = new TextInputDialog();
					//dialog.setTitle("Enter a");
					dialog.setHeaderText("Enter a weight(w >=1).");
					//dialog.setContentText("Please enter your name:");
					Optional<String> result = dialog.showAndWait();
					pathFinder = new AStarWeightedFinder(map, heuristic);
					result.ifPresent(weight -> {
						System.out.println("Entered weight " + weight);
						pathFinder.setWeight(Double.parseDouble(weight));
					});
				}
				else if (algo.equals("Uniform-Cost")){
					pathFinder = new UniformCostFinder(map, null);
				}
				else if (algo.equals("A* Sequential")){
					TextInputDialog dialog = new TextInputDialog();
					dialog.setHeaderText("Enter a w1(w1 >=1).");
					pathFinder = new AStarSequentialFinder(map, null);
					Optional<String> result = dialog.showAndWait();
					result.ifPresent(weight -> {
						System.out.println("Entered w1 " + weight);
						pathFinder.setW1(Double.parseDouble(weight));
					});
					dialog = new TextInputDialog();
					dialog.setHeaderText("Enter a w2(w2 >=1).");
					Optional<String> result2 = dialog.showAndWait();
					result2.ifPresent(weight -> {
						System.out.println("Entered w2 " + weight);
						pathFinder.setW2(Double.parseDouble(weight));
					});
				}
				else if (algo.equals("A* Integrated")){
					TextInputDialog dialog = new TextInputDialog();
					dialog.setHeaderText("Enter a w1(w1 >=1).");
					pathFinder = new AStarSequentialFinder(map, null);
					Optional<String> result = dialog.showAndWait();
					result.ifPresent(weight -> {
						System.out.println("Entered w1 " + weight);
						pathFinder.setW1(Double.parseDouble(weight));
					});
					dialog = new TextInputDialog();
					dialog.setHeaderText("Enter a w2(w2 >=1).");
					Optional<String> result2 = dialog.showAndWait();
					result2.ifPresent(weight -> {
						System.out.println("Entered w2 " + weight);
						pathFinder.setW2(Double.parseDouble(weight));
					});
				}
				else {
					System.out.println("pathFinder selection error");
					//nothing selected
				}
				Cell[][] updatedCells = pathFinder.findPath();
				if (updatedCells != null){
					//reset these values because of sequential A*
					map.setCells(updatedCells);
					map.setStartCell(map.getCells()[map.getStartCell().getXCoord()][map.getStartCell().getYCoord()]);
					map.setGoalCell(map.getCells()[map.getGoalCell().getXCoord()][map.getGoalCell().getYCoord()]);

					//need to change methods for sequential and integrated
					weight.setValue(String.format( "%.5f", pathFinder.getWeight()));
					time.setValue(String.format( "%.3f", pathFinder.getTimeElapsed()) + "ms");
					cost.setValue(String.format( "%.5f", pathFinder.getTotalCost()));
					expanded.setValue(Integer.toString(pathFinder.getExpandedCells()));


					grid.drawGrid(map, gc);
					grid.drawPath(map, gc);

				}
			}
		});

		clearPathButton.setOnAction((event) -> {
			if (map != null){
				grid.drawGrid(map, gc);
			}
		});

		openMenuItem.setOnAction((event) -> {
			File file = getFile();
			if (file != null){
				map = new DiscreteMap(file);
				grid.drawGrid(map, gc);
			}
		});

		runExperimentMenuItem.setOnAction((event) -> {
			TestBench testBench = new TestBench();
			testBench.runExperiment();
		});

		saveMenuItem.setOnAction((event) -> {
			if (map != null){
				saveFile();
			}
		});

		closeMenuItem.setOnAction((event) -> {
			main.handleClose();
		});
	}

	private Heuristic selectHeuristic(){
		Heuristic heuristic = null;
		String option = heuristicBox.getValue();
		if (option.equals("Consistent")){
			System.out.println("chosen: Consistent");
			heuristic = new ConsistentHeuristic();
		}
		else if (option.equals("Alt 1")){
			System.out.println("chosen: Alt1");
			heuristic = new Alt1Heuristic();
		}
		else if (option.equals("Alt 2")){
			System.out.println("chosen: Alt2");
			heuristic = new Alt2Heuristic();
		}
		else if (option.equals("Alt 3")){
			System.out.println("chosen: Alt3");
			heuristic = new Alt3Heuristic();
		}
		else if (option.equals("Alt 4")){
			System.out.println("chosen: Alt4");
			heuristic = new Alt4Heuristic();
		}
		else {
			System.out.println("heuristic selection error");
			//nothing selected
		}
		return heuristic;
	}

	private File getFile(){

		Stage stage = new Stage();
		FileChooser fc = new FileChooser();
		fc.setTitle("Select map file");
		File file = fc.showOpenDialog(stage);

		return file;
	}

	private void saveFile(){
		Stage stage = new Stage();
		FileChooser fc = new FileChooser();
		fc.setTitle("Select map file");
		File file = fc.showSaveDialog(stage);
		if (file != null){
			System.out.println("file: " + file.getAbsolutePath());
			map.writeToFile(file);
		}
		else {
			System.out.println("file is null");
		}

	}



	public void setMain(Main main){
		this.main = main;
	}
}
