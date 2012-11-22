import java.awt.Color;
import java.awt.Image;


public class Visulizer{
	/**
	 * 
	 */	 
	private static final int PADDING = 30;
	private static final int SIZE = Generator.MAX+(2*PADDING);
	private static final int CIRCLEDIAMETER = 7;

	int numNodes;
	Canvas myCanvas;
	TspNode[] nodes;
	Image nodeImage;

	public Visulizer(TspNode[] nodes) {
		this.nodes = nodes;
		numNodes = nodes.length;
		myCanvas = new Canvas("TSP Graph", SIZE, SIZE, new Color(0));
		myCanvas.setVisible(true);
		drawNodes();

	}
	public Visulizer(float[] nodesX, float[] nodesY) {
		numNodes = nodesX.length;
		nodes = new TspNode[numNodes];
		for(int i=0;i<numNodes;i++){
			nodes[i] = new TspNode(i, nodesX[i], nodesY[i]);
		}
		myCanvas = new Canvas("TSP Graph", SIZE, SIZE, new Color(0));
		myCanvas.setVisible(true);
		drawNodes();
	}
	
	public void drawNodes(){
		myCanvas.setForegroundColor(Color.RED);
		for(int i=0;i<numNodes;i++){
			myCanvas.fillCircle(Math.round(nodes[i].xPos-CIRCLEDIAMETER/2+PADDING), Math.round(nodes[i].yPos-CIRCLEDIAMETER/2+PADDING), CIRCLEDIAMETER);
		}
		nodeImage = myCanvas.getImage();
	}

	public void drawEdges(int[] order, int cost){
		myCanvas.drawImage(nodeImage, 0, 0);
		myCanvas.setForegroundColor(Color.CYAN);
		for(int i=1;i<order.length;i++){
			myCanvas.drawLine(Math.round(nodes[order[i-1]].xPos+PADDING), Math.round(nodes[order[i-1]].yPos+PADDING), Math.round(nodes[order[i]].xPos+PADDING), Math.round(nodes[order[i]].yPos+PADDING));
		}
		myCanvas.drawLine(Math.round(nodes[order[0]].xPos+PADDING), Math.round(nodes[order[0]].yPos+PADDING), Math.round(nodes[order[order.length-1]].xPos+PADDING), Math.round(nodes[order[order.length-1]].yPos+PADDING));
		myCanvas.drawString("Total cost: "+cost, 10, SIZE-10);
	}

	public void drawEdges(TspNode[] node, int cost){
		myCanvas.drawImage(nodeImage, 0, 0);
		myCanvas.setForegroundColor(Color.CYAN);
		for(int i=1;i<node.length;i++){
			myCanvas.drawLine(Math.round(node[i-1].xPos+PADDING), Math.round(node[i-1].yPos+PADDING), Math.round(node[i].xPos+PADDING), Math.round(node[i].yPos+PADDING));
		}
		myCanvas.drawLine(Math.round(node[0].xPos+PADDING), Math.round(node[0].yPos+PADDING), Math.round(node[node.length-1].xPos+PADDING), Math.round(node[node.length-1].yPos+PADDING));
		myCanvas.drawString("Total cost: "+cost, 10, SIZE-10);
	}

}
