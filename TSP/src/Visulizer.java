import java.awt.Color;


public class Visulizer{
	/**
	 * 
	 */
	private static final int SIZE_X = 500;
	private static final int SIZE_Y = 500; 
	private static final int PADDING = 30;
	private static final int CIRCLEDIAMETER = 16;
	private float maxX = Float.MIN_VALUE;
	private float minX = Float.MAX_VALUE;
	private float maxY = Float.MIN_VALUE;
	private float minY = Float.MAX_VALUE;

	int numNodes;
	Canvas myCanvas;
	TspNode[] nodes;


	public Visulizer(TspNode[] nodes) {
		this.nodes = nodes;
		numNodes = nodes.length;
		for(int i=0;i<numNodes;i++){
			minX = nodes[i].xPos < minX ? nodes[i].xPos : minX;
			maxX = nodes[i].xPos > maxX ? nodes[i].xPos : maxX;
			minY = nodes[i].yPos < minY ? nodes[i].yPos : minY;
			maxY = nodes[i].yPos > maxY ? nodes[i].yPos : maxY;
		}
		myCanvas = new Canvas("TSP Graph", SIZE_X, SIZE_Y, new Color(0));
		myCanvas.setVisible(true);
		myCanvas.setForegroundColor(Color.RED);
		for(int i=0;i<numNodes;i++){
			myCanvas.fillCircle(scaleX(nodes[i].xPos)-CIRCLEDIAMETER/2, scaleY(nodes[i].yPos)-CIRCLEDIAMETER/2, CIRCLEDIAMETER);
			System.out.println(i + ": scaleX="+scaleX(nodes[i].xPos) + " scaleY="+scaleY(nodes[i].yPos));
		}
		
	}
	public void drawEdges(int[] order){
		myCanvas.setForegroundColor(Color.CYAN);
		for(int i=1;i<order.length;i++){
			myCanvas.drawLine(scaleX(nodes[i-1].xPos), scaleY(nodes[i-1].yPos), scaleX(nodes[i].xPos), scaleY(nodes[i].yPos));
		}
		myCanvas.drawLine(scaleX(nodes[0].xPos), scaleY(nodes[0].yPos), scaleX(nodes[order.length-1].xPos), scaleY(nodes[order.length-1].yPos));
	}
	
	public int scaleX(float x){
		return Math.round((x-minX)/(maxX-minX) * (SIZE_X-2*PADDING) + PADDING);
	}
	public int scaleY(float y){
		return Math.round((y-minY)/(maxY-minY) * (SIZE_Y-2*PADDING) + PADDING);
	}

}
