import java.util.ArrayList;

public class Edge {
	
	int edgeId;
	Problem p;
	Vertex v = null;
	Vertex w = null;
	boolean traversableFromVtoW = true;
	boolean traversableFromWtoV = true;
	double distance = -1;
	
	ArrayList<Captain> captainsHere = new ArrayList<Captain>();
	ArrayList<Worker> workersHere = new ArrayList<Worker>();
	ArrayList<Treasure> treasuresHere = new ArrayList<Treasure>();
	
	Edge(Problem p, Vertex v, Vertex w, double d) {
		this.p = p;
		this.v = v;
		this.w = w;
		distance = d;
	}
	
	
	boolean traversableStartingHere(Vertex r) {
		if (r.id == v.id) return traversableFromVtoW;
		if (r.id == w.id) return traversableFromWtoV;
		return false;
	}
	
	Vertex otherVertex(Vertex r) {
		if (r.id == v.id) return w;
		if (r.id == w.id) return v;
		return null;
	}

}
