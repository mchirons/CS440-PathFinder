package model;

public class ConsistentHeuristic extends Heuristic {

	//given heuristic divided by 4 to account for rivers
	public double computeH(Cell s, Cell goal){
		//System.out.println("entered Consistent");
		int xDiff=Math.abs(s.getXCoord()-goal.getXCoord());
		int yDiff=Math.abs(s.getYCoord()-goal.getYCoord());

		return ((Math.sqrt(2)*Math.min(xDiff, yDiff))+Math.max(xDiff, yDiff)-Math.min(xDiff, yDiff)) / 4 ;
	}

}
