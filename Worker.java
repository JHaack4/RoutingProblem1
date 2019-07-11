import java.util.ArrayList;

public class Worker {
	
	int workerId;
	Problem p;
	Captain curCaptain = null;
	Treasure curTreasure = null;
	Vertex curVertex = null;
	Edge curEdge = null;
	double curLocationOnEdge = 0;
	
	// for now, workers in a squad move with the captain
	
	int type;
	double maturity;
	
	ArrayList<Integer> path = new ArrayList<Integer>();
	
	Worker(Problem p) {
		this.p = p;
		this.type = 0;
		this.maturity = 2;
	}

	void update() {
		// do nothing
	}
	
}
