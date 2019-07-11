
import java.util.ArrayList;

public abstract class Route {
	
	ArrayList<String> R; // current feed of routing decisions made
	int i = 0; // pointer to the current instruction to follow
	
	Route() {
		R = new ArrayList<String>();
	}
	
	public abstract void computeNextRoutingDecision(Problem p);
	
}
