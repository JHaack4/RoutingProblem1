import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Builder implements KeyListener, MouseListener {
	
	JFrame frame;
	MyPanel panel;
	JLabel label;
	Graphics g;
	final Problem p;
	
	Builder(Problem p) {
		this.p = p;
		loadPanel();
		updateGraphics();
	}
	
	String infoString = "";
	
	void loadPanel() {
		//EventQueue.invokeLater(new Runnable() {
		//public void run() {
			try {
				panel = new MyPanel();
				panel.setBounds(0, 0, 1000, 1000);
				panel.setBackground(Color.gray);
				panel.setLayout(null);
				panel.setFocusable(true);
				panel.addKeyListener(this);
				panel.addMouseListener(this);
				
				//g = panel.getGraphics();
		
				frame = new JFrame();				
				frame.setBounds(10, 10, 1000, 1000);
				frame.setContentPane(panel);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//frame.getContentPane().setLayout(null);
				frame.setVisible(true);
	
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error: Loading frame error");
			}
			
		//}
	//});	
	}
	
	Font ft = new Font("Serif", Font.PLAIN,15);
	Font ft60 = new Font("Serif", Font.PLAIN,40);
	
	void updateGraphics() {
		
		panel.removeAll();
		//g.clearRect(0, 0, 1000, 1000);
		
		//g.fillOval(370, 350, 100, 100);
		
				
		/*label = new JLabel("<html>" + p.state().replaceAll("\n", "<br>") + "</html>");
		label.setFont(ft);
		label.setForeground(new Color(230,230,230));
		label.setBounds(10, 10, 1000, 1000);
		frame.getContentPane().add(label);*/
				
		
		//JLabel jLabel3 = new JLabel(new ImageIcon(imgs[cursor]));
		//jLabel3.setBounds(N*W+10, 10, W, W);
		//frame.getContentPane().add(jLabel3);
		
		panel.repaint();
	}
	
	public class MyPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
		 
			 g.clearRect(0, 0, 1000, 1000);
			 
			 int vSize = 50;
			 int tSize = 0;
			 int cSize = 16;
			 int wSize = 4;
			 g.setFont(ft60);
			 
			 ds(g,infoString,50,50);
			 g.setFont(ft);
			 
			 for (int q = 0; q < p.V.size(); q++) {
				 Vertex v = p.V.get(q);
				 g.setColor(new Color(0,0,0));
				 g.fillOval((int)v.x-vSize/2, (int)v.y-vSize/2, vSize, vSize);
				 g.setColor(new Color(150,150,150));
				 ds(g,"v"+v.id,v.x,v.y);
				 
				/* int sz = v.workersHere.size();
				 for (int qq = 0; qq < v.workersHere.size(); qq++) {
					 Worker wk = v.workersHere.get(qq);
					 double xx = v.x + (vSize/2) * Math.cos(2*3.14159*qq/sz) + (vSize/3) * Math.random();
					 double yy = v.y + (vSize/2) * Math.sin(2*3.14159*qq/sz) + (vSize/3) * Math.random();
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }*/
			 }
			 
			 for (int q = 0; q < p.E.size(); q++) {
				 Edge e = p.E.get(q);
				 g.setColor(new Color(0,0,0));
				 g.drawLine((int)e.v.x, (int)e.v.y, (int)e.w.x, (int)e.w.y);
				 g.setColor(new Color(50,50,50));
				 ds(g,(int)e.distance+"",(e.v.x+e.w.x)/2,(e.v.y+e.w.y)/2);
			 }

			 for (int q = 0; q < p.T.size(); q++) {
				 Treasure t = p.T.get(q);
				 if (t.isCollected) continue;
				 tSize = t.weight + 5;
				 Vertex v = t.curVertex;
				 Vertex w = (t.curEdge==null) ? v : t.curEdge.otherVertex(v);
				 int hash = t.hashCode();
				 double x = v.x + t.curLocationOnEdge * (w.x-v.x) + (vSize/3) * Math.cos(2*3.14159*hash/360);
				 double y = v.y + t.curLocationOnEdge * (w.y-v.y) + (vSize/3) * Math.sin(2*3.14159*hash/360);;
				 g.setColor(new Color(230,130,0));
				 g.drawOval((int)x-tSize/2, (int)y-tSize/2, tSize, tSize);
				 ds(g,"t"+t.name,x-10,y+5);
				 /*
				 int sz = t.carriers.size();
				 for (int qq = 0; qq < sz; qq++) {
					 Worker wk = t.carriers.get(qq);
					 double xx = x + (tSize/2) * Math.cos(2*3.14159*qq/sz);
					 double yy = y + (tSize/2) * Math.sin(2*3.14159*qq/sz);
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }*/
			 }
			 
			 /*for (int q = 0; q < p.C.size(); q++) {
				 Captain c = p.C.get(q);
				 Vertex v = c.curVertex;
				 Vertex w = (c.curEdge==null) ? v : c.curEdge.otherVertex(v);
				 double x = v.x + c.curLocationOnEdge * (w.x-v.x);
				 double y = v.y + c.curLocationOnEdge * (w.y-v.y);
				 g.setColor(new Color(30,30,200));
				 g.fillOval((int)x-cSize/2, (int)y-cSize/2, cSize, cSize);
				 //ds(g,"c",x-10,0y);
				 
				 int sz = c.squad.size();
				 for (int qq = 0; qq < sz; qq++) {
					 Worker wk = c.squad.get(qq);
					 double xx = x + (cSize/2) * Math.cos(2*3.14159*qq/sz) + (cSize/2) * Math.random();
					 double yy = y + (cSize/2) * Math.sin(2*3.14159*qq/sz) + (cSize/2) * Math.random();
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }
			 }*/
			 
			 
			  
		     // draw oval
		     //g.drawOval(20 + 10*(int)p.curTime, 30, 75, 100); 
		 
		     // draw circle
		     //g.drawOval(150, 30, 100, 100); 
		  }
	}
	
	void ds(Graphics g, String s, double x, double y) {
		g.drawChars(s.toCharArray(), 0, s.length(), (int)x, (int)y);
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TAuto-generated method stub
		
	}

	Vertex cl1 = null;
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		
		int x = arg0.getX();
		int y = arg0.getY();
		if (infoString.equals("")) return;
		if (infoString.charAt(0) == 'v') {
			String[] sc = infoString.substring(1).trim().split(" ");
			int id = p.V.size();
			Vertex v = new Vertex(p, id);
			v.x = x;
			v.y = y;
			for (int i = 0; i < sc.length; i++) {
				if (sc[i].equals("x")) {
					++i;
					v.x = Double.parseDouble(sc[i]);
				}
				else if (sc[i].equals("y")) {
					++i;
					v.y = Double.parseDouble(sc[i]);
				}
			}
			p.V.add(v);
			System.out.println("vertex " + id + " x " + x + " y " + y);
		}
		if (infoString.charAt(0) == 'e') {
			Vertex cl = null;
			for (Vertex v: p.V) {
				if (Math.sqrt((x-v.x)*(x-v.x) + (y-v.y)*(y-v.y)) < 20) {
					cl = v;
				}
			}
			if (cl == null) return;
			if (cl1 == null) {
				cl1 = cl;
				return;
			}
			Vertex vv = cl1;
			Vertex ww = cl;
			cl1 = null;
			double distance = -1;
			String[] sc = infoString.substring(1).trim().split(" ");
			for (int i = 0; i < sc.length; i++) {
				if (sc[i].equals("d")) {
					++i;
					distance = Double.parseDouble(sc[i]);
				}
			}
			if (distance == -1) { // distance not provided by file
				distance = (int)Math.sqrt(vv.x*ww.x + vv.y*ww.y);
			}
			Edge e = new Edge(p, p.E.size(), vv, ww, distance);
			p.E.add(e);
			vv.adj.add(e);
			ww.adj.add(e);
			System.out.println("edge " + vv.id + " " + ww.id + " distance " + distance);
		}
		if (infoString.charAt(0) == 't') {
			Vertex cl = null;
			for (Vertex v: p.V) {
				if (Math.sqrt((x-v.x)*(x-v.x) + (y-v.y)*(y-v.y)) < 20) {
					cl = v;
				}
			}
			if (cl == null) return;
			Vertex vv = cl;
			int weight = 1;
			int maxCarriers = -1;
			double distance = -1;
			String name = "" + p.T.size();
			String[] sc = infoString.substring(1).trim().split(" ");
			for (int i = 0; i < sc.length; i++) {
				if (sc[i].charAt(0) == 'w') {
					++i;
					weight = Integer.parseInt(sc[i]);
				}
				if (sc[i].charAt(0) == 'm') {
					++i;
					maxCarriers = Integer.parseInt(sc[i]);
				}
				if (sc[i].charAt(0) == 'n') {
					++i;
					name = sc[i];
				}
			}
			if (maxCarriers == -1) { // distance not provided by file
				maxCarriers = weight * 2;
			}
			Treasure t = new Treasure(p, p.T.size(), vv, weight, maxCarriers);
			t.name = name;
			p.T.add(t);
			vv.treasuresHere.add(t);
			System.out.println("treasure " + vv.id + " weight " + weight + " max-carriers " + maxCarriers + " name " + name);
		}
		updateGraphics();
	}
	

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			infoString = infoString.substring(0,Math.max(0,infoString.length()-1));
			updateGraphics();
			return;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
			infoString = "";
			updateGraphics();
			return;
		}
		switch(arg0.getKeyChar()) {
		case 'v':
		case 't':
		case 'e':
		default:
			infoString += arg0.getKeyChar() + "";
		}
		updateGraphics();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TDO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TDO Auto-generated method stub
		
	}

}
