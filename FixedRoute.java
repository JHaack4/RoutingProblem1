import java.io.BufferedReader;
import java.io.FileReader;

public class FixedRoute extends Route {

	FixedRoute(String filename) {
		super();
		
		BufferedReader f;
		try {
			f = new BufferedReader(new FileReader(filename));
			while(true) {
				String s = f.readLine();
				if (s == null) break;
				if (s.charAt(0) == ';') continue; //;comment
				if (s.trim().equals("")) continue;
				R.add(s.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	  
	@Override
	public void computeNextRoutingDecision(Problem p) {
		// do nothing
		
	}
}