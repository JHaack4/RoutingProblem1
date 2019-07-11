import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Problem {

	ArrayList<Vertex> V = new ArrayList<Vertex>();
	HashMap<Integer, Vertex> idV = new HashMap<Integer, Vertex>();
	ArrayList<Edge> E = new ArrayList<Edge>();
	ArrayList<Captain> C = new ArrayList<Captain>();
	ArrayList<Worker> W = new ArrayList<Worker>();
	ArrayList<Treasure> T = new ArrayList<Treasure>();
	
	Route route;
	
	final int numFramesPerSecond = 1;
	int numFrames = 0;
	double curTime = 0;
	int treasuresCollected = 0;
	
	boolean printInfo;
	boolean printInfoNoisy = false;
	boolean draw;
	Draw drawer;
	int millisWaitBetweenDraw = 200;
	
	Problem(String filename, Route route, boolean draw, boolean printInfo) {
		this.draw = draw;
		this.printInfo = printInfo;
		this.route = route;
		if (draw) {
			drawer = new Draw(this);
			drawer.loadPanel();
		}
		try {
			BufferedReader f = new BufferedReader(new FileReader(filename));
			
			while(true) {
				String s = f.readLine();
				if (s == null) break;
				s = s.trim();
				if (s.charAt(0) == ';') continue; // comment
				if (s.indexOf(";") > -1) s = s.substring(0, s.indexOf(";"));
				
				String[] sc = s.split(" ");
				
				if (sc[0].charAt(0) == 'v') {
					int id = Integer.parseInt(sc[1]);
					Vertex v = new Vertex(this, id);
					for (int i = 2; i < sc.length; i++) {
						if (sc[i].equals("x")) {
							++i;
							v.x = Double.parseDouble(sc[i]);
						}
						else if (sc[i].equals("y")) {
							++i;
							v.y = Double.parseDouble(sc[i]);
						}
					}
					V.add(v);
					idV.put(id, v);
				}
				if (sc[0].charAt(0) == 'e') {
					int v = Integer.parseInt(sc[1]);
					int w = Integer.parseInt(sc[2]);
					Vertex vv = idV.get(v);
					Vertex ww = idV.get(w);
					double distance = -1;
					for (int i = 3; i < sc.length; i++) {
						if (sc[i].equals("d")) {
							++i;
							distance = Double.parseDouble(sc[i]);
						}
					}
					if (distance == -1) { // distance not provided by file
						distance = Math.sqrt(vv.x*ww.x + vv.y*ww.y);
					}
					Edge e = new Edge(this, vv, ww, distance);
					e.edgeId = E.size();
					E.add(e);
					vv.adj.add(e);
					ww.adj.add(e);
				}
				if (sc[0].charAt(0) == 'k' || sc[0].equals("task")) {
					
				}
				if (sc[0].charAt(0) == 't') {
					int v = Integer.parseInt(sc[1]);
					Vertex vv = idV.get(v);
					int weight = 1;
					int maxCarriers = -1;
					double distance = -1;
					String name = "" + T.size();
					for (int i = 2; i < sc.length; i++) {
						if (sc[i].charAt(0) == 'w') {
							++i;
							weight = Integer.parseInt(sc[i]);
						}
						if (sc[i].charAt(0) == 'm') {
							++i;
							maxCarriers = Integer.parseInt(sc[i]);
						}
						if (sc[i].charAt(0) == 'n') {
							++i;
							name = sc[i];
						}
					}
					if (maxCarriers == -1) { // distance not provided by file
						maxCarriers = weight * 2;
					}
					Treasure t = new Treasure(this, vv, weight, maxCarriers);
					t.treasureId = T.size();
					t.name = name;
					T.add(t);
					vv.treasuresHere.add(t);
				}
				if (sc[0].charAt(0) == 'c') {
					int v = Integer.parseInt(sc[1]);
					Vertex vv = idV.get(v);
					/*
					for (int i = 2; i < sc.length; i++) {
						if (sc[i].equals("w")) {
							++i;
							weight = Integer.parseInt(sc[i]);
						}
						if (sc[i].equals("m")) {
							++i;
							maxCarriers = Integer.parseInt(sc[i]);
						}
					}*/
					Captain c = new Captain(this, vv);
					vv.captainsHere.add(c);
					c.captainId = C.size();
					C.add(c);
				}
				
				if (sc[0].charAt(0) == 'w') {
					int num = Integer.parseInt(sc[1]);
					/*
					for (int i = 2; i < sc.length; i++) {
						if (sc[i].equals("w")) {
							++i;
							weight = Integer.parseInt(sc[i]);
						}
						if (sc[i].equals("m")) {
							++i;
							maxCarriers = Integer.parseInt(sc[i]);
						}
					}*/
					for (int i = 0; i < num; i++) {
						Worker w = new Worker(this);
						w.workerId = W.size();
						W.add(w);
						//Captain c = C.get(0);
						//Vertex v = c.curVertex;
						//v.workersHere.add(w);
						//c.squad.add(w);
						//w.curVertex = v;
						//w.curCaptain = c;
						w.curVertex = V.get(0);
						V.get(0).workersHere.add(w);
					}
				}
				
			}
			
			f.close();
			
			
			//System.out.println(shortestPath(V.get(0), V.get(1)).stream().map(Object::toString).collect(Collectors.joining(",")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	double run(double maxTimeAllowed) {
		
		while(true) {

			if (printInfo) {
				System.out.println("frames=" + numFrames + " time=" + curTime
							+ " treasures_collected=" + treasuresCollected + "/" + T.size());
			}
			
			for (Worker w: W) {
				w.update();
			}
			for (Treasure t: T) {
				t.update();
			}
			//route.computeNextRoutingDecision(this);
			for (Captain c: C) {
				c.update();
			}
			
			if (treasuresCollected == T.size()) {
				if (printInfo) {
					System.out.println("Problem completed, time=" + curTime);
				}
				route.R.add(String.format("; Complete! time=%.2f", curTime));
				return curTime;
			}
			if (curTime > maxTimeAllowed) {
				if (printInfo) {
					System.out.println("Time limit exceeded");
				}
				return maxTimeAllowed;
			}
			
			if (draw) {
				drawer.updateGraphics();
				try {
					Thread.sleep(millisWaitBetweenDraw);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			numFrames += 1;
			curTime = numFrames * 1.0 / numFramesPerSecond;
		}
	}
	
	
	
	
	
	// paths are represented as lists of ints
	// vn, ... e2, v2, e1, v1
	ArrayList<Integer> shortestPath(Vertex startV, Vertex endV) {
		// djikstra's algorithm
		
		PriorityQueue<Vertex> q = new PriorityQueue<Vertex>(new VertexComparator());
		
		for (int i = 0; i < V.size(); i++) {
			V.get(i).distanceFromStartVertex = (i==startV.id ? 0 : INF);
			V.get(i).prevEdge = null;
			q.add(V.get(i));
		}
		
		while(q.size() > 0) {
			Vertex r = q.poll();
			
			if (r.id == endV.id) break;
			
			for (Edge e: r.adj) {
				if (!e.traversableStartingHere(r)) continue;
				Vertex o = e.otherVertex(r);
				double alt = r.distanceFromStartVertex + e.distance;
				if (alt < o.distanceFromStartVertex) {
					o.prevEdge = e;
					o.distanceFromStartVertex = alt;
				}
			}
		}
		
		// reconstruct
		ArrayList<Integer> path = new ArrayList<Integer>();
		Vertex r = endV;
		while(true) {
			path.add(r.id);
			if (r.id == startV.id) break;
			path.add(r.prevEdge.edgeId);
			r = r.prevEdge.otherVertex(r);
		}
		return path;
		
	}
	
	final double INF = Double.MAX_VALUE;
	
	class VertexComparator implements Comparator<Vertex>{ 
        public int compare(Vertex s1, Vertex s2) { 
            if (s1.distanceFromStartVertex < s2.distanceFromStartVertex) 
                return -1; 
            else if (s1.distanceFromStartVertex > s2.distanceFromStartVertex) 
                return 1; 
            return 0; 
        } 
    }
}
