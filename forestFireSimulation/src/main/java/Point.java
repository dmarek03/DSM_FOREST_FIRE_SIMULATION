import java.util.ArrayList;
import java.util.List;

public class Point {
	// Nie byłem pewien czy wysokość będzie robić typu int czy double , na razie dałem int
	private int elevation;
	private boolean litter;
	private boolean floor;
	private boolean understory;
	private boolean coniferous;
	private boolean deciduous;
	private int height;
	private List<Double> state;
	private List<Double> temperature;
	private List<Point> neighbors;
	private int currentState;
	private int nextState;
	private int numStates = 6;
	
	public Point() {
		this.currentState = 0;
		this.nextState = 0;
		this.neighbors = new ArrayList<Point>();
		this.state = new ArrayList<>();
		this.temperature = new ArrayList<>();
		for(int i = 0 ; i < height;i ++){
			state.add(1.0);
		}
	}

	public void clicked() {
		currentState=(++currentState)%numStates;	
	}
	
	public int getState() {
		return currentState;
	}

	public void setState(int s) {
		currentState = s;
	}

	public void changeState() {
		currentState = nextState;
	}

	public void calculateNewState(){
	}
	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}


    @Override
	public String toString(){
		return "Elevation:"+this.elevation+"\n"
				+"Has litter:"+this.litter+"\n"
				+"Has floor:"+this.floor
				+"Has understory"+this.understory
				+"Is coniferous "+this.coniferous+"\n"
				+"Is deciduous "+this.deciduous+"\n"
				+"Height:"+this.height+"\n"
				+"States:"+this.state+"\n"
				+"Temperature:"+this.temperature+"\n";
	}
	


	public int countAliveNeighbour(){
		int cnt = 0;
		for(Point nei : this.neighbors){
			if(nei.getState()==1){
				cnt += 1;
			}
		}
		return cnt;

	}

}
