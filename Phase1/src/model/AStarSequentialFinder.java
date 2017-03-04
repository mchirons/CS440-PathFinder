package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import model.Cell.Type;

public class AStarSequentialFinder extends PathFinder{


	private ArrayList<Cell[][]>cellsList;
	private ArrayList<PriorityQueue<Cell>> queues;
	private ArrayList<Cell> startCells;
	private ArrayList<Cell> goalCells;

	public AStarSequentialFinder(DiscreteMap map, Heuristic heuristic){
		super(map, heuristic);
		//computeHValues();					//maybe only do this for a specific map once?
		//startCell.setF(startCell.getH());
	}

	private void expandState(Cell s, int index){

		expandedCells++;

		//set correct heuristic per search
		Heuristic heuristic = null;

		if (index == 0){
			heuristic = new ConsistentHeuristic();
		}
		else if (index == 1){
			heuristic = new Alt1Heuristic();
		}
		else if (index == 2){
			heuristic = new Alt2Heuristic();
				}
		else if (index == 3){
			heuristic = new Alt3Heuristic();
		}
		else if (index == 4){
			heuristic = new Alt4Heuristic();
		}

		//get successors for s
		ArrayList<Cell> succ = getSucc(s, index);

		for (int i = 0; i < succ.size(); i++){
			Cell sprime = succ.get(i);

			//make sure s and sprime have h values
			if (s.getH() == 0){
				s.setH(heuristic.computeH(s, goalCells.get(index)));
			}
			if (sprime.getH() == 0){
				sprime.setH(heuristic.computeH(sprime, goalCells.get(index)));
			}

			if (sprime.getType() != Type.BLOCKED){
				if (sprime.getGenerated() == false){
					sprime.setG(1000000);
					sprime.setParent(null);
				}
				if (sprime.getG() > s.getG() + cost(s, sprime)){
					sprime.setG(s.getG() + cost(s,sprime));
					sprime.setParent(s);

					if (sprime.getXCoord() == startCell.getXCoord() && sprime.getYCoord() == startCell.getYCoord()){
						//System.out.println("sprime is startCell with parent + " + sprime.getParent().getXCoord() + " " + sprime.getParent().getYCoord() + " , BAD");
						System.exit(0);
					}

					if (sprime.getClosed() == false){

						sprime.setF(sprime.getG() + w1 * sprime.getH());
						queues.get(index).add(sprime);
						//System.out.println("adding to SEARCH: " + index + " cell: " + sprime.getXCoord() + " " + sprime.getYCoord());
						//insert into open queue
					}
				}
			}
			sprime.setGenerated(true);
		}
		succ.clear();
	}

	//might need to return goal node on success
	public Cell[][] findPath(){



		//makes a copy of the maps cells for each search heuristic variation
		//also changes g value of goal cell in each to be 1000000 (infinity)
		copyCells();

		//creates a priority queue for each search heuristic variation
		initializeQueues();

		long startTime = System.nanoTime();
		long endTime = 0;

		PriorityQueue<Cell> OPEN0 = queues.get(0), OPENi;



		if (startCells.get(0).getParent() == null){
			//System.out.println("parent of start cell is null, GOOD");
		}
		else {
			System.out.println("parent of start cell is NOT null, BAD");
			return null;
		}

		while (OPEN0.peek().getF() < 1000000){

			for (int i = 1; i < 5; i++){
				//System.out.println("current queue: " + i);

				OPENi = queues.get(i);
				//System.out.println("OPEN0 minkey: " + OPEN0.peek().getF() + " OPEN" + i + " minkey: " + OPENi.peek().getF());
				if (OPENi.peek().getF() <= w2 * OPEN0.peek().getF()){
					//g value of goal cell in this searches copy of cells
					double gi = goalCells.get(i).getG();
					//System.out.println("gi: " + gi);
					//if g value of goal cell is <= to f value of cell at top of THIS search's priority queue (success)
					if (gi <= OPENi.peek().getF()){
						if (gi < 1000000){

							endTime = System.nanoTime();
							timeElapsed = endTime - startTime;
							totalCost = goalCells.get(i).getG();

							//use bpi path
							//System.out.println("path found from SEARCH: " + i);
							//System.out.println("path node number = " + getPath(goalCells.get(i)).size());
							return cellsList.get(i);				//returns THIS search's version of cells
						}
					}
					else{											//pop top cell of THIS search's priority queue and expand
						//System.out.println("popping from SEARCH: " + i + " cell: " + OPENi.peek().getXCoord() + " " + OPENi.peek().getYCoord());
						//Cell s = OPENi.peek();
						Cell s = OPENi.poll();
						expandState(s, i);
						s.setClosed(true);
					}
				}
				else{
					//g value of goal cell in OPTIMAL searches copy of cells
					double g0 = goalCells.get(0).getG();

					//g value of goal cell is <= to f value of cell at top of OPTIMAL search priority queue (success)
					//System.out.println("g0: " + g0);
					if (g0 <= OPEN0.peek().getF()){
						if(g0 < 1000000){

							endTime = System.nanoTime();
							timeElapsed = endTime - startTime;
							totalCost = goalCells.get(0).getG();

							//use bp0 path
							//System.out.println("path found from SEARCH: 0");
							//System.out.println("path node number = " + getPath(goalCells.get(0)).size());
							return cellsList.get(0);				//returns OPTIMAL search's version of cells
						}
					}
					else{
						//System.out.println("popping from SEARCH: 0 cell: " + OPEN0.peek().getXCoord() + " " + OPEN0.peek().getYCoord());
						//Cell s = OPEN0.peek();
						Cell s = OPEN0.poll();						//pop top cell of OPTIMAL search's priority queue and expand
						expandState(s, 0);
						s.setClosed(true);
					}
				}
			}
		}
		//System.out.println("size of queue 0: " + queues.get(0).size());
		//System.out.println("size of queue 1: " + queues.get(1).size());
		//System.out.println("size of queue 2: " + queues.get(2).size());
		//System.out.println("size of queue 3: " + queues.get(3).size());
		//System.out.println("size of queue 4: " + queues.get(4).size());

		return null;
	}


