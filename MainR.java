
public class MainR {

	public static void main(String args[]) {
		new MainR().run();
	}
	
	public void run() {
		int iter = 0;
		while (true) {
			System.out.println("\n\n\nITER = " + iter++);
			//FixedRoute r = new FixedRoute("src\\route1-1.txt");
			RouterRandomLegal r = new RouterRandomLegal();
			Problem p = new Problem("src\\problem1.txt", r, /*draw*/false, /*print*/true);
			System.out.println(Util.state(p));
			double time = p.run(150);
			//System.exit(0);
			if (time < 87) {
				for (String s: p.route.R) {
					System.out.println(s);
				}
				break;
			}
		}
	}
	
}


// todo: pretty print routes and problem statements, clean up statements
