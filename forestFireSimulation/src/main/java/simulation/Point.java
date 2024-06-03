package simulation;

import simulation.records.PointStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point {
    public static Integer[] types = {0, 1, 2, 3, 4, 5, 6};
    public static final int LEVELS = 10;
    private int elevation;
    public boolean litter;
    public boolean floor;
    public boolean understory;
    public boolean coniferous;
    public boolean deciduous;
    private double height;
    public boolean fireSource;
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
    double w = 0.5;
    double p = 0.5;
    double distance = 1.0;
    public List<Boolean> onFire;
    private double fireGrowthRate = 0.15;

    public Point(int x, int y) {
        this.neighbors = new ArrayList<Point>();
        elevation = Math.max(x + y - 50, 0);
        initializeEmpty();
    }

    public void initializeEmpty() {
        fireSource = false;

        litter = false;
        floor = false;
        understory = false;
        coniferous = false;
        deciduous = false;
        //elevation = Math.max(x+y-50,0);

        currentState = 0;
        state = new ArrayList<>();
        humidity = 0.4;
        temperature = new ArrayList<>();
        nextState = new ArrayList<>();
        nextTemperature = new ArrayList<>();
        onFire = new ArrayList<>();

        for (int i = 0; i < LEVELS; i++) {
            state.add(1.0);
            temperature.add(30.0);
            nextState.add(1.0);
            nextTemperature.add(30.0);
            onFire.add(Boolean.FALSE);
        }
    }

    public void addFireSource() {
        temperature.set(0, 600.0);
        fireSource = true;
        onFire.set(0, Boolean.TRUE);
    }

    public void initializeLitter() {
        initializeEmpty();
        litter = true;
        currentState = 1;

        height = RND.nextGaussian(mediumLitterHeight, Math.sqrt(mediumLitterHeightVariance));
    }

    public void initializeFloor() {
        initializeEmpty();
        floor = true;
        currentState = 2;

        height = RND.nextGaussian(mediumFloorHeight, Math.sqrt(mediumFloorHeightVariance));
    }

    public void initializeUnderstory() {
        initializeEmpty();
        understory = true;
        currentState = 3;

        height = RND.nextGaussian(mediumUnderstoryHeight, Math.sqrt(mediumUnderstoryHeightVariance));
    }

    public void initializeConiferous() {
        initializeEmpty();
        coniferous = true;
        currentState = 4;

        height = RND.nextGaussian(mediumConiferousHeight, Math.sqrt(mediumConiferousHeightVariance));
    }

    public void initializeDeciduous() {
        initializeEmpty();
        deciduous = true;
        currentState = 5;

        height = RND.nextGaussian(mediumDeciduousHeight, Math.sqrt(mediumDeciduousHeightVariance));
    }

    public int getState() {
        return currentState;
    }

    public void update() {
        double actualBurnTemp = actualBurningTemperature();

        for (int i = 0; i < LEVELS; i++) {
			/*if (state.get(i) == 0.0){
				for(int j = 0;j <i ;j ++){
					state.set(j, 0.0);
				}
			}*/
            state.set(i, nextState.get(i));
            temperature.set(i, nextTemperature.get(i));
            if (temperature.get(i) >= actualBurnTemp) {
                onFire.set(i, Boolean.TRUE);
            } else {
                onFire.set(i, Boolean.FALSE);
            }
        }
    }

    private double actualBurningTemperature() {

        double k = -0.1;
        return burningTemperature * Math.exp(k * humidity / StandardHumidity);
    }

    private void burn() {
        double actualBurnTemp = actualBurningTemperature();

        for (int i = 0; i < LEVELS; i++) {
            nextState.set(i, state.get(i));
            if (onFire.get(i)) {
                nextState.set(i, state.get(i) * (1 - 0.01 * temperature.get(i) / actualBurnTemp));
                nextTemperature.set(i, temperature.get(i) * (1 + fireGrowthRate) * nextState.get(i));
            }
        }
    }

    public void calculateNewState(double windVelocity) {
        if (currentState == 0) {
            return;
        }

        burn();


        for (int j = 0; j < neighbors.size(); j++) {
            Point neighbor = neighbors.get(j);

            if (neighbor.temperature.get(0) >= actualBurningTemperature()) {
                double elevationDifference = Math.sqrt(Math.pow((elevation - neighbor.elevation), 2) + Math.pow(distance, 2));
                double necessaryProb = 0.06 / (1 + elevationDifference * 1);
                if (j >= 4) necessaryProb /= Math.sqrt(2);
                if (RND.nextDouble() < necessaryProb) {
                    nextTemperature.set(0, neighbor.temperature.get(0));
                }
            }
        }


        // Spreading fire up and down
        for (int i = 0; i < LEVELS - 1; i++) {
            if (onFire.get(i) && !onFire.get(i + 1)) {
                if (RND.nextDouble() < 0.2) {
                    nextTemperature.set(i + 1, temperature.get(i));
                }
            }
        }

        for (int i = LEVELS - 1; i > 0; i--) {
            if (onFire.get(i) && !onFire.get(i - 1)) {
                if (RND.nextDouble() < 0.05) {
                    nextTemperature.set(i - 1, temperature.get(i));
                }
            }
        }

        double angle = calculateFireAngle(windVelocity, w);
        int direction = 4;
        if (neighbors.size() > direction) {
            double newElevation = elevation + height * Math.sin(Math.toRadians(angle));
            int i = (int) ((newElevation - neighbors.get(direction).elevation) * LEVELS / neighbors.get(direction).height);
            if (i >= 0 && i < LEVELS) {
                double actualDistance = distance;
                if (direction > 3) {
                    actualDistance *= Math.sqrt(2);
                }
                if (height * Math.cos(Math.toRadians(angle)) > actualDistance / 2) {
                    if (RND.nextDouble() < p) {
                        neighbors.get(direction).nextTemperature.set(i, temperature.get(i));
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

    public PointStatistics toPointStatistics(int x, int y) {
        return new PointStatistics(
                x,
                y,
                this.elevation,
                this.litter,
                this.floor,
                this.understory,
                this.coniferous,
                this.deciduous,
                this.height,
                this.fireSource,
                List.copyOf(this.state),
                List.copyOf(this.temperature),
                this.humidity,
                this.currentState,
                List.copyOf(this.onFire)
        );
    }
}