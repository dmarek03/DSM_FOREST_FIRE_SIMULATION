import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point {
	public static Integer []types ={0,1,2,3,4,5};
	private static final int LEVELS = 10;
	private int elevation;
	public boolean litter;
	public boolean floor;
	public boolean understory;
	public boolean coniferous;
	public boolean deciduous;
	private double height;
	public List<Double> state;
	protected List<Double> temperature;
	private List<Point> neighbors;
	private List<Double> nextState;
	protected Double StandardHumidity = 0.5;
	protected Double humidity = 0.5;
	private List<Double> nextTemperature;
	public int currentState;
	private int numStates = 3;
	static Random RND = new Random();
	private double mediumConiferousHeight = 25;
	private double burningTemperature = 400.0;
	private double mediumDeciduousHeight = 35;
	private double mediumUnderstoryHeight;
	private double mediumFloorHeight;
	private double mediumConiferousHeightVariance = 25;
	private double mediumDeciduousHeightVariance = 25;
	private double mediumUnderstoryHeightVariance;
	private double mediumFloorHeightVariance;
	private double mediumLitterHeightVariance;
	private double mediumLitterHeight;
	double w = 0.1;
	double p =  0.05;
	double distance = 10.0;
	public List<Boolean> onFire;
	private double fireGrowthRate = 0.05;
	
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
		elevation = RND.nextInt(100);

		currentState = 0;
		state = new ArrayList<>();
		humidity = 0.4;
		temperature = new ArrayList<>();
		nextState = new ArrayList<>();
		nextTemperature = new ArrayList<>();
		onFire = new ArrayList<>();

		for(int i = 0 ; i < LEVELS; i++){
			state.add(1.0);
			temperature.add(30.0);
			nextState.add(1.0);
			nextTemperature.add(30.0);
			onFire.add(Boolean.FALSE);

			if(RND.nextDouble() < 0.001) {
				temperature.set(i,500.0);
			}
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
		floor = true;
		currentState = 2;

		height = RND.nextGaussian(mediumFloorHeight,Math.sqrt(mediumFloorHeightVariance));
	}

	public void initializeUnderstory() {
		initializeEmpty();
		understory = true;
		currentState = 3;

		height = RND.nextGaussian(mediumUnderstoryHeight,Math.sqrt(mediumUnderstoryHeightVariance));
	}

	public void initializeConiferous() {
		initializeEmpty();
		coniferous = true;
		currentState = 4;

		height = RND.nextGaussian(mediumConiferousHeight,Math.sqrt(mediumConiferousHeightVariance));
	}

	public void initializeDeciduous() {
		initializeEmpty();
		deciduous = true;
		currentState = 5;

		height = RND.nextGaussian(mediumDeciduousHeight,Math.sqrt(mediumDeciduousHeightVariance));
	}
	
	public int getState() {
		return currentState;
	}

	public void update() {
		for(int i = 0; i < LEVELS; i++) {
			if (state.get(i) == 0.0){
				for(int j = 0;j <i ;j ++){
					state.set(j, 0.0);
				}
			}
			state.set(i,nextState.get(i));
			temperature.set(i,nextTemperature.get(i));
			if(temperature.get(i) >= burningTemperature) {
				onFire.set(i,Boolean.TRUE);
			} else {
				onFire.set(i,Boolean.FALSE);
			}
		}
	}
	private double actualBurningTemperature() {

		double k = -0.1;
		return burningTemperature * Math.exp(k * humidity / StandardHumidity);
	}
//
//	public void calculateNewState() {
//		double actualBurnTemp = actualBurningTemperature();
//
//		for (int i = 0; i < LEVELS; i++) {
//			if (onFire.get(i)) {
//				nextState.set(i, state.get(i));
//				nextState.set(i, state.get(i) * (1 - 0.01 * temperature.get(i) / actualBurnTemp));
//				nextTemperature.set(i, temperature.get(i) * (1 + fireGrowthRate) * nextState.get(i));
//			}
//		}
//
//		if (currentState > 0) {
//			for (Point neighbor : neighbors) {
//				for (int i = 0; i < LEVELS; i++) {
//					if (neighbor.temperature.get(i) >= burningTemperature) {
//						if (RND.nextDouble() < 0.05) {
//							nextTemperature.set(i, neighbor.temperature.get(i));
//						}
//					}
//				}
//			}
//		}
//
//		// Implementing the fire spreading up and down
//		for (int i = 0; i < LEVELS - 1; i++) {
//			if (onFire.get(i)) {
//				if(RND.nextDouble() < 0.08) {
//					nextTemperature.set(i + 1, nextTemperature.get(i + 1) * (1 + fireGrowthRate));
//					nextTemperature.set(i, nextTemperature.get(i) * (1 + fireGrowthRate));
//				}
//			}
//		}
//
//		for (int i = LEVELS - 1; i > 0; i--) {
//			if (onFire.get(i)) {
//				if(RND.nextDouble() < 0.08) {
//					nextTemperature.set(i - 1, nextTemperature.get(i - 1) * (1 + fireGrowthRate));
//					nextTemperature.set(i, nextTemperature.get(i) * (1 + fireGrowthRate));
//				}
//			}
//		}
//	}
public void calculateNewState(double windVelocity) {
	double actualBurnTemp = actualBurningTemperature();

	for (int i = 0; i < LEVELS; i++) {
		if (onFire.get(i)) {
			nextState.set(i, state.get(i));
			nextState.set(i, state.get(i) * (1 - 0.01 * temperature.get(i) / actualBurnTemp));
			nextTemperature.set(i, temperature.get(i) * (1 + fireGrowthRate) * nextState.get(i));
		}
	}

	if (currentState > 0) {
		for (Point neighbor : neighbors) {
			for (int i = 0; i < LEVELS; i++) {
				if (neighbor.temperature.get(i) >= burningTemperature) {
					if (RND.nextDouble() < 0.05) {
						nextTemperature.set(i, neighbor.temperature.get(i));
					}
				}
			}
		}
	}

	// Spreading fire up and down
	for (int i = 0; i < LEVELS - 1; i++) {
		if (onFire.get(i)) {
			if(RND.nextDouble() < 0.08) {
				nextTemperature.set(i + 1, nextTemperature.get(i + 1) * (1 + fireGrowthRate));
				nextTemperature.set(i, nextTemperature.get(i) * (1 + fireGrowthRate));
			}
		}
	}

	for (int i = LEVELS - 1; i > 0; i--) {
		if (onFire.get(i)) {
			if(RND.nextDouble() < 0.08) {
				nextTemperature.set(i - 1, nextTemperature.get(i - 1) * (1 + fireGrowthRate));
				nextTemperature.set(i, nextTemperature.get(i) * (1 + fireGrowthRate));
			}
		}
	}

	// Fire spreading with wind
	double angle = calculateFireAngle(windVelocity, w);
	for (Point neighbor : neighbors) {
		double neighborElevation = neighbor.getElevation();
		double newElevation = elevation + height * Math.sin(Math.toRadians(angle));

		if (height * Math.cos(Math.toRadians(angle)) > distance / 2) {
			if (RND.nextDouble() < p) {
				if (neighborElevation <= newElevation) {
					for (int i = 0; i < LEVELS; i++) {
						if (onFire.get(i)) {
							neighbor.nextTemperature.set(i, temperature.get(i));
						}
					}
				}
			}
		}
	}
}

	public double getElevation() {
		return elevation;
	}


	private double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	private double calculateFireAngle(double windVelocity, double w) {
		return 90 * (1 - sigmoid(w * windVelocity));
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
