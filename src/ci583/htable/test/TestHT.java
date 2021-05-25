package ci583.htable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ci583.htable.impl.Hashtable;
import ci583.htable.impl.Hashtable.PROBE_TYPE;

public class TestHT {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testEmpty() {//check return null
		Hashtable<Boolean> h = new Hashtable<Boolean>(10);
		assertNull(h.get("foo"));
	}

	@Test
	public void testZeroSizeArray() {//check can handel 0 size array
		Hashtable<Boolean> h = new Hashtable<>(0);
		h.put("yes", true);
		assertTrue(h.get("yes"));
	}

	@Test
	public void testFoundNotFound() {//check item can be inserted and found
		Hashtable<Boolean> h = new Hashtable<>(10);
		h.put("yes", true);
		assertTrue(h.get("yes"));
		assertNull(h.get("no"));
	}

	@Test
	public void testDuplicates() {//check items can be overwritten
		Hashtable<String> h = new Hashtable<>(100);
		for(int i=0;i<50;i++) {
		    h.put(i+"", i+"");
		}
		h.put("a", "a");
		h.put("b", "b");
		h.put("a", "c");
		h.put("b", "d");
		assertEquals(h.get("a"), "c");
		assertEquals(h.get("b"), "d");
		assertEquals(h.getKeys().size(), 52);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullKey() {//check put validated for null
		Hashtable<Boolean> h = new Hashtable<>(10);
		h.put(null, true);
	}

	@Test
	public void testInsert() {//stress test performance
		Hashtable<Boolean> h = new Hashtable<>(1000, PROBE_TYPE.DOUBLE_HASH);
		for(int i=0;i<2000;i++) {
			for(int j=2000;j>0;j--) {
				h.put(i+":"+j, true);
			}
		}
		
		for(int i=0;i<2000;i++) {
			for(int j=2000;j>0;j--) {
				assertTrue(h.hasKey(i+":"+j));
			}
		}
	}

	@Test
	public void testGet() {//test put and get
		Hashtable<String> h = new Hashtable<>(9);
		for(int i=0;i<10;i++) {
			for(int j=10;j>0;j--) {
				h.put(i+":"+j, j+":"+i);
				//System.out.println("DEBUG: putting number " + i + ", " + j);
			}
		}
		for(int i=0;i<10;i++) {
			for(int j=10;j>0;j--) {
				assertEquals(h.get(i+":"+j), j+":"+i);
				//System.out.println("DEBUG: finding number " + i + ", " + j);
			}
		}
	}
	
	@Test
	public void testNull() {//get null if nothing is there
		Hashtable<Integer> h = new Hashtable<Integer>(20);
		for(int i=0;i<10;i++) h.put(Integer.valueOf(i).toString(), Integer.valueOf(i));
		assertNull(h.get(11+""));
	}

	@Test
	public void testCapacity() {
		Hashtable<Integer> h = new Hashtable<>(20, Hashtable.PROBE_TYPE.LINEAR_PROBE);
		assertEquals(h.getCapacity(), 23);//23 is smallest prime > 20
		for(int i=0;i<20;i++) h.put(Integer.valueOf(i).toString(), Integer.valueOf(i));
		assertFalse(h.getCapacity() == 23);//should have resized
		assertFalse(h.getLoadFactor() > 0.6);
	}
	
	@Test
	public void testKeys() {//check getKeys() works
		Hashtable<Integer> h = new Hashtable<Integer>(20, Hashtable.PROBE_TYPE.LINEAR_PROBE);
		h.put("bananas", 1);
		h.put("pyjamas", 99);
		h.put("kedgeree", 1);
		for(String k: h.getKeys()) {
			assertTrue(k.equals("bananas") || k.equals("pyjamas") || k.equals("kedgeree"));
		}
	}

	@Test
	public void testKeysQUADRATIC_PROBE() {//chech QUADRATIC_PROBE works
		Hashtable<Integer> h = new Hashtable<Integer>(20, Hashtable.PROBE_TYPE.QUADRATIC_PROBE);
		h.put("bananas", 1);
		h.put("pyjamas", 99);
		h.put("kedgeree", 1);
		for(String k: h.getKeys()) {
			assertTrue(k.equals("bananas") || k.equals("pyjamas") || k.equals("kedgeree"));
		}
	}
}