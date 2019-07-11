import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Captain {
	
	int captainId;
	Problem p;
	Vertex curVertex = null;
	Edge curEdge = null;
	double curLocationOnEdge = 0;
	double captainSpeed = 1;
	
	ArrayList<Worker> squad = new ArrayList<Worker>();
	ArrayList<Integer> path = new ArrayList<Integer>();
	
	boolean isMoving = false;
	boolean isWaiting = false;
	double waitTimeRemaining = 0;
	int waitCount = 0;
	Treasure waitTreasure = null;
	
	Captain(Problem p, Vertex vv) {
		this.p = p;
		curVertex = vv;
	}
	
	void update() {
		if (isMoving) {
			curLocationOnEdge += captainSpeed / p.numFramesPerSecond / curEdge.distance;
			
			// reached the next vertex
			if (curLocationOnEdge > 1.00001) {
				curVertex = curEdge.otherVertex(curVertex);
				
				// reached the next vertex on the path
				path.remove(path.size()-1);
				path.remove(path.size()-1);
				curLocationOnEdge = 0;
				if (path.size() < 2) {
					// path is completed
					path.clear();
					isMoving = false;
					if (p.printInfo)
						System.out.println("-Captain " + captainId + " has reached vertex " + curVertex.id);
				} else {
					curEdge = p.E.get(path.get(path.size() - 2));
					return; // still moving
				}
			} 
			else return; // still moving
		}
		if (isWaiting && waitTimeRemaining > 0) {
			waitTimeRemaining -= 1.0 / p.numFramesPerSecond;
			if (waitTimeRemaining < 0.00001) {
				waitTimeRemaining = 0;
				isWaiting = false;
				if (p.printInfo)
					System.out.println("-Captain " + captainId + " has finished waiting");
			}
			return; // still waiting
		}
		if (isWaiting && waitCount > 0) {
			if (squad.size() + curVertex.workersHere.size() >= waitCount) {
				waitCount = 0;
				isWaiting = false;
				if (p.printInfo)
					System.out.println("-Captain " + captainId + " has finished waiting");
			} 
			else return; // still waiting
		}
		if (isWaiting && waitTreasure != null) {
			if (curVertex.treasuresHere.contains(waitTreasure) || waitTreasure.isCollected) {
				waitTreasure = null;
				isWaiting = false;
				if (p.printInfo)
					System.out.println("-Captain " + captainId + " has finished waiting");
			} 
			else return;
		}
		
		readRouteInstruction();
	}
	
	void readRouteInstruction() {
		
		if (p.route.i >= p.route.R.size()) {
			p.route.computeNextRoutingDecision(p);
			if (p.route.i >= p.route.R.size()) {
				return;
			}
		}
		
		String sss = p.route.R.get(p.route.i).trim();
		if (sss.charAt(0) == ';') {
			readRouteInstruction(); // this line was a comment
			return;
		}
		if (sss.indexOf(";") > -1) sss = sss.substring(0, sss.indexOf(";"));
		String[] sc = sss.split(" ");
		p.route.R.set(p.route.i, sss + " ; " + String.format("time=%.2f", p.curTime));
		
		p.route.i++;
		System.out.println("-Instruction: " + Arrays.toString(sc));
		
		if (sc[0].charAt(0) == 'g') { // go-here
			Vertex v = p.V.get(Integer.parseInt(sc[1]));
			path = p.shortestPath(curVertex, v);
			
			curLocationOnEdge = 0;
			if (path.size() < 2)  {
				// already here
			} else {
				isMoving = true;
				curEdge = p.E.get(path.get(path.size() - 2));
				if (p.printInfo)
					System.out.println("-Captain " + captainId + " is going-here to vertex " + v.id);
			}
		}
		if ((sc[0].charAt(0) == 'w' && sc[1].equals("time")) || sc[0].charAt(0) == 'm' 
				|| (sc[0].charAt(0) == 'w' && sc[1].charAt(0) == 'm')) { // wait time
			if (sc[1].charAt(0) > '9') 
				waitTimeRemaining = Double.parseDouble(sc[2]);
			else waitTimeRemaining = Double.parseDouble(sc[1]);
			isWaiting = true;
			if (p.printInfo)
				System.out.println("-Captain " + captainId + " is waiting for time " + waitTimeRemaining);
		}
		else if ((sc[0].charAt(0) == 'w' && sc[1].charAt(0) == 't')  
				|| sc[0].charAt(0) == 't') { // wait for treasure
			String wts = "";
			if (sc[1].charAt(0) > '9') 
				wts = sc[2].trim();
			else wts = sc[1].trim();
			for (Treasure t: p.T) {
				if (t.name.equals(wts)) {
					waitTreasure = t;
				}
			}
			isWaiting = true;
			if (p.printInfo)
				System.out.println("-Captain " + captainId + " is waiting for treasure " + waitTreasure.name);
		}
		else if (sc[0].charAt(0) == 'w') { // wait for count
			if (sc[1].charAt(0) > '9') 
				waitCount = Integer.parseInt(sc[2]);
			else waitCount = Integer.parseInt(sc[1]);
			isWaiting = true;
			if (p.printInfo)
				System.out.println("-Captain " + captainId + " is waiting for count " + waitCount);
		}
		if (sc[0].charAt(0) == 'c') { // call
			if (sc[1].charAt(0) == 'a') {
				// call all workers at this vertex
				for (Worker w: curVertex.workersHere) {
					squad.add(w);
					w.curVertex = null;
					w.curCaptain = this;
					w.curLocationOnEdge = 0;
				}
				curVertex.workersHere.clear();
				System.out.println("-Captain " + captainId + " called all at vertex " + curVertex.id + ", squad is now " + squad.size());
			}
		}
		if (sc[0].charAt(0) == 'd') { // direct workers onto a treasure
			Treasure t = p.T.get(Integer.parseInt(sc[sc.length-1]));
			ArrayList<Worker> workersToAdd = new ArrayList<Worker>();
			
			if (sc[1].charAt(0) == 'w') { // send fixed number of workers from squad
				int num = Integer.parseInt(sc[2]);
				if (squad.size() < num) {
					System.out.println("ERROR not enough workers");
				}
				for (int i = 0; i < num && i < squad.size(); i++) {
					workersToAdd.add(squad.get(i));
				}
				
			}
			
			if (p.printInfo) {
				System.out.print("-Captain " + captainId + " sends " + workersToAdd.size() 
				+ " workers to treasure " + t.treasureId 
				+ " {workers" );
				for (Worker w: workersToAdd) System.out.print(" " + w.workerId); 
				System.out.println("}");
			}
			
			for (int i = 0; i < workersToAdd.size(); i++) {
				squad.remove(workersToAdd.get(i));
			}
			t.addCarriers(workersToAdd);
			
		}
		
		update(); // do another thing, start moving, etc
		
	}
	
	// what does a route look like? 
	// g) go here to this vertex (taking the shortest path)
	// w) wait at a vertex until some number of workers arrive
	// m) wait at a vertex until some amount of time has passed
	// t) wait at a vertex until some treasure reaches that vertex or is collected
	// k) complete a task using the captain
	// c) call workers back into the squad
	// d) direct workers on a treasure
	

}
