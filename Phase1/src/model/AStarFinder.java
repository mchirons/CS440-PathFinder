package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import model.Cell.Type;

public class AStarFinder extends PathFinder {

	public AStarFinder(DiscreteMap map, Heuristic heuristic){
		super(map, heuristic);
		//computeHValues();					//maybe only do this for a specific map once?
		//startCell.setF(startCell.getH());
	}

	public void UpdateVertex(Cell s, Cell next, PriorityQueue<Cell> fringe){
		if (s.getH() == 0){
			s.setH(heuristic.computeH(s, goalCell));
		}
		if (next.getH() == 0){
			next.setH(heuristic.computeH(next, goalCell));
		}
		if((s.getG()+cost(s, next))<next.getG()){
			next.setG(s.getG()+cost(s, next));
			next.setParent(s);
			next.setF(next.getG()+next.getH());
			if(fringe.contains(next)){
				fringe.remove(next);
			}
			fringe.add(next);
		}
	}

}
