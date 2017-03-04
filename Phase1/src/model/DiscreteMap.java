package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import model.Cell.Type;

//********************
import java.util.PriorityQueue;
import java.util.Comparator;
//********************

public class DiscreteMap {


	private final int NUM_ROWS = 120;
	private final int NUM_COLUMNS = 160;

	private Cell[][] cells;
	private ArrayList<Cell> hardCells;
	private ArrayList<Cell> riverCells;
	private ArrayList<Cell> blockedCells;
	private ArrayList<Cell> seedCells;
	private ArrayList<Cell> pathCells;
	private Cell startCell;
	private Cell goalCell;



	public DiscreteMap(){
		this.cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		initializeCells();
		generateHardCells();
		generateRivers();
		createHardCellsList();
		generateBlockedCells();
		generateStartGoalCells();
	}

	public DiscreteMap(File file){
		if (file != null){
			this.cells = new Cell[NUM_COLUMNS][NUM_ROWS];
			initializeCells();
			generateFromFile(file);
			createHardCellsList();
		}

	}

	private void initializeCells(){
		int xCoord;
		int yCoord;
		int currentColumn = 0;
		int currentRow = 0;
		//initialize all cells to unblocked
		for (int i = 0; i < NUM_COLUMNS; i++){
			xCoord = i;
			for (int j = 0; j < NUM_ROWS; j++){
				yCoord = j;
				cells[currentColumn][currentRow] = new Cell(xCoord, yCoord, Type.UNBLOCKED);
				currentRow++;
			}
			currentRow = 0;
			currentColumn++;
		}
	}


	private void generateHardCells(){
		seedCells = new ArrayList<Cell>();
		Random rand = new Random();



		for (int i = 0; i < 8; i++){
			//Pick random coordinate for seed
			int seedX = rand.nextInt(NUM_COLUMNS);
			int seedY = rand.nextInt(NUM_ROWS);
			seedCells.add(cells[seedX][seedY]);

			//Make cells in surrounding 31 by 31 area hard if they are in bounds of grid w/ 50% chance
			for (int j = seedX - 15; j <= seedX + 15; j++){
				int xCoord = j;
				//if xCoord in bounds
				if (xCoord >= 0 && xCoord < NUM_COLUMNS){
					for (int k = seedY - 15; k <= seedY + 15; k++){
						int yCoord = k;
						//if yCoord in bounds
						if (yCoord >= 0 && yCoord < NUM_ROWS){
							//make cell hard w/ 50% chance
							int isPicked = rand.nextInt(2);
							if (isPicked == 1){
								cells[xCoord][yCoord].setType(Type.HARD);
								//hardCells.add(cells[xCoord][yCoord]); I do this at end of generateRivers()!!!

							}
						}
					}
				}
			}
		}

	}

	private void createHardCellsList(){
		hardCells = new ArrayList<Cell>();
		for (int i = 0; i < NUM_ROWS; i++){
			int yCoord = i;
			for ( int j = 0; j < NUM_COLUMNS; j++){
				int xCoord = j;
				if (cells[xCoord][yCoord].getType() == Type.HARD){
					hardCells.add(cells[xCoord][yCoord]);
				}
			}
		}
	}

	private void generateRivers(){

		boolean keepTrying = true;

		riverCells = new ArrayList<Cell>();

		Cell[] boundaryCells = new Cell[556];
		//create array of boundary cells
		for (int i = 0; i < 160; i++){
			boundaryCells[i] = cells[i][0];
		}
		for (int i = 0; i < 160; i++){
			boundaryCells[160 + i] = cells[i][119];
		}
		//i - 1 because i = 1 at start of loop
		for (int i = 1; i < 119; i++){
			boundaryCells[320 + (i - 1)] = cells[0][i];
		}
		for (int i = 1; i < 119; i++){
			boundaryCells[438 + (i - 1)] = cells[159][i];
		}

		Random rand = new Random();
		int successes = 0;

		while (keepTrying){
			//Pick boundary cell thats not a river cell
			Cell seedCell;
			do {
				seedCell = boundaryCells[rand.nextInt(556)];
			}
			while (seedCell.getType() == Type.UNBLOCKEDRIVER || seedCell.getType() == Type.HARDRIVER);

			int xCoord = seedCell.getXCoord();
			int yCoord = seedCell.getYCoord();

			//System.out.println("seedX: " + xCoord);
			//System.out.println("seedY: " + yCoord);

			char direction = 0;

			//find what boundary cell is on
			if (xCoord == 0){
				direction = 'r';
			}
			else if (yCoord == 0){
				direction = 'd';
			}
			else if (xCoord == 159){
				direction = 'l';
			}
			else if (yCoord == 119){
				direction = 'u';
			}
			else {
				System.out.println("Not a boundary cell");
			}
			if (findRiverPath(direction, seedCell)){
				successes++;
			}
			else{
				clearRiverCells(riverCells);
				riverCells = new ArrayList<Cell>();
				successes = 0;
			}
			if (successes == 4){
				keepTrying = false;
			}

		}
		//System.out.println("successes: " + successes);
		//add all hard cells to hardCells once river generation done

	}

