import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class factoringTests implements Settings{
	Vector<FactoringMethod> methods = new Vector<FactoringMethod>();
	int methodIndex = 0;
	
	private static final int[] smallPrimes = {2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97};

	@Before
	public void setUp() throws Exception {
		if(SMALL > -1){
			if(DEBUGLEVEL > 0) System.err.println("Using the Small Numbers method");
			methods.add(SMALL,new SmallNumbers());
		}
		if(POLLARDS > -1){
			if(DEBUGLEVEL > 0) System.err.println("Using the Pollard's Rho method");
			methods.add(POLLARDS,new PollardsRho());
		}
		if(NAIVE > -1){
			if(DEBUGLEVEL > 0) System.err.println("Using the Naive method");
			//methodes.add(NAIVE,new navie());
		}
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void smallPrimeTest() {
		FactoringMethod method = methods.get(methodIndex);
		for(int i = 0;i<smallPrimes.length;i++){
			BigInteger number = new BigInteger(Integer.toString(smallPrimes[i]));
			Vector<BigInteger> factors = method.factorize(number);
			assertNotNull("could't factor: "+number, factors);
			assertEquals("Wrong number of factors, it should hav been: 1 but was: "+ factors.size(), 
					1, factors.size());
			assertTrue("The factor was wrong, it should have been:\n"+number+"\nbut was:\n"+factors.toString(), 
					number.equals(factors.get(0)));
		}
	}
	
	@Test
	public void twoFactorsTest(){
		FactoringMethod method = methods.get(methodIndex);
		
		for(int j = 0;j<(smallPrimes.length/2);j++){
			for(int i = 0;i<smallPrimes.length;i++){
				Vector<BigInteger> expected = new Vector<BigInteger>(2);
				expected.add(0,new BigInteger(Integer.toString(smallPrimes[j])));
				expected.add(1,new BigInteger(Integer.toString(smallPrimes[i])));
				BigInteger number = expected.get(0).multiply(expected.get(1));
				if(number.compareTo(Constants.PrecalculatedSize)> 0) continue;
				Vector<BigInteger> factors = method.factorize(number);
				assertNotNull("could't factor: "+number, factors);
				assertEquals("Wrong number of factors, it should hav been: "+ expected.size() + " but was: "+ factors.size(), 
						expected.size(), factors.size());
				Collections.sort(expected);
				Collections.sort(factors);
				for(int f = 0;f<factors.size();f++){
				assertEquals("Some factors was wrong, they should have been:\n"+expected.toString()+"\nbut was:\n"+factors.toString(), 
						expected.get(f),factors.get(f));
				}
			}
		}
	}
	
	@Test
	public void perfectPowerTest(){
		//TODO
		fail("Not yet implemented");
		
	}
	
	@Test
	public void nextMethod(){
		while(methodIndex < methods.size()){
			methodIndex++;
			if(methods.get(methodIndex) != null) break;  
			
		}
	}

}
