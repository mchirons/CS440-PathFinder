package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import model.Cell.Type;

//class that describes an informed-search algorithm that utilizes a priority queue and given heuristic
public abstract class PathFinder {

	Cell[][] cells;
	Cell startCell;
	Cell goalCell;
	double weight;
	double w1;
	double w2;
	Heuristic heuristic;
	long timeElapsed;
	int expandedCells;;
	double totalCost;



	public PathFinder(DiscreteMap map, Heuristic heuristic) {
		// TODO Auto-generated constructor stub
		this.cells = map.getCells();
		this.startCell = map.getStartCell();
		this.goalCell = map.getGoalCell();
		this.heuristic = heuristic;
		this.expandedCells = 0;
		this.totalCost = 0;
		this.timeElapsed = 0;
	}

	public abstract void UpdateVertex(Cell s, Cell next, PriorityQueue<Cell> fringe);

	public Cell[][] findPath(){

		long startTime = System.nanoTime();
		long endTime = 0;
		Comparator<Cell> comparator=new PriorityQueueComparator();
		PriorityQueue<Cell> fringe=new PriorityQueue<Cell>(comparator);

		resetCellValues();
		if (heuristic != null){
			startCell.setH(heuristic.computeH(startCell, goalCell));
		}
		else {
			//System.out.println("heuristic is null");
		}
		startCell.setF(startCell.getH());
		fringe.add(startCell);	//f value must be calculated before this
		//need to set parent of startCell to itself?
		//ArrayList<Cell> closed=new ArrayList<Cell>();
		ArrayList<Cell> succ=new ArrayList<Cell>(8);

		while(!fringe.isEmpty()){
			Cell s=fringe.poll();
			if(s.equals(goalCell)){
				//System.out.println("path found!");
				endTime = System.nanoTime();
				timeElapsed = endTime - startTime;
				totalCost = goalCell.getG();
				return cells;
			}
			s.setClosed(true);
			//closed.add(s);
			expandedCells++;
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
			for(int i=0;i<succ.size();i++){
				Cell next=succ.get(i);

				/*
				if(!closed.contains(next)){
					if(next.getType()!=Type.BLOCKED){
						if(!fringe.contains(next)){
							next.setG(1000000);
							next.setParent(null);
						}
						UpdateVertex(s, next, fringe);
					}
				}
				*/
				//optimization - used boolean field for each cell instead of actual list
				if(next.getClosed() == false){
					if(next.getType()!=Type.BLOCKED){
						if(!fringe.contains(next)){
							next.setG(1000000);
							next.setParent(null);
						}
						UpdateVertex(s, next, fringe);
					}
				}

			}
			succ.clear();
		}
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("path not found");
		return null;
	}

	public void resetCellValues(){
		int xCoord;
		int yCoord;

		for (int i = 0; i < cells.length; i++){
			xCoord = i;
			for (int j = 0; j < cells[0].length; j++){
				yCoord = j;
				Cell cell = cells[xCoord][yCoord];
				cell.setClosed(false);
				cell.setF(0);
				cell.setG(0);
				cell.setH(0);
				cell.setParent(null);
			}
		}
	}

	public double cost(Cell s, Cell next){
		Type sType=s.getType();
		Type nextType=next.getType();
		int xs=s.getXCoord();			//current x
		int ys=s.getYCoord();			//current y
		int xnext=next.getXCoord();
		int ynext=next.getYCoord();

		if((sType==Type.UNBLOCKED&&nextType==Type.UNBLOCKED)||(sType==Type.UNBLOCKED&&nextType==Type.UNBLOCKEDRIVER)||(sType==Type.UNBLOCKEDRIVER&&nextType==Type.UNBLOCKED)){
			if(xs!=xnext&&ys!=ynext)
				return Math.sqrt(2);
			else
				return 1;
		}
		else if((sType==Type.UNBLOCKED&&nextType==Type.HARD)||(sType==Type.HARD&&nextType==Type.UNBLOCKED)||(sType==Type.UNBLOCKED&&nextType==Type.HARDRIVER)||(sType==Type.HARDRIVER&&nextType==Type.UNBLOCKED)){
			if(xs!=xnext&&ys!=ynext)
				return (Math.sqrt(2)+Math.sqrt(8))/2;
			else
				return 1.5;
		}
		else if((sType==Type.HARD&&nextType==Type.HARD)||(sType==Type.HARD&&nextType==Type.HARDRIVER)||(sType==Type.HARDRIVER&&nextType==Type.HARD)){
			if(xs!=xnext&&ys!=ynext)
				return Math.sqrt(8);
			else
				return 2;
		}
		else if((sType==Type.UNBLOCKEDRIVER&&nextType==Type.HARDRIVER)||sType==Type.HARDRIVER&&nextType==Type.UNBLOCKEDRIVER){
			if(xs!=xnext&&ys!=ynext)
				return (Math.sqrt(2)+Math.sqrt(8))/2;
			else
				return 0.375;
		}
		else if(sType==Type.UNBLOCKEDRIVER&&nextType==Type.UNBLOCKEDRIVER){
			if(xs!=xnext&&ys!=ynext)
				return Math.sqrt(2);
			else
				return 0.25;
		}
		else if(sType==Type.HARDRIVER&&nextType==Type.HARDRIVER){
			if(xs!=xnext&&ys!=ynext)
				return 2*Math.sqrt(2);
			else
				return 0.5;
		}
		else if (sType == Type.UNBLOCKEDRIVER && nextType == Type.HARD || sType == Type.HARD && nextType == Type.UNBLOCKEDRIVER){
			if(xs!=xnext&&ys!=ynext)
				return (Math.sqrt(2)+Math.sqrt(8))/2;
			else
				return 1.5;
		}
		System.out.println("Cost error from : " + s.getXCoord() + " " + s.getYCoord() + " to: " + next.getXCoord() + " " + next.getYCoord());
		return 0;
	}

	public void setWeight(double weight){
		this.weight = weight;
	}

	public void setW1(double w1){
		this.w1 = w1;
	}

	public void setW2(double w2){
		this.w2 = w2;
	}

	public double getW1(){
		return w1;
	}

	public double getW2(){
		return w2;
	}

	public double getTimeElapsed(){
		return (double)(timeElapsed / 1000.0);
	}

	public int getExpandedCells(){
		return expandedCells;
	}

	public double getTotalCost(){
		return totalCost;
	}

	public double getWeight(){
		return weight;
	}

}