	private boolean findRiverPath(char direction, Cell seedCell){
		boolean successful = false;
		//System.out.println("direction at start findRiverPath: " + direction);

		ArrayList<Cell> tempRiverCells = new ArrayList<Cell>();

		boolean pathNotFound = true;
		boolean riverHit = false;
		char currentDirection = direction;

		//add seed cell to tempRiver list
		int xCoord = seedCell.getXCoord();
		int yCoord = seedCell.getYCoord();
		int count = 0;
		tempRiverCells.add(cells[xCoord][yCoord]);
		if (cells[xCoord][yCoord].getType() == Type.UNBLOCKED){
			cells[xCoord][yCoord].setType(Type.UNBLOCKEDRIVER);
		}
		else if (cells[xCoord][yCoord].getType() == Type.HARD){
			cells[xCoord][yCoord].setType(Type.HARDRIVER);
		}

		//System.out.println("cell added: " + cells[xCoord][yCoord].getXCoord() + ":" + cells[xCoord][yCoord].getYCoord());

		int testLimit = 0;

		//iterate through at most 1000 direction changes until restarting
		while (pathNotFound && testLimit < 1000){
			//System.out.println("direction at begin of while loop: " + currentDirection);


			for (int i = 0; i < 20; i++){
				Cell currentCell = null;
				if (currentDirection == 'r'){
					xCoord = xCoord + 1;
					//out of bounds
					if (xCoord >= 160){
						//may have found path
						pathNotFound = false;
						break;
					}
					currentCell = cells[xCoord][yCoord];
					if (currentCell.getType() != Type.UNBLOCKEDRIVER && currentCell.getType() != Type.HARDRIVER){
						tempRiverCells.add(currentCell);
						if (currentCell.getType() == Type.UNBLOCKED){
							currentCell.setType(Type.UNBLOCKEDRIVER);
						}
						else {
							currentCell.setType(Type.HARDRIVER);
						}
						count++;
					}
					else {
						riverHit = true;
						break;
					}
				}
				else if (currentDirection == 'l'){
					xCoord = xCoord - 1;
					if (xCoord <= -1){
						pathNotFound = false;
						break;
					}
					currentCell = cells[xCoord][yCoord];
					if (currentCell.getType() != Type.UNBLOCKEDRIVER && currentCell.getType() != Type.HARDRIVER){
						tempRiverCells.add(currentCell);
						if (currentCell.getType() == Type.UNBLOCKED){
							currentCell.setType(Type.UNBLOCKEDRIVER);
						}
						else {
							currentCell.setType(Type.HARDRIVER);
						}
						count++;
					}
					else {
						riverHit = true;
						break;
					}
				}
				else if (currentDirection == 'd'){
					yCoord = yCoord + 1;
					if (yCoord >= 120){
						pathNotFound = false;
						break;
					}
					currentCell = cells[xCoord][yCoord];
					if (currentCell.getType() != Type.UNBLOCKEDRIVER && currentCell.getType() != Type.HARDRIVER){
						tempRiverCells.add(currentCell);
						if (currentCell.getType() == Type.UNBLOCKED){
							currentCell.setType(Type.UNBLOCKEDRIVER);
						}
						else {
							currentCell.setType(Type.HARDRIVER);
						}
						count++;
					}
					else {
						riverHit = true;
						break;
					}
				}
				else if (currentDirection == 'u'){
					yCoord = yCoord - 1;
					if (yCoord <= -1){
						pathNotFound = false;
						break;
					}
					currentCell = cells[xCoord][yCoord];
					if (currentCell.getType() != Type.UNBLOCKEDRIVER && currentCell.getType() != Type.HARDRIVER){
						tempRiverCells.add(currentCell);
						if (currentCell.getType() == Type.UNBLOCKED){
							currentCell.setType(Type.UNBLOCKEDRIVER);
						}
						else {
							currentCell.setType(Type.HARDRIVER);
						}
						count++;
					}
					else {
						riverHit = true;
						break;
					}
				}
				else{
					System.out.println("direction error 1");
				}
				if (currentCell != null){
					//System.out.println("cell added: " + currentCell.getXCoord() + ":" + currentCell.getYCoord());
				}
			}
			if (riverHit){
					//System.out.println("River hit");
					xCoord = seedCell.getXCoord();
					yCoord = seedCell.getYCoord();
					clearRiverCells(tempRiverCells);
					tempRiverCells = new ArrayList<Cell>();
					tempRiverCells.add(cells[xCoord][yCoord]);
					if (cells[xCoord][yCoord].getType() == Type.UNBLOCKED){
						cells[xCoord][yCoord].setType(Type.UNBLOCKEDRIVER);
					}
					else if (cells[xCoord][yCoord].getType() == Type.HARD){
						cells[xCoord][yCoord].setType(Type.HARDRIVER);
					}
					count = 0;
					currentDirection = direction;
					riverHit = false;
			}
			else if (pathNotFound){
				currentDirection = findNewDirection(currentDirection);
			}
			else if (!pathNotFound){
				if (count < 100){
					clearRiverCells(tempRiverCells);
					tempRiverCells = new ArrayList<Cell>();
					pathNotFound = true;
					xCoord = seedCell.getXCoord();
					yCoord = seedCell.getYCoord();
					tempRiverCells.add(cells[xCoord][yCoord]);
					if (cells[xCoord][yCoord].getType() == Type.UNBLOCKED){
						cells[xCoord][yCoord].setType(Type.UNBLOCKEDRIVER);
					}
					else if (cells[xCoord][yCoord].getType() == Type.HARD){
						cells[xCoord][yCoord].setType(Type.HARDRIVER);
					}
					currentDirection = direction;
					//System.out.println("path too short");
					count = 0;
				}
				else {
					//System.out.println("path found!");
					riverCells.addAll(tempRiverCells);
					successful = true;
				}
			}
			else {
				System.out.println("river error");
			}
			testLimit++;
		}
		if (testLimit == 1000){
			successful = false;
			clearRiverCells(tempRiverCells);
		}

		//System.out.println("cells added: " + count);
		return successful;

	}

