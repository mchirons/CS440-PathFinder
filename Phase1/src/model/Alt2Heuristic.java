package model;

//Euclidean Distance
public class Alt2Heuristic extends Heuristic{
	public double computeH(Cell s, Cell goal){

		//System.out.println("entered Alt2");

		int x1 = s.getXCoord();
		int y1 = s.getYCoord();

		int x2 = goal.getXCoord();
		int y2 = goal.getYCoord();

		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
}
