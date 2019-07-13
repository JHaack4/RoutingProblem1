import java.util.ArrayList;
import java.util.Arrays;

public class RouterRandomLegal extends Route {

	double distWeightFunc(Problem p, Captain c, Treasure t) {
		double dist = Util.shortestDistance(p, c.curVertex, t.curVertex);
		if (t.isCollected || t.isMoving) return 0;
		return 1 / (1 + dist * dist / Util.averageDistance / Util.averageDistance * 100);
	}
	
	double distWeightFunc2(Problem p, Treasure t0, Treasure t) {
		double dist = Util.shortestDistance(p, t0.curVertex, t.curVertex) / Util.averageDistance;
		if (t.isCollected || t.isMoving) return 0;
		return 0.1 / (1 + dist * 100);
	}
	
	@Override
	public void computeNextRoutingDecision(Problem p) {
		
		if (p.route.i < p.route.R.size()) return; // already have instructions
		Draw.println("! Random Legal trying to compute next move");

		Captain c = p.C.get(0);
		int squadSize = c.squad.size();
		//double proportionOfWorkersInSquad = squadSize * 1.0 / p.W.size();
		
		if (p.numFrames == 0 && squadSize == 0) {
			p.route.R.add("call all");
			return;
		}
		
		int numWorkersToCollectGroup = 0;
		ArrayList<Treasure> treasureGroup = new ArrayList<Treasure>();
		double[] treasureWeights = new double[p.T.size()];
		
		// determine an initial treasure to collect
		for (int i = 0; i < p.T.size(); i++) {
			Treasure t = p.T.get(i);
			treasureWeights[i] = t.isCollected || t.isMoving ? 0 : t.weight;
		}
		
		Treasure tInit = p.T.get(Util.randomWithWeights(treasureWeights));
		treasureGroup.add(tInit);
		numWorkersToCollectGroup += tInit.weight;
		int maxGroupWeight = (int)Math.min(p.W.size(), (Math.random() < 0.2 ? 0 : 10000) + tInit.weight + p.W.size()*Math.random());
		
		if (tInit.isCollected || tInit.isMoving) {
			// all treasures already collected
			p.route.R.add("wait-time 10000");
			return;
		}
		
		// give treasures at the same vertex chance to join treasure group
		ArrayList<Integer> perm = Util.getRandomPermutation(tInit.curVertex.treasuresHere.size());
		for (int i = 0; i < tInit.curVertex.treasuresHere.size(); i++) {
			Treasure t = tInit.curVertex.treasuresHere.get(perm.get(i));
			if (t.isCollected || t.isMoving || treasureGroup.contains(t)) continue;
			if (Math.random() < 0.99 && t.weight + numWorkersToCollectGroup <= maxGroupWeight) {
				treasureGroup.add(t);
				numWorkersToCollectGroup += t.weight;
			}
		}
		
		// give all other treasures a chance to join the treasure group (much lower chance)
		perm = Util.getRandomPermutation(p.T.size());
		for (int i = 0; i < p.T.size() && numWorkersToCollectGroup <= maxGroupWeight; i++) {
			Treasure t = p.T.get(perm.get(i));
			if (t.isCollected || t.isMoving || treasureGroup.contains(t)) continue;
			if (Math.random() < distWeightFunc2(p, tInit, t) && t.weight + numWorkersToCollectGroup <= maxGroupWeight) {
				treasureGroup.add(t);
				numWorkersToCollectGroup += t.weight;
			}
		}
		
		Draw.println("! going for a treasure group, weight=" + numWorkersToCollectGroup);
		for (Treasure t: treasureGroup) {
			Draw.print(t.name + " ");
		}
		Draw.println("");
		
		// check if there are enough workers, if not, go to onion and wait
		// todo: check other vertices for workers, other waiting styles
		if (squadSize < numWorkersToCollectGroup) {
			p.route.R.add("go-here-to-vertex 0");
			p.route.R.add("wait-workers " + numWorkersToCollectGroup);
			p.route.R.add("call all");
		}
		
		// todo: sort treasures into better order
		
		// direct to each treasure
		for (int i = 0; i < treasureGroup.size(); i++) {
			Treasure t = treasureGroup.get(i);
			if (i == 0 || treasureGroup.get(i-1).curVertex.id != t.curVertex.id)
				p.route.R.add("go-here-to-vertex " + t.curVertex.id);
			p.route.R.add("direct workers " + t.weight + " to treasure " + t.name);
		}
		
		double nextAction = Math.random();
		if (nextAction < 0.001) {
			// go to onion, wait for a random treasure in the group
			// man it really hates this instruction...
			p.route.R.add("go-here-to-vertex 0");
			Treasure t = treasureGroup.get((int)(treasureGroup.size()*Math.random()));
			p.route.R.add("wait-treasure " + t.name);
		}
		else if (nextAction < 0.7) {
			// go to vertex with idle workers??
		}
		
		

			// get more workers at a vertex with idle workers
			/*for (int i = 0; i < 100; i++) {
				int r = i==1 ? 0 : (int)(Math.random() * p.V.size()); // make sure onion is tried
				Vertex tv = p.V.get(r);
				if (tv.workersHere.size() == 0) continue;
				Draw.println("! going to a vertex for more workers: " + tv.id);
				p.route.R.add("go-here-to-vertex " + tv.id);
				p.route.R.add("call all");
				return;
			}*/
			

		
	}

}


