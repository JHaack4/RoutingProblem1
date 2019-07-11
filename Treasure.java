import java.util.ArrayList;
import java.util.stream.Collectors;

public class Treasure {
	
	int treasureId;
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
	
	Treasure(Problem p, Vertex vv, int weight, int maxCarriers) {
		this.p = p;
		curVertex = vv;
		this.weight = weight;
		this.maxCarriers = maxCarriers;
	}
	
	void updatePath() {
		path = p.shortestPath(curVertex, p.V.get(0));
		if (p.printInfo) {
			System.out.print("-Treasure " + treasureId + " taking {path " + path.stream().map(Object::toString).collect(Collectors.joining("<")) + "}\n");
		}
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
				System.out.println("error, too many carriers");
			}
		}
		
		if (p.printInfo) {
			System.out.println("-Treasure " + treasureId + " adds " + cc.size() + " carriers");
		}
		
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
		if (curLocationOnEdge > 0.99999) {
			curVertex = curEdge.otherVertex(curVertex);
			
			if (curVertex.id == 0) {
				// reached onion
				if (p.printInfo) {
					System.out.println("-Treasure " + name + " has reached the onion");
				}
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
			path.remove(path.size()-1);
			path.remove(path.size()-1);
			curLocationOnEdge = 0;
			if (path.size() < 2) return;
			curEdge = p.E.get(path.get(path.size() - 2));
		}
		
	}

}
