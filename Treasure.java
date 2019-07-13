import java.util.ArrayList;
import java.util.stream.Collectors;

public class Treasure {
	
	int id;
	Problem p;
	String name;
	Vertex curVertex = null;
	Edge curEdge = null;
	double curLocationOnEdge = 0;
	boolean isCollected = false;
	boolean isMoving = false;
	
	int weight;
	int maxCarriers;
	double speed;
	
	ArrayList<Worker> carriers = new ArrayList<Worker>();
	ArrayList<Integer> path = new ArrayList<Integer>();
	
	Treasure(Problem p, int id, Vertex initV, int weight, int maxCarriers) {
		this.p = p;
		this.id = id;
		curVertex = initV;
		this.weight = weight;
		this.maxCarriers = maxCarriers;
		initV.treasuresHere.add(this);
	}
	
	void updatePath() {
		path = Util.shortestPath(p,curVertex, p.V.get(0));
		Draw.print("-Treasure " + id + " taking {path " + path.stream().map(Object::toString).collect(Collectors.joining("<")) + "}\n");
		
		curLocationOnEdge = 0;
		if (path.size() < 2) return;
		curEdge = p.E.get(path.get(path.size() - 2));
	}
	
	void addCarriers(ArrayList<Worker> cc) {
		for (Worker w: cc) {
			if (carriers.size() < maxCarriers) {
				carriers.add(w);
			}
			else {
				System.out.println("Error, too many carriers");
			}
		}
		
		Draw.println("-Treasure " + id + " adds " + cc.size() + " carriers");
		
		
		if (carriers.size() >= weight) {
			isMoving = true;
			speed = 0.5;
			updatePath();
		}
		else {
			isMoving = false;
			speed = 0;
		}
	}
	
	void removeCarriers(ArrayList<Worker> cc) {
		// todo, for now, carriers can't be removed
	}
	
	void update() {
		if (isCollected || !isMoving) return;
		
		curLocationOnEdge += speed / p.numFramesPerSecond / curEdge.distance;
		
		// reached the next vertex
		if (curLocationOnEdge > 1 - 1.0e-12) {
			curVertex.treasuresHere.remove(this);
			curVertex = curEdge.otherVertex(curVertex);
			
			if (curVertex.id == 0) {
				Draw.println("-Treasure " + name + " has been collected");
				
				isCollected = true;
				p.treasuresCollected ++;
				curLocationOnEdge = 0;
				curEdge = null;
				for (Worker w: carriers) {
					w.curTreasure = null;
					w.curVertex = curVertex;
					w.curLocationOnEdge = 0;
					curVertex.workersHere.add(w);
				}
				carriers.clear();
				isMoving = false;
				return;
			}
			
			// reached the next vertex on the path
			curVertex.treasuresHere.add(this);
			path.remove(path.size()-1);
			path.remove(path.size()-1);
			curLocationOnEdge = 0;
			if (path.size() < 2) return;
			curEdge = p.E.get(path.get(path.size() - 2));
		}
		
	}

}
