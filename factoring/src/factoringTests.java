import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class factoringTests {
	Factorizer f;
	Vector<BigInteger> factors;

	@Before
	public void setUp() throws Exception {
		f = new Factorizer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void primeTest() {
		// testing 2
		factors = f.factor("2");
		assertEquals(1,factors.size());
		assertEquals(0, factors.get(0).compareTo(new BigInteger("2")));
		// testing 7
		factors = f.factor("7");
		assertEquals(1,factors.size());
		assertEquals(0, factors.get(0).compareTo(new BigInteger("7")));
		// testing 911
		factors = f.factor("911");
		assertEquals(1,factors.size());
		assertEquals(0, factors.get(0).compareTo(new BigInteger("911")));
	}
	
	@Test
	public void smallNumberTest(){
		// testing 6
		factors = f.factor("6");
		assertEquals(2,factors.size());
		int index = factors.indexOf(new BigInteger("2"));
		assertTrue(index!=-1);
		factors.remove(index);
		index = factors.indexOf(new BigInteger("3"));
		assertTrue(index!=-1);
		factors.remove(index);
	}
	
	@Test
	public void squareTest(){
		// testing 9
		factors = f.factor("9");
		assertEquals(2,factors.size());
		int index = factors.indexOf(new BigInteger("3"));
		assertTrue(index!=-1);
		factors.remove(index);
		index = factors.indexOf(new BigInteger("3"));
		assertTrue(index!=-1);
		factors.remove(index);
		
		// testing 4
		factors = f.factor("4");
		assertEquals(2,factors.size());
		index = factors.indexOf(new BigInteger("2"));
		assertTrue(index!=-1);
		factors.remove(index);
		index = factors.indexOf(new BigInteger("2"));
		assertTrue(index!=-1);
		factors.remove(index);
	}

}
