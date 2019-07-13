import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainR {

	public static void main(String args[]) {
		new MainR().run();
	}
	
	void build() {
		new Builder(new Problem(new ArrayList<String>(), new RouterRandomLegal()));
	}
	
	public void run() {

		boolean useFixed = true;
		Draw.printInfo = false;
		Draw.draw = false;
		Draw.millisWaitBetweenDraw = 200;
		
		if (useFixed) {
			Draw.draw = true;
			Draw.printInfo = true;
		}
		if (Draw.draw) {
			Draw.drawer.loadPanel();
		}
		
		
		ArrayList<String> problem = Util.loadFile("src\\problem2.txt");
		ArrayList<String> fixedRoute = Util.loadFile("src\\route2-3.txt");
		ArrayList<Double> times = new ArrayList<Double>();

		int best = 91;
		for (int iter = 0; iter < 1e10; iter++) {
			if (iter > 0 && times.size() >= 100 && (iter%10000 == 0 && iter < 100000 || iter%100000 == 0)) {
				Collections.sort(times);
				double sum = 0;
				double sum1 = 0;
				for (int i = 0; i < times.size(); i++) {
					if (i < times.size()/100) sum1 += times.get(i);
					sum += times.get(i);
				}
				System.out.println("\nITER = " + iter);
				System.out.println("avg = " + (sum/times.size()));
				System.out.println("avg top 1% = " + (sum1/(times.size()/100)));
				System.out.print("best 100 =");
				for (int i = 0; i < 100; i++) System.out.print(" " + times.get(i));
				System.out.println();
			}
			Problem p;
			if (useFixed) p = new Problem(problem, new FixedRoute(fixedRoute));
			else p = new Problem(problem, new RouterRandomLegal());
			if (iter == 0) System.out.println(Util.state(p));
			double time = p.run(600);
			times.add(time);
			
			if (time < best) {
				System.out.println("\nITER = " + iter);
				for (String s: p.route.R) {
					System.out.println(s);
				}
				best = (int) time;
				//break;
			}
		}
	}
	
}


// todo: pretty print routes and problem statements, clean up statements
