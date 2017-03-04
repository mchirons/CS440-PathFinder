package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import model.AStarFinder;
import model.AStarIntegratedFinder;
import model.AStarSequentialFinder;
import model.AStarWeightedFinder;
import model.Alt1Heuristic;
import model.Alt2Heuristic;
import model.Alt3Heuristic;
import model.Alt4Heuristic;
import model.ConsistentHeuristic;
import model.DiscreteMap;
import model.Heuristic;
import model.PathFinder;
import model.UniformCostFinder;

//generates report of path-finding algorithm tests
public class TestBench {

	//generate 5 maps
	ArrayList<File> files;
	ArrayList<DiscreteMap> maps;

	//weights for A* weighted
	double w1;
	double w2;

	//w1 and w2 for sequential and integrated
	double w1A;
	double w1B;
	double w1C;
	double w1D;

	double w2A;
	double w2B;
	double w2C;
	double w2D;

	//avg times
	double aStarConsistentTime;
	double aStarAlt1Time;
	double aStarAlt2Time;
	double aStarAlt3Time;
	double aStarAlt4Time;

	double aStarWeightedConsistentW1Time;
	double aStarWeightedAlt1W1Time;
	double aStarWeightedAlt2W1Time;
	double aStarWeightedAlt3W1Time;
	double aStarWeightedAlt4W1Time;

	double aStarWeightedConsistentW2Time;
	double aStarWeightedAlt1W2Time;
	double aStarWeightedAlt2W2Time;
	double aStarWeightedAlt3W2Time;
	double aStarWeightedAlt4W2Time;

	double aStarSequentialV1Time;
	double aStarSequentialV2Time;

	double aStarIntegratedV1Time;
	double aStarIntegratedV2Time;

	double UniformCostTime;

	//avg expandedNodes
	int aStarConsistentNodes;
	int aStarAlt1Nodes;
	int aStarAlt2Nodes;
	int aStarAlt3Nodes;
	int aStarAlt4Nodes;

	int aStarWeightedConsistentW1Nodes;
	int aStarWeightedAlt1W1Nodes;
	int aStarWeightedAlt2W1Nodes;
	int aStarWeightedAlt3W1Nodes;
	int aStarWeightedAlt4W1Nodes;

	int aStarWeightedConsistentW2Nodes;
	int aStarWeightedAlt1W2Nodes;
	int aStarWeightedAlt2W2Nodes;
	int aStarWeightedAlt3W2Nodes;
	int aStarWeightedAlt4W2Nodes;

	int UniformCostNodes;

	double aStarSequentialV1Nodes;
	double aStarSequentialV2Nodes;

	double aStarIntegratedV1Nodes;
	double aStarIntegratedV2Nodes;

	//avg costs
	double aStarConsistentCost;
	double aStarAlt1Cost;
	double aStarAlt2Cost;
	double aStarAlt3Cost;
	double aStarAlt4Cost;

	double aStarWeightedConsistentW1Cost;
	double aStarWeightedAlt1W1Cost;
	double aStarWeightedAlt2W1Cost;
	double aStarWeightedAlt3W1Cost;
	double aStarWeightedAlt4W1Cost;

	double aStarWeightedConsistentW2Cost;
	double aStarWeightedAlt1W2Cost;
	double aStarWeightedAlt2W2Cost;
	double aStarWeightedAlt3W2Cost;
	double aStarWeightedAlt4W2Cost;

	double UniformCostCost;

	double aStarSequentialV1Cost;
	double aStarSequentialV2Cost;

	double aStarIntegratedV1Cost;
	double aStarIntegratedV2Cost;




	public void runExperiment(){
		TestBench testbench = new TestBench();
		testbench.createMaps();
		testbench.testMaps();

	}