	private void clearRiverCells(ArrayList<Cell> tempRiverCells){
		for (int i = 0; i < tempRiverCells.size(); i++){
			Cell cell = tempRiverCells.get(i);
			if (cell.getType() == Type.UNBLOCKEDRIVER){
				cell.setType(Type.UNBLOCKED);
			}
			else if (cell.getType() == Type.HARDRIVER){
				cell.setType(Type.HARD);
			}
		}
	}

	private char findNewDirection(char direction){
		char newDirection = direction;
		Random rand = new Random();
		int pick = 0;

		if (direction == 'r'){
			pick = rand.nextInt(5);
			if (pick == 0){
				newDirection = 'u';
			}
			else if (pick == 1){
				newDirection = 'd';
			}
		}
		else if (direction == 'l'){
			pick = rand.nextInt(5);
			if (pick == 0){
				newDirection = 'u';
			}
			else if (pick == 1){
				newDirection = 'd';
			}
		}
		else if (direction == 'd'){
			pick = rand.nextInt(5);
			if (pick == 0){
				newDirection = 'l';
			}
			else if (pick == 1){
				newDirection = 'r';
			}
		}
		else if (direction == 'u'){
			pick = rand.nextInt(5);
			if (pick == 0){
				newDirection = 'l';
			}
			else if (pick == 1){
				newDirection = 'r';
			}
		}
		else {
			System.out.println("direction error 2");
			System.out.println("direction: " + direction);
		}
		return newDirection;
	}

