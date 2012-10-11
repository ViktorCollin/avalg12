import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


import java.math.BigInteger;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class factoringTests {
	Factorizer f;
	private static final int[] smallPrimes = {2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97};

	@Before
	public void setUp() throws Exception {
		f = new Factorizer();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void smallPrimeTest() {
		for(int i = 0;i<smallPrimes.length;i++){
			BigInteger number = new BigInteger(Integer.toString(smallPrimes[i]));
			BigInteger[] factors = f.factorPollard(number);
			assertNotNull("could't factor: "+number, factors);
			assertEquals("Wrong number of factors, it should hav been: 1 but was: "+ factors.length, 
					1, factors.length);
			assertTrue("The factor was wrong, it should have been:\n"+number+"\nbut was:\n"+Arrays.toString(factors), 
					number.equals(factors[0]));
		}
	}
	
	@Test
	public void twoFactorsTest(){
		BigInteger[] expected = new BigInteger[2];
		for(int j = 0;j<(smallPrimes.length/2);j++){
			for(int i = 0;i<smallPrimes.length;i++){
				expected[0] = new BigInteger(Integer.toString(smallPrimes[j]));
				expected[1] = new BigInteger(Integer.toString(smallPrimes[i]));
				BigInteger number = expected[0].multiply(expected[1]);
				BigInteger[] factors = f.factorPollard(number);
				assertNotNull("could't factor: "+number, factors);
				assertEquals("Wrong number of factors, it should hav been: "+ expected.length + " but was: "+ factors.length, 
						expected.length, factors.length);
				Arrays.sort(expected);
				Arrays.sort(factors);
				assertTrue("Some factors was wrong, they should have been:\n"+Arrays.toString(expected)+"\nbut was:\n"+Arrays.toString(factors), 
						Arrays.equals(expected,factors));
			}
		}
	}
	
	@Test
	public void perfectPowerTest(){
		//TODO
		fail("Not yet implemented");
		
	}

}