	//for each of the 5 create 4 additional maps that only differ in start goal pairs
	private void createMaps(){
		int mapNumber = 0;
		int versionNumber = 0;

		files = new ArrayList<File>();
		maps = new ArrayList<DiscreteMap>();

		//create 50 maps and save to file
		DiscreteMap[] baseMaps = new DiscreteMap[5];
		for (int i = 0; i < 5; i++){
			String fileName = "map" + mapNumber + "_v" + versionNumber;
			versionNumber++;
			baseMaps[i] = new DiscreteMap();
			File file = new File("test", fileName);
			if (file.exists()){
				file.delete();
				try {
					file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			baseMaps[i].writeToFile(file);
			files.add(file);
			for (int j = 1; j < 10; j++){
				baseMaps[i].setNewStartGoal();
				fileName = "map" + mapNumber + "_v" + versionNumber;
				versionNumber++;
				file = new File("test", fileName);
				if (file.exists()){
					file.delete();
					try {
						file.createNewFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				baseMaps[i].writeToFile(file);
				files.add(file);
			}
			mapNumber++;
			versionNumber = 0;
		}


	}
	//for all of the 50 resulting maps test each algorithm
	private void testMaps(){

		for (int i = 0; i < files.size(); i++){
			System.out.println("testing map: " + i);
			DiscreteMap map = new DiscreteMap(files.get(i));
			testAStar(map);
			testAStarWeighted(map);
			testUniformCost(map);
			testAStarSequential(map);
			testAStarIntegrated(map);
		}
		System.out.println("");

		saveTestResults();
	}

	private void saveTestResults(){
		ArrayList<String> contents = new ArrayList<String>();
		contents.add("------------------------------------------------------");
		contents.add("Average Times: (ms)");
		contents.add(" ");
		contents.add("A* (no-weight)");
		contents.add("Consistent: " + aStarConsistentTime / 50);
		contents.add("Alt1: " + aStarAlt1Time / 50);
		contents.add("Alt2: " + aStarAlt2Time / 50);
		contents.add("Alt3: " + aStarAlt3Time / 50);
		contents.add("Alt4: " + aStarAlt4Time / 50);
		contents.add(" ");

		contents.add("A* with weight = " + w1);
		contents.add("Consistent: " + aStarWeightedConsistentW1Time / 50);
		contents.add("Alt1: " + aStarWeightedAlt1W1Time / 50);
		contents.add("Alt2: " + aStarWeightedAlt2W1Time / 50);
		contents.add("Alt3: " + aStarWeightedAlt3W1Time / 50);
		contents.add("Alt4: " + aStarWeightedAlt4W1Time / 50);
		contents.add(" ");

		contents.add("A* with weight = " + w2);
		contents.add("Consistent: " + aStarWeightedConsistentW2Time / 50);
		contents.add("Alt1: " + aStarWeightedAlt1W2Time / 50);
		contents.add("Alt2: " + aStarWeightedAlt2W2Time / 50);
		contents.add("Alt3: " + aStarWeightedAlt3W2Time / 50);
		contents.add("Alt4: " + aStarWeightedAlt4W2Time / 50);
		contents.add(" ");

		contents.add("Uniform-Cost: " + UniformCostTime / 50);
		contents.add(" ");

		contents.add("A* Sequential with w1 = " + w1A + " w2 = " + w2A + ": " + aStarSequentialV1Time / 50);
		contents.add(" ");

		contents.add("A* Sequential with w1 = " + w1B + " w2 = " + w2B + ": " + aStarSequentialV2Time / 50);
		contents.add(" ");

		contents.add("A* Integrated with w1 = " + w1C + " w2 = " + w2C + ": " + aStarIntegratedV1Time / 50);
		contents.add(" ");

		contents.add("A* Integrated with w1 = " + w1D + " w2 = " + w2D + ": " + aStarIntegratedV2Time / 50);
		contents.add(" ");

		contents.add("------------------------------------------------------");
		contents.add("Average Nodes:");
		contents.add(" ");
		contents.add("A* Consistent: " + aStarConsistentNodes / 50);
		contents.add("A* Alt1: " + aStarAlt1Nodes / 50);
		contents.add("A* Alt2: " + aStarAlt2Nodes / 50);
		contents.add("A* Alt3: " + aStarAlt3Nodes / 50);
		contents.add("A* Alt4: " + aStarAlt4Nodes / 50);
		contents.add(" ");

		contents.add("A* with weight = " + w1);
		contents.add("Consistent: " + aStarWeightedConsistentW1Nodes / 50);
		contents.add("Alt1: " + aStarWeightedAlt1W1Nodes / 50);
		contents.add("Alt2: " + aStarWeightedAlt2W1Nodes / 50);
		contents.add("Alt3: " + aStarWeightedAlt3W1Nodes / 50);
		contents.add("Alt4: " + aStarWeightedAlt4W1Nodes / 50);
		contents.add(" ");

		contents.add("A* with weight = " + w2);
		contents.add("Consistent: " + aStarWeightedConsistentW2Nodes / 50);
		contents.add("Alt1: " + aStarWeightedAlt1W2Nodes / 50);
		contents.add("Alt2: " + aStarWeightedAlt2W2Nodes / 50);
		contents.add("Alt3: " + aStarWeightedAlt3W2Nodes / 50);
		contents.add("Alt4: " + aStarWeightedAlt4W2Nodes / 50);
		contents.add(" ");

		contents.add("Uniform-Cost: " + UniformCostNodes / 50);
		contents.add(" ");

		contents.add("A* Sequential with w1 = " + w1A + " w2 = " + w2A + ": " + aStarSequentialV1Nodes / 50);
		contents.add(" ");

		contents.add("A* Sequential with w1 = " + w1B + " w2 = " + w2B + ": " + aStarSequentialV2Nodes / 50);
		contents.add(" ");

		contents.add("A* Integrated with w1 = " + w1C + " w2 = " + w2C + ": " + aStarIntegratedV1Nodes / 50);
		contents.add(" ");

		contents.add("A* Integrated with w1 = " + w1D + " w2 = " + w2D + ": " + aStarIntegratedV2Nodes / 50);
		contents.add(" ");

		contents.add("------------------------------------------------------");
		contents.add("Average Cost:");
		contents.add(" ");
		contents.add("A* Consistent: " + aStarConsistentCost / 50);
		contents.add("A* Alt1: " + aStarAlt1Cost / 50);
		contents.add("A* Alt2: " + aStarAlt2Cost / 50);
		contents.add("A* Alt3: " + aStarAlt3Cost / 50);
		contents.add("A* Alt4: " + aStarAlt4Cost / 50);
		contents.add(" ");

		contents.add("A* with weight = " + w1);
		contents.add("Consistent: " + aStarWeightedConsistentW1Cost / 50);
		contents.add("Alt1: " + aStarWeightedAlt1W1Cost / 50);
		contents.add("Alt2: " + aStarWeightedAlt2W1Cost / 50);
		contents.add("Alt3: " + aStarWeightedAlt3W1Cost / 50);
		contents.add("Alt4: " + aStarWeightedAlt4W1Cost / 50);
		contents.add(" ");

		contents.add("A* with weight = " + w2);
		contents.add("Consistent: " + aStarWeightedConsistentW2Cost / 50);
		contents.add("Alt1: " + aStarWeightedAlt1W2Cost / 50);
		contents.add("Alt2: " + aStarWeightedAlt2W2Cost / 50);
		contents.add("Alt3: " + aStarWeightedAlt3W2Cost / 50);
		contents.add("Alt4: " + aStarWeightedAlt4W2Cost / 50);
		contents.add(" ");

		contents.add("Uniform-Cost: " + UniformCostCost / 50);
		contents.add(" ");

		contents.add("A* Sequential with w1 = " + w1A + " w2 = " + w2A + ": " + aStarSequentialV1Cost / 50);
		contents.add(" ");

		contents.add("A* Sequential with w1 = " + w1B + " w2 = " + w2B + ": " + aStarSequentialV2Cost / 50);
		contents.add(" ");

		contents.add("A* Integrated with w1 = " + w1C + " w2 = " + w2C + ": " + aStarIntegratedV1Cost / 50);
		contents.add(" ");

		contents.add("A* Integrated with w1 = " + w1D + " w2 = " + w2D + ": " + aStarIntegratedV2Cost / 50);
		contents.add(" ");

		for (int i = 0; i < contents.size(); i++){
			System.out.println(contents.get(i));
		}

		File file = new File("test", "testData");
		if (file.exists()){
			file.delete();
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), "utf-8"));
			for (int i = 0; i < contents.size(); i++){
				//System.out.println("wrote line");
				writer.write(contents.get(i));
				writer.newLine();
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void testAStar(DiscreteMap map){
		Heuristic h;
		PathFinder p;
		//aStarConsistent
		h = new ConsistentHeuristic();
		p = new AStarFinder(map, h);
		p.findPath();
		aStarConsistentTime += p.getTimeElapsed();
		aStarConsistentNodes += p.getExpandedCells();
		aStarConsistentCost += p.getTotalCost();

		//aStarAlt1
		h = new Alt1Heuristic();
		p = new AStarFinder(map, h);
		p.findPath();
		aStarAlt1Time += p.getTimeElapsed();
		aStarAlt1Nodes += p.getExpandedCells();
		aStarAlt1Cost += p.getTotalCost();

		//aStarAlt2
		h = new Alt2Heuristic();
		p = new AStarFinder(map, h);
		p.findPath();
		aStarAlt2Time += p.getTimeElapsed();
		aStarAlt2Nodes += p.getExpandedCells();
		aStarAlt2Cost += p.getTotalCost();

		//aStarAlt3
		h = new Alt3Heuristic();
		p = new AStarFinder(map, h);
		p.findPath();
		aStarAlt3Time += p.getTimeElapsed();
		aStarAlt3Nodes += p.getExpandedCells();
		aStarAlt3Cost += p.getTotalCost();

		//aStarAlt4
		h = new Alt4Heuristic();
		p = new AStarFinder(map, h);
		p.findPath();
		aStarAlt4Time += p.getTimeElapsed();
		aStarAlt4Nodes += p.getExpandedCells();
		aStarAlt4Cost += p.getTotalCost();

	}

	private void testAStarWeighted(DiscreteMap map){
		Heuristic h;
		PathFinder p;


		//weight 1
		w1 = 1.5;

		w2 = 4.0;
		//aStarWeightConsistent
		h = new ConsistentHeuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w1);
		p.findPath();
		aStarWeightedConsistentW1Time += p.getTimeElapsed();
		aStarWeightedConsistentW1Nodes += p.getExpandedCells();
		aStarWeightedConsistentW1Cost += p.getTotalCost();

		//aStarAlt1
		h = new Alt1Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w1);
		p.findPath();
		aStarWeightedAlt1W1Time += p.getTimeElapsed();
		aStarWeightedAlt1W1Nodes += p.getExpandedCells();
		aStarWeightedAlt1W1Cost += p.getTotalCost();

		//aStarAlt2
		h = new Alt2Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w1);
		p.findPath();
		aStarWeightedAlt2W1Time += p.getTimeElapsed();
		aStarWeightedAlt2W1Nodes += p.getExpandedCells();
		aStarWeightedAlt2W1Cost += p.getTotalCost();

		//aStarAlt3
		h = new Alt3Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w1);
		p.findPath();
		aStarWeightedAlt3W1Time += p.getTimeElapsed();
		aStarWeightedAlt3W1Nodes += p.getExpandedCells();
		aStarWeightedAlt3W1Cost += p.getTotalCost();

		//aStarAlt4
		h = new Alt4Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w1);
		p.findPath();
		aStarWeightedAlt4W1Time += p.getTimeElapsed();
		aStarWeightedAlt4W1Nodes += p.getExpandedCells();
		aStarWeightedAlt4W1Cost += p.getTotalCost();


