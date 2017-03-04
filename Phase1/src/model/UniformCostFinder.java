package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import model.Cell.Type;

public class UniformCostFinder extends PathFinder{

	public UniformCostFinder(DiscreteMap map, Heuristic heuristic){
		super(map, heuristic);
	}

	public void UpdateVertex(Cell s, Cell next, PriorityQueue<Cell> fringe){
		if((s.getG()+cost(s, next))<next.getG()){
			next.setG(s.getG()+cost(s, next));
			next.setParent(s);
			next.setF(next.getG());
			if(fringe.contains(next))
				fringe.remove(next);
			fringe.add(next);
		}
	}

}
