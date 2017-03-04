package model;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Cell.Type;

public class Grid {

//Grid parameters
	private final int LINE_START = 8;
	private final int LINE_SPACING = 8;
	private final int HOR_LINE_END = 1288;
	private final int VER_LINE_END = 968;
	private final int LOOP_START = 1;
	private final int HOR_LOOP_END = 122;
	private final int VER_LOOP_END = 162;
	private final int CELL_WIDTH_HEIGHT = 8;
	private final double LINE_WIDTH = 1.0;

	/*
	//Grid parameters
	private final int LINE_START = 10;
	private final int LINE_SPACING = 10;
	private final int HOR_LINE_END = 1610;
	private final int VER_LINE_END = 1210;
	private final int LOOP_START = 1;
	private final int HOR_LOOP_END = 122;
	private final int VER_LOOP_END = 162;
	private final int CELL_WIDTH_HEIGHT = 10;
	private final double LINE_WIDTH = 1.0;
	*/

	/* Old dimensions
	private final int LINE_START = 20;
	private final int LINE_SPACING = 20;
	private final int HOR_LINE_END = 3220;
	private final int VER_LINE_END = 2420;
	private final int LOOP_START = 1;
	private final int HOR_LOOP_END = 122;
	private final int VER_LOOP_END = 162;
	private final int CELL_WIDTH_HEIGHT = 20;
	private final double LINE_WIDTH = 1.0;
	*/


	public Grid(){

	}

	public void drawGrid(DiscreteMap map, GraphicsContext gc){

		clearCanvas(gc);
        if (map != null){
        	drawHardCells(map, gc);
        	drawRiverCells(map, gc);
        	drawBlockedCells(map, gc);
        	drawStartGoalCells(map, gc);
        	//drawClickableCell(map, gc);
        }
        drawLines(gc);

	}

	private void drawLines(GraphicsContext gc){
		gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);

        //Draw vertical lines
        for (int i = LOOP_START; i < VER_LOOP_END; i++){
        	gc.strokeLine(i * LINE_SPACING, LINE_START, i * LINE_SPACING, VER_LINE_END);
        }
        //Draw horizontal lines
        for (int i = LOOP_START; i < HOR_LOOP_END; i++){
        	gc.strokeLine(LINE_START, i * LINE_SPACING, HOR_LINE_END, i * LINE_SPACING);
        }
	}

	private void drawHardCells(DiscreteMap map, GraphicsContext gc){
		gc.setFill(Color.GRAY);
		ArrayList<Cell> hardCells = map.getHardCells();
		for (int i = 0; i < hardCells.size(); i++){
			int xCoord = hardCells.get(i).getXCoord();
			int yCoord = hardCells.get(i).getYCoord();
			gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
		}

	}

	private void drawRiverCells(DiscreteMap map, GraphicsContext gc){

		ArrayList<Cell> riverCells = map.getRiverCells();
		for (int i = 0; i < riverCells.size(); i++){
			int xCoord = riverCells.get(i).getXCoord();
			int yCoord = riverCells.get(i).getYCoord();
			Cell cell = riverCells.get(i);
			if (cell.getType() == Type.UNBLOCKEDRIVER){
				gc.setFill(Color.rgb(51, 102, 255));
			}
			else{
				gc.setFill(Color.rgb(0, 45, 179));
			}
			gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
		}

		/*
		Cell[][] cells = map.getCells();
		int xCoord;
		int yCoord;
		//initialize all cells to unblocked
		for (int i = 0; i < 160; i++){
			xCoord = i;
			for (int j = 0; j < 120; j++){
				yCoord = j;
				Cell cell = cells[xCoord][yCoord];
				if (cell.getType() == Type.UNBLOCKEDRIVER){
					gc.setFill(Color.rgb(51, 102, 255));
					gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
				}
				else if (cell.getType() == Type.HARDRIVER){
					gc.setFill(Color.rgb(0, 45, 179));
					gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
				}

			}
		}
		*/


	}

	private void drawBlockedCells(DiscreteMap map, GraphicsContext gc){
		gc.setFill(Color.BLACK);
		ArrayList<Cell> blockedCells = map.getBlockedCells();
		for (int i = 0; i < blockedCells.size(); i++){
			int xCoord = blockedCells.get(i).getXCoord();
			int yCoord = blockedCells.get(i).getYCoord();
			gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
		}

	}

	private void drawStartGoalCells(DiscreteMap map, GraphicsContext gc){
		gc.setFill(Color.web("0xff00ff"));
		Cell startCell = map.getStartCell();
		Cell goalCell = map.getGoalCell();
		int xCoord = startCell.getXCoord();
		int yCoord = startCell.getYCoord();
		gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
		gc.setFill(Color.RED);
		xCoord = goalCell.getXCoord();
		yCoord = goalCell.getYCoord();
		gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
	}

	private void clearCanvas(GraphicsContext gc){
		gc.clearRect(0, 0,  HOR_LINE_END + LINE_SPACING, VER_LINE_END + LINE_SPACING);
	}

		/*
	public void drawPath(DiscreteMap map, GraphicsContext gc){
		gc.setFill(Color.PURPLE);
		ArrayList<Cell> pathCells = map.getPathCells();
		for (int i = 0; i < pathCells.size(); i++){
			int xCoord = pathCells.get(i).getXCoord();
			int yCoord = pathCells.get(i).getYCoord();
			if (pathCells.get(i) != map.getStartCell() && pathCells.get(i) != map.getGoalCell()){
				gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
			}

		}
	}
	*/

	public void drawPath(DiscreteMap map, GraphicsContext gc){
		gc.setFill(Color.PURPLE);
		Cell cell = map.getGoalCell();
		ArrayList<Cell> path = new ArrayList<Cell>();

		int count = 0;
		while (cell != null){
			path.add(cell);
			cell = cell.getParent();
			count++;
			if (count > 19200){
				System.out.println("Path never ending!!!!!!!");
				return;
			}
		}

		for (int i = 0; i < path.size(); i++){
			int xCoord = path.get(i).getXCoord();
			int yCoord = path.get(i).getYCoord();
			if (path.get(i) != map.getStartCell() && path.get(i) != map.getGoalCell()){
				cell = path.get(i);
				if (cell.getType() == Type.UNBLOCKED){
					gc.setFill(Color.web("0xffff66"));
				}
				else if (cell.getType() == Type.UNBLOCKEDRIVER){
					gc.setFill(Color.web("0x8cff66"));
				}
				else if (cell.getType() == Type.HARD){
					gc.setFill(Color.web("0xcccc00"));
				}
				else {
					gc.setFill(Color.web("0x33cc00"));
				}
				gc.fillRect((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
			}
		}

	}


	public ArrayList<Rectangle> createClickCells(int columns, int rows){
		ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
		for (int i = 0; i < columns; i++){
			int xCoord = i;
			for (int j = 0; j < rows; j++){
				int yCoord = j;
				Rectangle rect = new Rectangle((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
				rect.setFill(Color.TRANSPARENT);
				rect.relocate((xCoord + 1) * LINE_SPACING, (yCoord + 1) * LINE_SPACING);
				//System.out.println("rectangle added: " + rect.getX() + " " + rect.getY());
				rectangles.add(rect);
			}
		}
		return rectangles;
	}

}
