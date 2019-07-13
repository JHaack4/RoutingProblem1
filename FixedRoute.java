import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FixedRoute extends Route {

	FixedRoute(ArrayList<String> route) {
		super();
		for (String r: route) R.add(r);
	}
	  
	@Override
	public void computeNextRoutingDecision(Problem p) {
		// do nothing
		
	}
}