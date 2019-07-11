
public class Util {

	static int randomWithWeights(double[] weights) {
		double sum = 0;
		for (double w: weights) sum += w;
		
		double decision = Math.random()*sum;
		for (int i = 0; i < weights.length; i++) {
			if (decision >= sum) return i;
			sum -= weights[i];
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
				s += " " + e.edgeId;
			}
			s += "} {treasures";
			for (Treasure t: v.treasuresHere) {
				s += " " + t.treasureId;
			}
			s += "} {captains";
			for (Captain c: v.captainsHere) {
				s += " " + c.captainId;
			}
			s += "} {workers";
			for (Worker w: v.workersHere) {
				s += " " + w.workerId;
			}
			s += "}\n";
		}
		for (Edge e: p.E) {
			s += "e" + e.edgeId + ": " + "v=" + e.v.id + " w=" + e.w.id 
					+ " ->" + e.traversableFromVtoW + " <-" + e.traversableFromVtoW;
			
			s += " {treasures";
			for (Treasure t: e.treasuresHere) {
				s += " " + t.treasureId;
			}
			s += "} {captains";
			for (Captain c: e.captainsHere) {
				s += " " + c.captainId;
			}
			s += "} {workers";
			for (Worker w: e.workersHere) {
				s += " " + w.workerId;
			}
			s += "}\n";
		}
		for (Treasure t: p.T) {
			s += "t" + t.treasureId + ": " + t.carriers.size() + "/" + t.weight + "/" + t.maxCarriers + " v=" 
						+ t.curVertex.id + " e=" + (t.curEdge==null ? "_": t.curEdge.edgeId) + "@" + t.curLocationOnEdge;

			s += " {carriers";
			for (Worker w: t.carriers) {
				s += " " + w.workerId;
			}
			s += "} name=" + t.name + "\n";
		}
		for (Captain c: p.C) {
			s += "c" + c.captainId + ": sqaud=" + c.squad.size()  + " v=" 
						+ c.curVertex.id + " e=" + (c.curEdge==null ? "_": c.curEdge.edgeId) + "@" + c.curLocationOnEdge;

			s += " {squad";
			for (Worker w: c.squad) {
				s += " " + w.workerId;
			}
			s += "}\n";
		}
		for (Worker w: p.W) {
			s += "w" + w.workerId + ": type=" + w.type  + " c=" + (w.curCaptain==null ? "_":w.curCaptain.captainId) 
					+ " t=" + (w.curTreasure==null ? "_":w.curTreasure.treasureId)
					+ " v=" + (w.curVertex==null ? "_":w.curVertex.id) + " e=" + (w.curEdge==null ? "_": w.curEdge.edgeId) + "@" + w.curLocationOnEdge;
			s += "}\n";
		}
		return s;
	}
	
	
}
