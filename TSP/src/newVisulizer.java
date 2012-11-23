import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class newVisulizer extends Canvas {
	private static final long serialVersionUID = 1L;
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
		g.setColor(Color.red);
		for(int i=0;i<nodesX.length;i++){
			g.fillRoundRect(nodesX[i]-(NODESIZE/2), nodesY[i]-(NODESIZE/2), NODESIZE, NODESIZE, NODESIZE, NODESIZE);
		}
		g.dispose();
	}
	
	public void drawEdges(int[] order, int cost, String mesage){
		drawNodes();
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setColor(Color.cyan);
		g.drawString("Total cost: "+cost, 10, SIZE-10);
		g.drawString(mesage, 10, 20);
		for(int i=1;i<order.length;i++){
			g.drawLine(nodesX[order[i-1]], nodesY[order[i-1]], nodesX[order[i]], nodesY[order[i]]);
		}
		g.drawLine(nodesX[order[0]], nodesY[order[0]], nodesX[order[order.length-1]], nodesY[order[order.length-1]]);
		g.dispose();
		strategy.show();
	}
	
	public void drawEdges(int[] order){
		int cost = sanityCheck(order);
		drawEdges(order, cost, "");
	}
	public void drawEdges(int[] order, String mesage){
		int cost = sanityCheck(order);
		drawEdges(order, cost, mesage);
	}
	
	public int sanityCheck(int[] order){
		boolean[] visited = new boolean[nodesX.length];
		int cost = 0;
		visited[order[0]] = true;
		for(int i=1;i<order.length;i++){
			visited[order[i]] = true;
			cost += distanceMatrix[order[i-1]][order[i]];
		}
		cost += distanceMatrix[order[0]][order[order.length-1]];
		for(int i=0;i<visited.length;i++){
			if(!visited[i]) System.err.println("NODE: "+i+" NOT VISITED!!!");
		}
		return cost;
	}

}
