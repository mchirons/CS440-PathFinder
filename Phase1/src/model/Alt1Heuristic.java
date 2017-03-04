package model;


//Given heuristic
public class Alt1Heuristic extends Heuristic {
	public double computeH(Cell s, Cell goal){
		//System.out.println("entered Alt1");
		int xDiff=Math.abs(s.getXCoord()-goal.getXCoord());
		int yDiff=Math.abs(s.getYCoord()-goal.getYCoord());

		return (Math.sqrt(2)*Math.min(xDiff, yDiff))+Math.max(xDiff, yDiff)-Math.min(xDiff, yDiff);
	}
}
