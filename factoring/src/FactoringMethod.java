import java.math.BigInteger;
import java.util.Vector;


public interface FactoringMethod extends Settings{
	public Vector<BigInteger> factorize(BigInteger number);
	public String methodName();

}
