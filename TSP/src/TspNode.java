
public class TspNode {



	int nodeNumber;
	float xPos;
	float yPos;
	

	public TspNode(int nodeNumber, float xPos, float yPos) {
		this.nodeNumber = nodeNumber;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	@Override
	public int hashCode() {
		return nodeNumber;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TspNode other = (TspNode) obj;
		if (nodeNumber != other.nodeNumber)
			return false;
		if (Float.floatToIntBits(xPos) != Float.floatToIntBits(other.xPos))
			return false;
		if (Float.floatToIntBits(yPos) != Float.floatToIntBits(other.yPos))
			return false;
		return true;
	}

}
