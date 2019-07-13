import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Draw implements KeyListener, MouseListener {
	
	static Draw drawer = new Draw();
	static boolean draw = false;
	static boolean printInfo = false;
	static int millisWaitBetweenDraw = 300;
	
	JFrame frame;
	MyPanel panel;
	JLabel label;
	Graphics g;
	Problem p = null;
	
	Font ft = new Font("Serif", Font.PLAIN, 15);
	Font ft2 = new Font("Serif", Font.BOLD, 20);
	
	void updateGraphics(Problem p) {
		if (!draw) return;
		this.p = p;
		panel.removeAll();		
		panel.repaint();
	}
	
	public class MyPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
		 
			 g.clearRect(0, 0, 1000, 1000);
			 if (p == null) return;
			 
			 int vSize = 60;
			 int tSize = 1; // multiplier on treasure size
			 int cSize = 16;
			 int wSize = 4;
			 g.setFont(ft2);
			 g.setColor(new Color(120,120,120));
			 ds(g,"time: " + String.format("%.2f", p.curTime),10,30);
			 
			 for (int q = 0; q < p.E.size(); q++) {
				 Edge e = p.E.get(q);
				 g.setColor(new Color(200,200,200));
				 g.drawLine((int)e.v.x, (int)e.v.y, (int)e.w.x, (int)e.w.y);
				 g.setColor(new Color(120,120,120));
				 ds(g,(int)e.distance+"",(e.v.x+e.w.x)/2,(e.v.y+e.w.y)/2);
			 }
			 
			 for (int q = 0; q < p.V.size(); q++) {
				 Vertex v = p.V.get(q);
				 g.setColor(new Color(200,200,200));
				 g.fillOval((int)v.x-vSize/2, (int)v.y-vSize/2, vSize, vSize);
				 g.setColor(new Color(170,170,170));
				 ds(g,"v"+v.id,v.x + vSize/4,v.y + vSize/2);
				 
				 int sz = v.workersHere.size();
				 for (int qq = 0; qq < v.workersHere.size(); qq++) {
					 Worker wk = v.workersHere.get(qq);
					 double xx = v.x + (vSize/4) * Math.cos(2*3.14159*qq/sz) + (vSize/6) * Math.random();
					 double yy = v.y + (vSize/4) * Math.sin(2*3.14159*qq/sz) + (vSize/6) * Math.random();
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }
			 }
			 
			 

			 for (int q = 0; q < p.T.size(); q++) {
				 Treasure t = p.T.get(q);
				 if (t.isCollected) continue;
				 tSize = t.weight + 5;
				 Vertex v = t.curVertex;
				 Vertex w = (t.curEdge==null) ? v : t.curEdge.otherVertex(v);
				 int hash = t.curVertex.treasuresHere.indexOf(t);
				 int div = Math.max(1, t.curVertex.treasuresHere.size());
				 double x = v.x + t.curLocationOnEdge * (w.x-v.x) + (vSize/3) * Math.cos(2*3.14159*hash/div);
				 double y = v.y + t.curLocationOnEdge * (w.y-v.y) + (vSize/3) * Math.sin(2*3.14159*hash/div);;
				 g.setColor(new Color(230,130,0));
				 g.drawOval((int)x-tSize/2, (int)y-tSize/2, tSize, tSize);
				 g.setColor(new Color(100,50,0));
				 ds(g,t.weight+"",x-5,y+5);
				 
				 int sz = t.carriers.size();
				 for (int qq = 0; qq < sz; qq++) {
					 Worker wk = t.carriers.get(qq);
					 double xx = x + (tSize/2) * Math.cos(2*3.14159*qq/sz);
					 double yy = y + (tSize/2) * Math.sin(2*3.14159*qq/sz);
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }
			 }
			 
			 for (int q = 0; q < p.C.size(); q++) {
				 Captain c = p.C.get(q);
				 Vertex v = c.curVertex;
				 Vertex w = (c.curEdge==null) ? v : c.curEdge.otherVertex(v);
				 double x = v.x + c.curLocationOnEdge * (w.x-v.x);
				 double y = v.y + c.curLocationOnEdge * (w.y-v.y);
				 g.setColor(new Color(30,30,200));
				 g.fillOval((int)x-cSize/2, (int)y-cSize/2, cSize, cSize);
				 ds(g,"squad size: " + c.squad.size(),10,50);
				 
				 int sz = c.squad.size();
				 for (int qq = 0; qq < sz; qq++) {
					 Worker wk = c.squad.get(qq);
					 double xx = x + (cSize/2) * Math.cos(2*3.14159*qq/sz) + (cSize/2) * Math.random();
					 double yy = y + (cSize/2) * Math.sin(2*3.14159*qq/sz) + (cSize/2) * Math.random();
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }
			 }
			 
		  }
	}
	
	void loadPanel() {
		if (!draw) return;
		try {
			panel = new MyPanel();
			panel.setBounds(0, 0, 1000, 1000);
			panel.setBackground(Color.gray);
			panel.setLayout(null);
			panel.setFocusable(true);
			panel.addKeyListener(this);
			panel.addMouseListener(this);

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

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TAuto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TOO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TDO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TDO Auto-generated method stub
		
	}
	
	static void print(int o) {
		if (!printInfo) return;
		System.out.print(o);
	}
	
	static void println(int o) {
		if (!printInfo) return;
		System.out.println(o);
	}
	
	static void print(double o) {
		if (!printInfo) return;
		System.out.print(o);
	}
	
	static void println(double o) {
		if (!printInfo) return;
		System.out.println(o);
	}
	
	static void print(String o) {
		if (!printInfo) return;
		System.out.print(o);
	}
	
	static void println(String o) {
		if (!printInfo) return;
		System.out.println(o);
	}
	
	static void print(Object o) {
		if (!printInfo) return;
		System.out.print(o);
	}
	
	static void println(Object o) {
		if (!printInfo) return;
		System.out.println(o);
	}

}