/*
import java.util.Arrays;

public class RouterRandomLegal extends Route {

	double distWeightFunc(Problem p, Captain c, Treasure t) {
		double dist = Util.shortestDistance(p, c.curVertex, t.curVertex);
		if (t.isCollected || t.isMoving) return 0;
		return 1 / (1 + dist * dist / Util.averageDistance / Util.averageDistance * 100);
	}
	
	@Override
	public void computeNextRoutingDecision(Problem p) {
		
		if (p.route.i < p.route.R.size()) return; // already have instructions
		Draw.println("! Random Legal trying to compute next move");

		Captain c = p.C.get(0);
		int squadSize = c.squad.size();
		double probOfGettingTreasure = squadSize * 1.0 / p.W.size();
		probOfGettingTreasure = Math.pow(probOfGettingTreasure, 0.25);
		double distToOnion = Util.shortestDistance(p, c.curVertex, p.V.get(0));
		if (distToOnion > Util.averageDistance) probOfGettingTreasure = 1-(1-probOfGettingTreasure)/2;
		Draw.println("! prob of getting treasure: " + probOfGettingTreasure);
		
		Treasure t = null;
		if (Math.random() < probOfGettingTreasure) {
			// get a random treasure
			
			// compute distances from captain to each vertex
			double[] treasureWeights = new double[p.T.size()];
			for (int i = 0; i < p.T.size(); i++) {
				treasureWeights[i] = distWeightFunc(p, c, p.T.get(i));
			}
			Draw.println("! Random Legal treasure weights " + Arrays.toString(treasureWeights));
			
			for (int i = 0; i < 100; i++) {
				Treasure tc = p.T.get(Util.randomWithWeights(treasureWeights));
				if (tc.isCollected || tc.isMoving || tc.weight > squadSize) continue;
				t = tc;
				break;
			}
			if (t!=null) {
				//System.out.println(Arrays.toString(treasureWeights));
				//System.out.println(t.treasureId);
				Draw.println("! going for a treasure: " + t.name);
				p.route.R.add("go-here-to-vertex " + t.curVertex.id);
				p.route.R.add("direct workers " + t.weight + " to treasure " + t.name);
				return;
			} else {
				Draw.println("! tried and failed to go for a treasure: ");
			}
		}
		if (t == null) {
			// get more workers at a vertex with idle workers
			for (int i = 0; i < 100; i++) {
				int r = i==1 ? 0 : (int)(Math.random() * p.V.size()); // make sure onion is tried
				Vertex tv = p.V.get(r);
				if (tv.workersHere.size() == 0) continue;
				Draw.println("! going to a vertex for more workers: " + tv.id);
				p.route.R.add("go-here-to-vertex " + tv.id);
				p.route.R.add("call all");
				return;
			}
			
			Draw.println("! waiting/going to onion for workers: ");
			// wait for workers to arrive
			if (c.curVertex.isOnion) {
				if (c.squad.size() < p.W.size()) {
					p.route.R.add("wait-workers " + (c.squad.size()+1));
					p.route.R.add("call all");
				}
				else p.route.R.add("wait-time 1");
				return;
			} 
			
			// go to onion
			p.route.R.add("go-here-to-vertex 0");
			if (c.squad.size() < p.W.size()) {
				p.route.R.add("wait-workers " + (c.squad.size()+1));
				p.route.R.add("call all");
			}
			return;
		}
		
	}

}

 */
