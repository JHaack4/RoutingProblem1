import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;


public class Util {
	
	static ArrayList<String> loadFile(String fileName) {
		ArrayList<String> r = new ArrayList<String>();
		BufferedReader f;
		try {
			f = new BufferedReader(new FileReader(fileName));
			while(true) {
				String s = f.readLine();
				if (s == null) break;
				r.add(s.trim());
			}
			f.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	static int randomWithWeights(double[] weights) {
		double sum = 0;
		for (double w: weights) sum += w;
		
		double decision = Math.random()*sum;
		for (int i = 0; i < weights.length; i++) {
			sum -= weights[i];
			if (decision >= sum) return i;
		}
		return 0;
	}
	
	static String state(Problem p) {
		String s = "STATE:\n";
		s += "time=" + p.curTime + " frames=" + p.numFrames + "\n";
		s += "treasures_collected=" + p.treasuresCollected + "/" + p.T.size() + "\n";
		for (Vertex v: p.V) {
			s += "v" + v.id + ": ";
			s += "{adj";
			for (Edge e: v.adj) {
				s += " " + e.id;
			}
			s += "} {treasures";
			for (Treasure t: v.treasuresHere) {
				s += " " + t.id;
			}
			s += "} {captains";
			for (Captain c: v.captainsHere) {
				s += " " + c.id;
			}
			s += "} {workers";
			for (Worker w: v.workersHere) {
				s += " " + w.id;
			}
			s += "}\n";
		}
		for (Edge e: p.E) {
			s += "e" + e.id + ": " + "v=" + e.v.id + " w=" + e.w.id  + " d=" + e.distance
					+ " ->" + e.traversableFromVtoW + " <-" + e.traversableFromVtoW;
			
			s += " {treasures";
			for (Treasure t: e.treasuresHere) {
				s += " " + t.id;
			}
			s += "} {captains";
			for (Captain c: e.captainsHere) {
				s += " " + c.id;
			}
			s += "} {workers";
			for (Worker w: e.workersHere) {
				s += " " + w.id;
			}
			s += "}\n";
		}
		for (Treasure t: p.T) {
			s += "t" + t.id + ": " + t.carriers.size() + "/" + t.weight + "/" + t.maxCarriers + " v=" 
						+ t.curVertex.id + " e=" + (t.curEdge==null ? "_": t.curEdge.id) + "@" + t.curLocationOnEdge;

			s += " {carriers";
			for (Worker w: t.carriers) {
				s += " " + w.id;
			}
			s += "} name=" + t.name + "\n";
		}
		for (Captain c: p.C) {
			s += "c" + c.id + ": sqaud=" + c.squad.size()  + " v=" 
						+ c.curVertex.id + " e=" + (c.curEdge==null ? "_": c.curEdge.id) + "@" + c.curLocationOnEdge;

			s += " {squad";
			for (Worker w: c.squad) {
				s += " " + w.id;
			}
			s += "}\n";
		}
		for (Worker w: p.W) {
			s += "w" + w.id + ": type=" + w.type  + " c=" + (w.curCaptain==null ? "_":w.curCaptain.id) 
					+ " t=" + (w.curTreasure==null ? "_":w.curTreasure.id)
					+ " v=" + (w.curVertex==null ? "_":w.curVertex.id) + " e=" + (w.curEdge==null ? "_": w.curEdge.id) + "@" + w.curLocationOnEdge;
			s += "}\n";
		}
		return s;
	}
	
	// precompute all shortest paths
	static HashMap<String, ArrayList<Integer>> memorizedPaths = new HashMap<String, ArrayList<Integer>>();
	static HashMap<String, Double> memorizedDistances = new HashMap<String, Double>();
	static double averageDistance = 1;
	
	static void computeAllShortestPaths(Problem p) {
		Draw.println("--Computing all shortest paths...");
		double sumDists = 0;
		int numCon = 0;
		for (Vertex st: p.V) {
			for (Vertex end: p.V) {
				ArrayList<Integer> path = shortestPathDijkstra(p,st,end);
				String key = st.id+"|"+end.id;
				memorizedPaths.put(key, path);
				if (path == null) memorizedDistances.put(key, INF);
				double dist = 0;
				for (int i = 1; i < path.size(); i += 2) {
					dist += p.E.get(path.get(i)).distance;
				}
				memorizedDistances.put(key, dist);
				sumDists += dist;
				numCon += 1;
			}
		}
		averageDistance = Math.max(0.001, sumDists) / Math.max(1, numCon); 
	}
	
	// look up paths in memorized table
	static ArrayList<Integer> shortestPath(Problem p, Vertex startV, Vertex endV) {
		ArrayList<Integer> fnd = memorizedPaths.get(startV.id + "|" + endV.id);
		
		if (fnd == null) return null;
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (Integer i: fnd) ret.add(i);
		return ret;
	}
	
	static double shortestDistance(Problem p, Vertex startV, Vertex endV) {
		return memorizedDistances.get(startV.id + "|" + endV.id);
	}
	
	// compute a path using Dijkstra
	// paths are represented as lists of ints
	// vn, ... e2, v2, e1, v1
	static ArrayList<Integer> shortestPathDijkstra(Problem p, Vertex startV, Vertex endV) {
		// Dijkstra's algorithm
		
		PriorityQueue<Vertex> q = new PriorityQueue<Vertex>(new VertexComparator());
		
		for (int i = 0; i < p.V.size(); i++) {
			p.V.get(i).distanceFromStartVertex = (i==startV.id ? 0 : INF);
			p.V.get(i).prevEdge = null;
			p.V.get(i).visited = false;
			if (i==startV.id) q.add(p.V.get(i));
		}
		
		while(q.size() > 0) {
			Vertex r = q.poll();
			
			if (r.visited) continue; // already visited
			r.visited = true;
			
			if (r.id == endV.id) break;
			
			for (Edge e: r.adj) {
				if (!e.traversableStartingHere(r)) continue;
				Vertex o = e.otherVertex(r);
				double alt = r.distanceFromStartVertex + e.distance;
				if (alt < o.distanceFromStartVertex) {
					o.prevEdge = e;
					o.distanceFromStartVertex = alt;
					q.add(o);
				}
			}
		}
		
		if (endV.distanceFromStartVertex == INF) return null;
		
		// reconstruct
		ArrayList<Integer> path = new ArrayList<Integer>();
		Vertex r = endV;
		while(true) {
			path.add(r.id);
			if (r.id == startV.id) break;
			path.add(r.prevEdge.id);
			r = r.prevEdge.otherVertex(r);
		}
		return path;
		
	}
	
	final static double INF = Double.MAX_VALUE;
	
	static class VertexComparator implements Comparator<Vertex>{ 
        public int compare(Vertex s1, Vertex s2) { 
            if (s1.distanceFromStartVertex < s2.distanceFromStartVertex) 
                return -1; 
            else if (s1.distanceFromStartVertex > s2.distanceFromStartVertex) 
                return 1; 
            return 0; 
        } 
    }
	
	
	public static ArrayList<Integer> getRandomPermutation(int size) {
		
		ArrayList<Integer> unused = new ArrayList<>();
		ArrayList<Integer> perm = new ArrayList<>();
		
		for (int i = 0; i < size; i++) // loop for element in array
		{
			unused.add(i);
		}

		for (int k = 0; k < size; k++) // loop for random number between 1 to 10
		{
			int pos = (int) (Math.random() * unused.size());
			perm.add(unused.get(pos));
			unused.remove(pos);
		}

		return perm;
	}
	
	
}
