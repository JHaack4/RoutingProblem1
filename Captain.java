import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Captain {
	
	int id;
	Problem p;
	Vertex curVertex = null;
	Edge curEdge = null;
	double curLocationOnEdge = 0;
	double captainSpeed = 1;
	String name = "";
	
	ArrayList<Worker> squad = new ArrayList<Worker>();
	ArrayList<Integer> path = new ArrayList<Integer>();
	
	boolean isMoving = false;
	boolean isWaiting = false;
	boolean isLagging = false;
	double waitTimeRemaining = 0;
	int waitCount = 0;
	Treasure waitTreasure = null;
	
	Captain(Problem p, int id, Vertex initV) {
		this.p = p;
		this.id = id;
		curVertex = initV;
		initV.captainsHere.add(this);
	}
	
	void update() {
		updateMovingWaiting();
		
		int numInstructionsRead = 0;
		while (true) {
			if (isMoving || isWaiting || isLagging) {
				return; // not in a state to read an instruction 
			}
			
			// Compute the next route instruction
			if (p.route.i >= p.route.R.size()) {
				p.route.computeNextRoutingDecision(p);
			}
			
			if (p.route.i >= p.route.R.size()) {
				return; // no more route instructions.
			}
			
			String s = p.route.R.get(p.route.i).trim();
			if (s.indexOf(";") > -1) s = s.substring(0, s.indexOf(";")).trim();
			if (s.equals("")) {
				p.route.i++;
				continue; // no content in this instruction
			}
			
			// time stamp this line
			p.route.R.set(p.route.i, s + " ; " + String.format("time=%.2f", p.curTime));
			p.route.i++;
			
			updateRouteInstruction(s);
			
			numInstructionsRead++;
			if (numInstructionsRead > 10000) {
				System.out.println("Error, probably an infinite instruction loop");
				return;
			}
		}
	}
	
	void updateMovingWaiting() {
		if (isMoving) {
			curLocationOnEdge += captainSpeed / p.numFramesPerSecond / curEdge.distance;
			
			if (curLocationOnEdge > 1 - 1.0e-12) {
				// reached the next vertex on the path
				curVertex.captainsHere.remove(this);
				curVertex = curEdge.otherVertex(curVertex);
				
				path.remove(path.size()-1);
				path.remove(path.size()-1);
				curLocationOnEdge = 0;
				
				if (path.size() < 2) {
					// path is completed
					path.clear();
					isMoving = false;
					Draw.println("-Captain " + id + " has reached vertex " + curVertex.id);
				} else {
					curEdge = p.E.get(path.get(path.size() - 2));
					curVertex.captainsHere.add(this);
				}
			} 
		}
		else if (isWaiting && waitTimeRemaining > 0) {
			waitTimeRemaining -= 1.0 / p.numFramesPerSecond;
			if (waitTimeRemaining < 1.0e-12) {
				// waiting time has elapsed
				waitTimeRemaining = 0;
				isWaiting = false;
				Draw.println("-Captain " + id + " has finished waiting for time");
			}
		}
		else if (isWaiting && waitCount > 0) {
			if (squad.size() + curVertex.workersHere.size() >= waitCount) {
				// worker count reached
				Draw.println("-Captain " + id + " has finished waiting for workers " + waitCount);
				waitCount = 0;
				isWaiting = false;
			} 
		}
		else if (isWaiting && waitTreasure != null) {
			if (curVertex.treasuresHere.contains(waitTreasure) || waitTreasure.isCollected) {
				// treasure collected or arrived to vertex
				Draw.println("-Captain " + id + " has finished waiting for treasure " + waitTreasure.name);
				waitTreasure = null;
				isWaiting = false;
			} 
		}
	}
	
	void updateRouteInstruction(String s) {
		
		String[] sc = s.split(" ");
		Draw.println("-Route instruction: " + s);
		
		String instr = "";
		if (sc[0].charAt(0) == 'g') instr = "go-here";
		else if (sc[0].charAt(0) == 'c') instr = "call";
		else if (sc[0].charAt(0) == 'd') instr = "direct";
		else if (sc[0].charAt(0) == 'w' && sc[0].length() >= 7) {
			if (sc[0].substring(0, 7).equals("wait-ti")) instr = "wait-time";
			if (sc[0].substring(0, 7).equals("wait-tr")) instr = "wait-treasure";
			if (sc[0].substring(0, 7).equals("wait-wo")) instr = "wait-worker";
		}
		else if (sc[0].charAt(0) == 'w') instr = "wait-worker";
		else if (sc[0].charAt(0) == 'm') instr = "wait-time";
		else if (sc[0].charAt(0) == 't') instr = "wait-treasure";
		
		if (instr.equals("go-here")) {
			Vertex v = p.V.get(Integer.parseInt(sc[1]));
			path = Util.shortestPath(p, curVertex, v);
			if (path == null) {
				Draw.println("-Captain " + id + " failed go here from " + curVertex.id + " to " + v.id + ", no path.");
			}
			
			curLocationOnEdge = 0;
			if (path.size() < 2 || path == null)  {
				// already here
			} else {
				isMoving = true;
				curEdge = p.E.get(path.get(path.size() - 2));
				Draw.println("-Captain " + id + " is going-here to vertex " + v.id);
			}
		}
		else if (instr.equals("wait-time")) {
			waitTimeRemaining = Double.parseDouble(sc[1]);
			if (waitTimeRemaining < 1.0e-12) {
				// condition already satisfied
			}
			else {
				isWaiting = true;
				Draw.println("-Captain " + id + " is waiting for time " + waitTimeRemaining);
			}
		}
		else if (instr.equals("wait-treasure")) { // wait for treasure
			try { // first, assume we're given the treasure id
				int id = Integer.parseInt(sc[1]);
				waitTreasure = p.T.get(id);
			} catch (Exception e) { // otherwise, assume the treasure name
				for (Treasure t: p.T) {
					if (t.name.equals(sc[1])) {
						waitTreasure = t;
					}
				}
			}
			if (curVertex.treasuresHere.contains(waitTreasure) || waitTreasure.isCollected) {
				// condition already satisfied
			}
			else {
				isWaiting = true;
				Draw.println("-Captain " + id + " is waiting for treasure " + waitTreasure.name);
			}
		}
		else if (instr.equals("wait-worker")) { // wait for count
			waitCount = Integer.parseInt(sc[1]);
			if (squad.size() + curVertex.workersHere.size() >= waitCount) {
				// condition already satisfied
			} else {
				isWaiting = true;
				Draw.println("-Captain " + id + " is waiting for count " + waitCount);
			}
		}
		else if (instr.equals("call")) { // call
			if (sc[1].charAt(0) == 'a') { // all
				// call all workers at this vertex
				for (Worker w: curVertex.workersHere) {
					squad.add(w);
					w.curVertex = null;
					w.curCaptain = this;
					w.curLocationOnEdge = 0;
				}
				curVertex.workersHere.clear();
				Draw.println("-Captain " + id + " called all at vertex " + curVertex.id + ", squad is now " + squad.size());
			}
		}
		else if (instr.equals("direct")) { // direct workers onto a treasure
			Treasure t = p.T.get(Integer.parseInt(sc[sc.length-1]));
			if (t.curVertex.id != curVertex.id) {
				System.out.println("Error: trying to load a treasure from wrong vertex");
				System.exit(0);
				return;
			}
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
			
			if (Draw.draw) {
				Draw.print("-Captain " + id + " sends " + workersToAdd.size() 
				+ " workers to treasure " + t.id 
				+ " {workers" );
				for (Worker w: workersToAdd) Draw.print(" " + w.id); 
				Draw.println("}");
			}
			
			for (int i = 0; i < workersToAdd.size(); i++) {
				squad.remove(workersToAdd.get(i));
			}
			t.addCarriers(workersToAdd);
			
		}
		else {
			System.out.println("Error: cannot parse routing instruction: " + s);
		}
		
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
