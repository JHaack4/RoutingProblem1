import java.util.ArrayList;

public class Vertex {

	Problem p;
	int id = -1;
	double x = -1;
	double y = -1;
	boolean isOnion = false;
	
	ArrayList<Edge> adj;
	ArrayList<Captain> captainsHere = new ArrayList<Captain>();
	ArrayList<Worker> workersHere = new ArrayList<Worker>();
	ArrayList<Treasure> treasuresHere = new ArrayList<Treasure>();
	
	// helpers for shortest path
	double distanceFromStartVertex;
	Edge prevEdge;
	boolean visited;
	
	Vertex(Problem p, int id) {
		this.p = p;
		this.id = id;
		adj = new ArrayList<Edge>();
		isOnion = id == 0;
	}
	
}