		//weight 2

		//aStarConsistent
		h = new ConsistentHeuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w2);
		p.findPath();
		aStarWeightedConsistentW2Time += p.getTimeElapsed();
		aStarWeightedConsistentW2Nodes += p.getExpandedCells();
		aStarWeightedConsistentW2Cost += p.getTotalCost();

		//aStarAlt1
		h = new Alt1Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w2);
		p.findPath();
		aStarWeightedAlt1W2Time += p.getTimeElapsed();
		aStarWeightedAlt1W2Nodes += p.getExpandedCells();
		aStarWeightedAlt1W2Cost += p.getTotalCost();

		//aStarAlt2
		h = new Alt2Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w2);
		p.findPath();
		aStarWeightedAlt2W2Time += p.getTimeElapsed();
		aStarWeightedAlt2W2Nodes += p.getExpandedCells();
		aStarWeightedAlt2W2Cost += p.getTotalCost();

		//aStarAlt3
		h = new Alt3Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w2);
		p.findPath();
		aStarWeightedAlt3W2Time += p.getTimeElapsed();
		aStarWeightedAlt3W2Nodes += p.getExpandedCells();
		aStarWeightedAlt3W2Cost += p.getTotalCost();

		//aStarAlt4
		h = new Alt4Heuristic();
		p = new AStarWeightedFinder(map, h);
		p.setWeight(w2);
		p.findPath();
		aStarWeightedAlt4W2Time += p.getTimeElapsed();
		aStarWeightedAlt4W2Nodes += p.getExpandedCells();
		aStarWeightedAlt4W2Cost += p.getTotalCost();

	}

	private void testUniformCost(DiscreteMap map){
		Heuristic h;
		PathFinder p;
		//aStarConsistent
		h = null;
		p = new UniformCostFinder(map, h);
		p.findPath();
		UniformCostTime += p.getTimeElapsed();
		UniformCostNodes += p.getExpandedCells();
		UniformCostCost += p.getTotalCost();
	}

	//for algorithms that used heuristics try different heuristics for each algorithm
	//save each map
	//save average test info to file


	private void testAStarSequential(DiscreteMap map){
		Heuristic h;
		PathFinder p;

		w1A = 1.25;
		w2A = 2.0;
		h = null;
		p = new AStarSequentialFinder(map, h);
		p.setW1(w1A);
		p.setW2(w2A);
		p.findPath();
		aStarSequentialV1Time += p.getTimeElapsed();
		aStarSequentialV1Nodes += p.getExpandedCells();
		aStarSequentialV1Cost += p.getTotalCost();

		w1B = 2.0;
		w2B = 3.0;
		h = null;
		p = new AStarSequentialFinder(map, h);
		p.setW1(w1B);
		p.setW2(w2B);
		p.findPath();
		aStarSequentialV2Time += p.getTimeElapsed();
		aStarSequentialV2Nodes += p.getExpandedCells();
		aStarSequentialV2Cost += p.getTotalCost();


	}

	private void testAStarIntegrated(DiscreteMap map){
		Heuristic h;
		PathFinder p;



		w1C = 1.25;
		w2C = 2.0;
		h = null;
		p = new AStarIntegratedFinder(map, h);
		p.setW1(w1C);
		p.setW2(w2C);
		p.findPath();
		aStarIntegratedV1Time += p.getTimeElapsed();
		aStarIntegratedV1Nodes += p.getExpandedCells();
		aStarIntegratedV1Cost += p.getTotalCost();

		w1D = 2.0;
		w2D = 3.0;
		h = null;
		p = new AStarIntegratedFinder(map, h);
		p.setW1(w1D);
		p.setW2(w1D);
		p.findPath();
		aStarIntegratedV2Time += p.getTimeElapsed();
		aStarIntegratedV2Nodes += p.getExpandedCells();
		aStarIntegratedV2Cost += p.getTotalCost();


	}
}