	private void generateBlockedCells(){
		blockedCells = new ArrayList<Cell>();
		Random rand = new Random();
		int cellsAdded = 0;
		while (cellsAdded < 3840){
			int xCoord = rand.nextInt(NUM_COLUMNS);
			int yCoord = rand.nextInt(NUM_ROWS);

			Cell currentCell = cells[xCoord][yCoord];
			if (currentCell.getType() != Type.UNBLOCKEDRIVER && currentCell.getType() != Type.HARDRIVER){
				currentCell.setType(Type.BLOCKED);
				blockedCells.add(currentCell);
				cellsAdded++;
			}
		}
	}

	private void generateStartGoalCells(){

		Cell currentCell = null;

		ArrayList<Cell> possibleStarts = new ArrayList<Cell>();
		int xCoord = 0;
		int yCoord = 0;
		Type type = null;
		//create list of possible cells
		for (int i = 0; i < NUM_COLUMNS; i++){
			xCoord = i;
			for (int j = 0; j < 20; j++){
				yCoord = j;
				currentCell = cells[xCoord][yCoord];
				type = currentCell.getType();
				if (type == Type.UNBLOCKED || type == Type.HARD){
					possibleStarts.add(currentCell);
				}
			}
		}

		for (int i = 0; i < NUM_COLUMNS; i++){
			xCoord = i;
			for (int j = NUM_ROWS - 1; j > NUM_ROWS - 21; j--){
				yCoord = j;
				currentCell = cells[xCoord][yCoord];
				type = currentCell.getType();
				if (type == Type.UNBLOCKED || type == Type.HARD){
					possibleStarts.add(currentCell);
				}
			}
		}

		for (int i = 20; i < NUM_ROWS - 20; i++){
			yCoord = i;
			for (int j = 0; j < 20; j++){
				xCoord = j;
				currentCell = cells[xCoord][yCoord];
				type = currentCell.getType();
				if (type == Type.UNBLOCKED || type == Type.HARD){
					possibleStarts.add(currentCell);
				}
			}
		}

		for (int i = 20; i < NUM_ROWS - 20; i++){
			yCoord = i;
			for (int j = NUM_COLUMNS - 20; j < NUM_COLUMNS; j++){
				xCoord = j;
				currentCell = cells[xCoord][yCoord];
				type = currentCell.getType();
				if (type == Type.UNBLOCKED || type == Type.HARD){
					possibleStarts.add(currentCell);
				}
			}
		}

		//choose start cell and goal cell
		Random rand = new Random();

		boolean notFoundGoal = true;

		while(notFoundGoal){
			startCell = possibleStarts.get(rand.nextInt(possibleStarts.size()));
			goalCell = possibleStarts.get(rand.nextInt(possibleStarts.size()));

			double distance = findDistance(startCell.getXCoord(), startCell.getYCoord(), goalCell.getXCoord(), goalCell.getYCoord());

			if (distance >= 100){
				notFoundGoal = false;
			}
		}

	}

	public void setNewStartGoal(){
		startCell = null;
		goalCell = null;
		generateStartGoalCells();
	}

	//Euclidean distance
	private double findDistance(int x1, int y1, int x2, int y2){

		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

	}

	private void generateFromFile(File file){
		BufferedReader br = null;
		ArrayList<String> mapDescription = new ArrayList<String>();
		//form arraylist of file contents
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			int count = 1;
			while ((line = br.readLine()) != null){
				mapDescription.add(line);
				//System.out.println("line read " + count);
				count++;
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}

		parseStartGoal(mapDescription);
		parseSeedCells(mapDescription);
		parseCellInfo(mapDescription);

	}

	private void parseStartGoal(ArrayList<String> mapDescription){
		String[] line = mapDescription.get(0).split(" ");

		int xCoord = Integer.parseInt(line[0]);
		int yCoord = Integer.parseInt(line[1]);
		startCell = cells[xCoord][yCoord];

		line = mapDescription.get(1).split(" ");
		xCoord = Integer.parseInt(line[0]);
		yCoord = Integer.parseInt(line[1]);
		goalCell = cells[xCoord][yCoord];

	}

	private void parseSeedCells(ArrayList<String> mapDescription){
		seedCells = new ArrayList<Cell>();
		String[] line = null;
		int xCoord = 0;
		int yCoord = 0;
		for (int i = 2; i < 10; i++){
			line = mapDescription.get(i).split(" ");
			xCoord = Integer.parseInt(line[0]);
			yCoord = Integer.parseInt(line[1]);
			seedCells.add(cells[xCoord][yCoord]);
		}
	}

