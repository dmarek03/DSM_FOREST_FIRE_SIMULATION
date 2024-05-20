import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point {
	public static Integer []types ={0,1,2,3,4,5};
	private int elevation;
	public boolean litter;
	public boolean floor;
	public boolean understory;
	public boolean coniferous;
	public boolean deciduous;
	private double height;
	private List<Double> state;
	protected List<Double> temperature;
	private List<Point> neighbors;
	public int currentState;
	private int numStates = 3;
	static Random RND = new Random();
	private double mediumConiferousHeight;
	private double mediumDeciduousHeight;
	private double mediumUnderstoryHeight;
	private double mediumFloorHeight;
	private double mediumConiferousHeightVariance;
	private double mediumDeciduousHeightVariance;
	private double mediumUnderstoryHeightVariance;
	private double mediumFloorHeightVariance;
	private double mediumLitterHeightVariance;
	private double mediumLitterHeight;
	
	public Point() {
		this.neighbors = new ArrayList<Point>();
		initializeEmpty();
	}

	public void initializeEmpty() {
		litter = false;
		floor = false;
		understory = false;
		coniferous = false;
		deciduous = false;

		currentState = 0;
		state = new ArrayList<>();
		temperature = new ArrayList<>();

		for(int i = 0 ; i < height; i++){
			state.add(1.0);
			temperature.add(30.0);
		}
	}

	public void initializeLitter() {
		initializeEmpty();
		litter = true;
		currentState = 1;

		height = RND.nextGaussian(mediumLitterHeight,Math.sqrt(mediumLitterHeightVariance));
	}

	public void initializeFloor() {
		initializeEmpty();
		litter = true;
		floor = true;
		currentState = 2;

		height = RND.nextGaussian(mediumFloorHeight,Math.sqrt(mediumFloorHeightVariance));
	}

	public void initializeUnderstory() {
		initializeEmpty();
		litter = true;
		floor = true;
		understory = true;
		currentState = 3;

		height = RND.nextGaussian(mediumUnderstoryHeight,Math.sqrt(mediumUnderstoryHeightVariance));
	}

	public void initializeConiferous() {
		initializeEmpty();
		litter = true;
		floor = true;
		understory = true;
		coniferous = true;
		currentState = 4;

		height = RND.nextGaussian(mediumConiferousHeight,Math.sqrt(mediumConiferousHeightVariance));
	}

	public void initializeDeciduous() {
		initializeEmpty();
		litter = true;
		floor = true;
		understory = true;
		deciduous = true;
		currentState = 5;

		height = RND.nextGaussian(mediumDeciduousHeight,Math.sqrt(mediumDeciduousHeightVariance));
	}
	
	public int getState() {
		return currentState;
	}

	public void calculateNewState() {
		if(currentState > 0) {
			double burningTemperature = 400.0;
			for (Point neighbor : neighbors) {
				if (neighbor.temperature.get(0) >= burningTemperature || neighbor.currentState == 2) {
					if (RND.nextDouble() < 0.05) {
						temperature.set(0, 400.0);
						return;
					}
				}
			}
		}
	}
	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}


    @Override
	public String toString() {
		return "Elevation:" + this.elevation + "\n"
				+ "Has litter:" + this.litter + "\n"
				+ "Has floor:" + this.floor
				+ "Has understory" + this.understory
				+ "Is coniferous " + this.coniferous + "\n"
				+ "Is deciduous " + this.deciduous + "\n"
				+ "Height:" + this.height + "\n"
				+ "States:" + this.state + "\n"
				+ "Temperature:" + this.temperature + "\n";
	}
}