	public void UpdateVertex(Cell s, Cell next, PriorityQueue<Cell> fringe){
		//for compiler
	}

	private void copyCells(){
		Cell[][] cells;
		startCells = new ArrayList<Cell>();
		goalCells = new ArrayList<Cell>();
		cellsList = new ArrayList<Cell[][]>();

		for (int k = 0; k < 5; k++){

			cells = new Cell[this.cells.length][this.cells[0].length];
			for (int i = 0; i < this.cells.length; i++){
				int xCoord = i;
				for (int j = 0; j < this.cells[0].length; j++){
					int yCoord = j;
					cells[xCoord][yCoord] = new Cell(xCoord, yCoord, this.cells[xCoord][yCoord].getType());
				}
			}

			Heuristic heuristic = null;

			if (k == 0){
				heuristic = new ConsistentHeuristic();
			}
			else if (k == 1){
				heuristic = new Alt1Heuristic();
			}
			else if (k == 2){
				heuristic = new Alt2Heuristic();
					}
			else if (k == 3){
				heuristic = new Alt3Heuristic();
			}
			else if (k == 4){
				heuristic = new Alt4Heuristic();
			}

			Cell startCell = cells[this.startCell.getXCoord()][this.startCell.getYCoord()];


			Cell goalCell = cells[this.goalCell.getXCoord()][this.goalCell.getYCoord()];
			goalCell.setG(1000000);
			goalCell.setParent(null);
			goalCells.add(goalCell);

			startCell.setH(heuristic.computeH(startCell, goalCell));
			startCell.setF(startCell.getG() + w1 * startCell.getH());
			startCell.setParent(null);
			startCell.setG(0);
			startCell.setGenerated(true);
			startCells.add(startCell);

			cellsList.add(cells);
		}

	}

	private void initializeQueues(){
		Comparator<Cell> comparator=new PriorityQueueComparator();
		queues = new ArrayList<PriorityQueue<Cell>>();
		for (int i = 0; i < 5; i++){

			PriorityQueue<Cell> OPEN = new PriorityQueue<Cell>(comparator);

			//insert copies start cell into corresponding priority queues
			OPEN.add(startCells.get(i));
			queues.add(OPEN);

		}


	}

	private ArrayList<Cell> getSucc(Cell s, int index){

		Cell[][] cells = cellsList.get(index);

		ArrayList<Cell> succ = new ArrayList<Cell>(8);

		int x=s.getXCoord();
		int y=s.getYCoord();
		if(x!=0&&x!=159&&y!=0&&y!=119){
			succ.add(cells[x-1][y-1]);
			succ.add(cells[x][y-1]);
			succ.add(cells[x+1][y-1]);
			succ.add(cells[x-1][y]);
			succ.add(cells[x+1][y]);
			succ.add(cells[x-1][y+1]);
			succ.add(cells[x][y+1]);
			succ.add(cells[x+1][y+1]);
		}
		else if(x!=0&&x!=159&&y!=0){
			succ.add(cells[x-1][y-1]);
			succ.add(cells[x][y-1]);
			succ.add(cells[x+1][y-1]);
			succ.add(cells[x-1][y]);
			succ.add(cells[x+1][y]);
		}
		else if(x!=0&&x!=159&&y!=119){
			succ.add(cells[x-1][y]);
			succ.add(cells[x+1][y]);
			succ.add(cells[x-1][y+1]);
			succ.add(cells[x][y+1]);
			succ.add(cells[x+1][y+1]);
		}
		else if(x!=0&&y!=0&&y!=119){
			succ.add(cells[x-1][y-1]);
			succ.add(cells[x][y-1]);
			succ.add(cells[x-1][y]);
			succ.add(cells[x-1][y+1]);
			succ.add(cells[x][y+1]);
		}
		else if(x!=159&&y!=0&&y!=119){
			succ.add(cells[x][y-1]);
			succ.add(cells[x+1][y-1]);
			succ.add(cells[x+1][y]);
			succ.add(cells[x][y+1]);
			succ.add(cells[x+1][y+1]);
		}
		else if(x==0&&y==0){
			succ.add(cells[x+1][y]);
			succ.add(cells[x][y+1]);
			succ.add(cells[x+1][y+1]);
		}
		else if(x==0&&y==119){
			succ.add(cells[x][y-1]);
			succ.add(cells[x+1][y-1]);
			succ.add(cells[x+1][y]);
		}
		else if(x==159&&y==119){
			succ.add(cells[x-1][y-1]);
			succ.add(cells[x][y-1]);
			succ.add(cells[x-1][y]);
		}
		else if(x==159&&y==0){
			succ.add(cells[x-1][y]);
			succ.add(cells[x-1][y+1]);
			succ.add(cells[x][y+1]);
		}

		return succ;
	}

	private ArrayList<Cell> getPath(Cell goalCell){
		int count = 0;
		ArrayList<Cell> path = new ArrayList<Cell>();
		Cell cell = goalCell;
		while (cell != null && count < 200){
			path.add(cell);
			//System.out.println("cellX: " + cell.getXCoord() + " cellY: " + cell.getYCoord());
			cell = cell.getParent();
			count++;
		}
		return path;
	}
}
