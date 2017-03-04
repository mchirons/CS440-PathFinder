package model;

public class Cell {

	private int xCoord;
	private int yCoord;
	private Type type;
	//***************
	private Cell parent;
	private double h;
	private double g;
	private double f;
	//***************

	private boolean closed;
	private boolean generated;
	private boolean inOpen;

	public enum Type{
		UNBLOCKED, HARD, BLOCKED, UNBLOCKEDRIVER, HARDRIVER
	}

	public Cell(int xCoord, int yCoord, Cell.Type type){
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.type = type;
	}

	public void setType(Cell.Type type){
		this.type = type;
	}

	public Cell.Type getType(){
		return type;
	}
	//********************
	public void setH(double H){
		this.h=H;
	}

	public void setG(double G){
		this.g=G;
	}

	public void setParent(Cell Parent){
		this.parent=Parent;
	}

	public void setF(double F){
		this.f=F;
	}

	public void setClosed(boolean closed){
		this.closed = closed;
	}

	public void setInOpen(boolean inOpen){
		this.inOpen = inOpen;
	}

	//***************
	public int getXCoord(){
		return xCoord;
	}

	public int getYCoord(){
		return yCoord;
	}
//	****************
	public Cell getParent(){
		return parent;
	}

	public double getH(){
		return h;
	}

	public double getG(){
		return g;
	}

	public double getF(){
		return f;
	}

	public boolean getClosed(){
		return closed;
	}

	public boolean getInOpen(){
		return inOpen;
	}

	public boolean getGenerated(){
		return generated;
	}

	public void setGenerated(boolean generated){
		this.generated = generated;
	}

	public String getTypeString(){
		if (type == Type.UNBLOCKED){
			return "U";
		}
		else if (type == Type.HARD){
			return "H";
		}
		else if (type == Type.HARDRIVER){
			return "HR";
		}
		else if (type == Type.UNBLOCKEDRIVER){
			return "UR";
		}
		else if (type == Type.BLOCKED){
			return "B";
		}
		else{
			return null;
		}
	}
	//***************
}
