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
	
	JFrame frame;
	MyPanel panel;
	JLabel label;
	Graphics g;
	final Problem p;
	
	Draw(Problem p) {
		this.p = p;
	}
	
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
			 g.setFont(ft);
			 
			 for (int q = 0; q < p.V.size(); q++) {
				 Vertex v = p.V.get(q);
				 g.setColor(new Color(0,0,0));
				 g.fillOval((int)v.x-vSize/2, (int)v.y-vSize/2, vSize, vSize);
				 g.setColor(new Color(150,150,150));
				 ds(g,"v"+v.id,v.x,v.y);
				 
				 int sz = v.workersHere.size();
				 for (int qq = 0; qq < v.workersHere.size(); qq++) {
					 Worker wk = v.workersHere.get(qq);
					 double xx = v.x + (vSize/2) * Math.cos(2*3.14159*qq/sz) + (vSize/3) * Math.random();
					 double yy = v.y + (vSize/2) * Math.sin(2*3.14159*qq/sz) + (vSize/3) * Math.random();
					 g.setColor(new Color(255,0,0)); // color depend on worker...
					 g.drawOval((int)xx-wSize/2, (int)yy-wSize/2, wSize, wSize);
					 //g.setColor(new Color(230,130,0));
				 }
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
			 }
			 
			 
			  
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

}