	private void parseCellInfo(ArrayList<String> mapDescription){
		blockedCells = new ArrayList<Cell>();
		riverCells = new ArrayList<Cell>();
		String[] line = null;

		for (int i = 10; i < 130; i++){
			line = mapDescription.get(i).split(" ");
			for (int j = 0; j < 160; j++){
				char c = line[j].charAt(0);
				int xCoord = j;
				int yCoord = i - 10;
				switch (c) {
					case '0':
						cells[xCoord][yCoord].setType(Type.BLOCKED);
						blockedCells.add(cells[xCoord][yCoord]);
						break;
					case '1':
						//nothing
						break;
					case '2':
						cells[xCoord][yCoord].setType(Type.HARD);
						break;
					case 'a':
						cells[xCoord][yCoord].setType(Type.UNBLOCKEDRIVER);
						riverCells.add(cells[xCoord][yCoord]);
						break;
					case 'b':
						cells[xCoord][yCoord].setType(Type.HARDRIVER);
						riverCells.add(cells[xCoord][yCoord]);
						break;
					default:
						System.out.println("parseCellInfo error");
				}
			}
		}
	}

	public void writeToFile(File file){
		//System.out.println("entered writeToFile()");

		ArrayList<String> contents = new ArrayList<String>();
		String startString = "";
		//System.out.println(startCell.getXCoord() + " " + startCell.getYCoord());
		startString = startString.concat(Integer.toString(startCell.getXCoord()));
		startString = startString.concat(" ");
		startString = startString.concat(Integer.toString(startCell.getYCoord()));
		contents.add(startString);
		//System.out.println("startString: " + startString);

		String goalString = "";
		goalString = goalString.concat(Integer.toString(goalCell.getXCoord()));
		goalString = goalString.concat(" ");
		goalString = goalString.concat(Integer.toString(goalCell.getYCoord()));
		contents.add(goalString);
		//System.out.println("goalString: " + goalString);

		String seedString = null;

		for (int i = 0; i < 8; i++){
			seedString = "";
			seedString = seedString.concat(Integer.toString(seedCells.get(i).getXCoord()));
			seedString = seedString.concat(" ");
			seedString = seedString.concat(Integer.toString(seedCells.get(i).getYCoord()));
			//System.out.println("seedString: " + seedString);
			contents.add(seedString);
		}

		String cellString = null;

		for ( int i = 0; i < NUM_ROWS; i++){
			int yCoord = i;
			cellString = "";
			for (int j = 0; j < NUM_COLUMNS; j++){
				int xCoord = j;
				Cell currentCell = cells[xCoord][yCoord];
				switch (currentCell.getType()){
					case BLOCKED:
						cellString = cellString.concat("0");
						break;
					case UNBLOCKED:
						cellString = cellString.concat("1");
						break;
					case HARD:
						cellString = cellString.concat("2");
						break;
					case UNBLOCKEDRIVER:
						cellString = cellString.concat("a");
						break;
					case HARDRIVER:
						cellString = cellString.concat("b");
						break;
					default:
						System.out.println("cell writing error");
						break;
				}
				cellString = cellString.concat(" ");
			}
			//System.out.println("cellString: " + cellString);
			contents.add(cellString);
		}
		//System.out.println("contents size: " + contents.size());
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), "utf-8"));
			for (int i = 0; i < contents.size(); i++){
				//System.out.println("wrote line");
				writer.write(contents.get(i));
				writer.newLine();
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public ArrayList<Cell> getHardCells(){
		return hardCells;
	}

	public ArrayList<Cell> getRiverCells(){
		return riverCells;
	}

	public ArrayList<Cell> getBlockedCells(){
		return blockedCells;
	}

	public Cell getStartCell(){
		return startCell;
	}

	public Cell getGoalCell(){
		return goalCell;
	}

	public Cell[][] getCells(){
		return cells;
	}

	public ArrayList<Cell> getPathCells(){
		return pathCells;
	}

	public void setPathCells(ArrayList<Cell> pathCells){
		this.pathCells = pathCells;

	}

	public void setCells(Cell[][] cells){
		this.cells = cells;
	}

	public void setStartCell(Cell cell){
		this.startCell = cell;
	}

	public void setGoalCell(Cell cell){
		this.goalCell = cell;
	}
}
