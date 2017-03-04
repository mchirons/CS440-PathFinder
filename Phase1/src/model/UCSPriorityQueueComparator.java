package model;

import java.util.Comparator;

public class UCSPriorityQueueComparator implements Comparator<Cell>{
	public int compare(Cell x, Cell y){
		if(x.getF()<y.getF())
			return -1;
		else if(x.getF()>y.getF())
			return 1;
		else if (x.getF() == y.getF()){
			if (x.getG() > y.getG()){
				return -1;
			}
			else if (x.getG() < y.getG()){
				return 1;
			}
			else if (x.getG() == y.getG()){		//go with y by default
				return 1;
			}
		}
		return 0;
	}

}
