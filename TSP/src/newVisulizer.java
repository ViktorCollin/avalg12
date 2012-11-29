import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class newVisulizer extends Canvas {
	private static final long serialVersionUID = 1L;
	private static final boolean SLOWMOTION = false;
	private static final int PADDING = 30;
	private static final int SIZE = Generator.MAX+(2*PADDING);
	private static final int NODESIZE = 10;
	
	private BufferStrategy strategy;
	private int[] nodesX;
	private int[] nodesY;
	private int[][] distanceMatrix;

	public newVisulizer(float[] nodesX, float[] nodesY, int[][] distanceMatrix) {
		this.nodesX = new int[nodesX.length];
		this.nodesY = new int[nodesX.length];
		this.distanceMatrix = distanceMatrix;
		for(int i=0;i<nodesX.length;i++){
			this.nodesX[i] = Math.round(nodesX[i])+PADDING;
			this.nodesY[i] = Math.round(nodesY[i])+PADDING;
		}
		JFrame container = new JFrame("TSP Graph");
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(SIZE,SIZE));
		panel.setLayout(null);
		setBounds(0,0,SIZE,SIZE);
		panel.add(this);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIgnoreRepaint(true);
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		drawNodes();
		strategy.show();
	}
	
	public void drawNodes(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0,SIZE,SIZE);
		g.setColor(Color.DARK_GRAY);
		for(int i=0;i<nodesX.length;i++){
			g.drawString(i+"", nodesX[i]-(NODESIZE/2)-5, nodesY[i]-(NODESIZE/2)-5);
		}
		g.setColor(Color.red);
		for(int i=0;i<nodesX.length;i++){
			g.fillRoundRect(nodesX[i]-(NODESIZE/2), nodesY[i]-(NODESIZE/2), NODESIZE, NODESIZE, NODESIZE, NODESIZE);
		}
		g.dispose();
	}
	
	public void drawEdges(int[] tour, int cost, String mesage){
		drawNodes();
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setColor(Color.cyan);
		g.drawString("Total cost: "+cost, 10, SIZE-10);
		g.drawString(mesage, 10, 20);
		for(int i=1;i<tour.length;i++){
			g.drawLine(nodesX[tour[i-1]], nodesY[tour[i-1]], nodesX[tour[i]], nodesY[tour[i]]);
		}
		g.drawLine(nodesX[tour[0]], nodesY[tour[0]], nodesX[tour[tour.length-1]], nodesY[tour[tour.length-1]]);
		g.dispose();
		strategy.show();
		if(SLOWMOTION){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void drawEdges(int[] tour){
		int cost = sanityCheck(tour);
		drawEdges(tour, cost, "");
	}
	public void drawEdges(int[] tour, String mesage){
		int cost = sanityCheck(tour);
		drawEdges(tour, cost, mesage);
	}
	
	public int sanityCheck(int[] tour){
		boolean[] visited = new boolean[nodesX.length];
		int cost = 0;
		visited[tour[0]] = true;
		for(int i=1;i<tour.length;i++){
			visited[tour[i]] = true;
			cost += distanceMatrix[tour[i-1]][tour[i]];
		}
		cost += distanceMatrix[tour[0]][tour[tour.length-1]];
		for(int i=0;i<visited.length;i++){
			if(!visited[i]) System.err.println("NODE: "+i+" NOT VISITED!!!");
		}
		return cost;
	}

}
