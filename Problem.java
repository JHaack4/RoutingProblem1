import java.util.ArrayList;
import java.util.Arrays;

public class Problem {

	ArrayList<Vertex> V = new ArrayList<Vertex>();
	ArrayList<Edge> E = new ArrayList<Edge>();
	ArrayList<Captain> C = new ArrayList<Captain>();
	ArrayList<Worker> W = new ArrayList<Worker>();
	ArrayList<Treasure> T = new ArrayList<Treasure>();
	
	Route route;
	
	int numFramesPerSecond = 1;
	int numFrames = 0;
	double curTime = 0;
	int treasuresCollected = 0;
	
	Problem(ArrayList<String> problem, Route route) {

		this.route = route;
		
		for (int instr_idx = 0; instr_idx < problem.size(); instr_idx++) {
			
			String s = problem.get(instr_idx);
			if (s.indexOf(";") > -1) s = s.substring(0, s.indexOf(";")).trim(); // ;comment
			if (s.length() == 0) continue;
			String[] sc = s.split(" ");
			
			String instr = "";
			if (sc[0].equals("num-frames-per-second")) instr = "num-frames-per-second";
			else if (sc[0].charAt(0) == 'v') instr = "vertex";
			else if (sc[0].charAt(0) == 'e') instr = "edge";
			else if (sc[0].charAt(0) == 't') instr = "treasure";
			else if (sc[0].charAt(0) == 'c') instr = "captain";
			else if (sc[0].charAt(0) == 'w') instr = "worker";
			
			
			if (instr.equals("num-frames-per-second")) {
				numFramesPerSecond = Integer.parseInt(sc[1]);
			}
			else if (instr.equals("vertex")) {
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
			}
			else if (instr.equals("edge")) {
				Vertex v = V.get(Integer.parseInt(sc[1]));
				Vertex w = V.get(Integer.parseInt(sc[2]));
				double distance = -1;
				for (int i = 3; i < sc.length; i++) {
					if (sc[i].charAt(0) == 'd') {
						++i;
						distance = Double.parseDouble(sc[i]);
					}
				}
				if (distance == -1) { // distance not provided by file
					distance = Math.sqrt(v.x*w.x + v.y*w.y);
				}
				Edge e = new Edge(this, E.size(), v, w, distance);
				E.add(e);
			}
			else if (instr.equals("treasure")) {
				Vertex v = V.get(Integer.parseInt(sc[1]));
				int weight = 1;
				int maxCarriers = -1;
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
					if (sc[i].equals("name")) {
						++i;
						name = sc[i];
					}
				}
				if (maxCarriers == -1) { // maxCarriers not provided
					maxCarriers = weight * 2;
				}
				Treasure t = new Treasure(this, T.size(), v, weight, maxCarriers);
				t.name = name;
				T.add(t);
			}
			else if (instr.equals("captain")) {
				Vertex v = V.get(Integer.parseInt(sc[1]));
				Captain c = new Captain(this, C.size(), v);
				c.name = "" + C.size();
				C.add(c);
				for (int i = 2; i < sc.length; i++) {
					if (sc[i].equals("speed")) {
						++i;
						c.captainSpeed = Double.parseDouble(sc[i]);
					}
					if (sc[i].equals("name")) {
						++i;
						c.name = sc[i];
					}
				}
				
			}
			else if (instr.equals("worker")) {
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
					Worker w = new Worker(this, W.size(), V.get(0));
					W.add(w);
				}
			}
			else {
				System.out.println("Misparsed problem statement " + s);
			}
			
		}
		
		Util.computeAllShortestPaths(this);

		
	}
	
	
	double run(double maxTimeAllowed) {
		
		while(true) {

			Draw.println("frames=" + numFrames + " time=" + curTime
							+ " treasures_collected=" + treasuresCollected + "/" + T.size());
			
			
			for (Worker w: W) {
				w.update();
			}
			for (Treasure t: T) {
				t.update();
			}

			for (Captain c: C) {
				c.update();
			}
			
			if (Draw.draw) {
				Draw.drawer.updateGraphics(this);
				try {
					Thread.sleep(Draw.millisWaitBetweenDraw);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (treasuresCollected == T.size()) {
				Draw.println("Problem completed, time=" + curTime);
				route.R.add(String.format("; Complete! time=%.2f", curTime));
				return curTime;
			}
			if (curTime > maxTimeAllowed) {
				Draw.println("Time limit exceeded");
				return maxTimeAllowed;
			}
			
			numFrames += 1;
			curTime = numFrames * 1.0 / numFramesPerSecond;
		}
	}
	

}
