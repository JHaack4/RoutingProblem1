
public class RouterRandomLegal extends Route {

	@Override
	public void computeNextRoutingDecision(Problem p) {
		
		if (p.route.i < p.route.R.size()) return; // already have instructions

		Captain c = p.C.get(0);
		int squadSize = c.squad.size();
		double probOfGettingTreasure = squadSize * 1.0 / p.W.size();
		
		Treasure t = null;
		if (Math.random() < probOfGettingTreasure) {
			// get a random treasure
			
			for (int i = 0; i < 100; i++) {
				Treasure tc = p.T.get((int)(Math.random() * p.T.size()));
				if (tc.isCollected || tc.isMoving || tc.weight > squadSize) continue;
				t = tc;
				break;
			}
			if (t!=null) {
				p.route.R.add("go-here-to-vertex " + t.curVertex.id);
				p.route.R.add("direct workers " + t.weight + " to treasure " + t.name);
				return;
			}
		}
		if (t == null) {
			// get more workers at a vertex with idle workers
			for (int i = 0; i < 100; i++) {
				int r = i==1 ? 0 : (int)(Math.random() * p.V.size()); // make sure onion is tried
				Vertex tv = p.V.get(r);
				if (tv.workersHere.size() == 0) continue;
				p.route.R.add("go-here-to-vertex " + tv.id);
				p.route.R.add("call all");
				return;
			}
			
			// wait for workers to arrive
			if (c.curVertex.isOnion) {
				p.route.R.add("wait-workers " + (c.squad.size()+1));
				p.route.R.add("call all");
				return;
			} 
			
			// go to onion
			p.route.R.add("go-here-to-vertex 0");
			return;
		}
		
	}

}
