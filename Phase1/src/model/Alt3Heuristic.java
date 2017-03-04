package model;

//Manhattan Distance
public class Alt3Heuristic extends Heuristic{
	public double computeH(Cell s, Cell goal){

		//System.out.println("entered Alt3");

		int x1 = s.getXCoord();
		int y1 = s.getYCoord();

		int x2 = goal.getXCoord();
		int y2 = goal.getYCoord();

		return Math.abs(y2 - y1) + Math.abs(x2 - x1);
	}
}
