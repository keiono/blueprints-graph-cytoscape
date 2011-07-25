package org.cytoscape.blueprints;

import java.util.ArrayList;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

import junit.framework.TestCase;

public class IterateMeTest extends TestCase {

	private final IterateMe<String> it = new IterateMe<String>();
	
	public void setUp() throws Exception {
	}

	
	public void tearDown() throws Exception {
	}
	
	public void testSizeOneIterable() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("Test");
		one.add("Test2");
		it.combine(one);
		int count = 0;
		for (String i: it) {
			count++;
		}
		assertEquals(count,2);
	}
	
	public void testSizeTwoIterable() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("Test");
		one.add("Test2");
		ArrayList<String> two = new ArrayList<String>();
		two.add("Two");
		two.add("Two2");
		it.combine(one, two);
		int count = 0;
		for (String i: it) {
			count++;
		}
		assertEquals(count,4);
	}
	
	public void testSizeEmptyIterable() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		it.combine(one);
		int count = 0;
		for (String i: it) {
			count++;
		}
		assertEquals(count,0);
	}
	
	public void testSizeEmptyIterables() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		ArrayList<String> two = new ArrayList<String>();
		it.combine(one, two);
		int count = 0;
		for (String i: it) {
			count++;
		}
		assertEquals(count,0);
	}
	
	public void testSizeOnlyFirstHas() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("First only");
		one.add("First only again");
		ArrayList<String> two = new ArrayList<String>();
		it.combine(one, two);
		int count = 0;
		for (String i: it) {
			count++;
		}
		assertEquals(count,2);
	}
	
	public void testSizeOnlySecondHas() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		ArrayList<String> two = new ArrayList<String>();
		two.add("Second only");
		two.add("Second only again");
		it.combine(one, two);
		int count = 0;
		for (String i: it) {
			count++;
		}
		assertEquals(count,2);
	}
	
	public void testOneMaintainValue() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("Maintain");
		it.combine(one);
		for (String i: it) {
			assertTrue("Maintain".equals(i));
		}
	}
	
	public void testTwoMaintainValue() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("Maintain");
		ArrayList<String> two = new ArrayList<String>();
		two.add("Maintainer");
		it.combine(one, two);
		ArrayList<String> orig = new ArrayList<String>();
		orig.add("Maintain");
		orig.add("Maintainer");
		ArrayList<String> compare = new ArrayList<String>();
		for (String i: it) {
			compare.add(i);
		}
		assertEquals(orig, compare);
	}
	
	public void testSizeWithConstruct() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("Construct");
		ArrayList<String> two = new ArrayList<String>();
		two.add("Constructor");
		it.combine(one, two);
		IterateMe<String> it2 = new IterateMe<String>(one,two);
		int itSize = 0;
		for (String i: it) {
			itSize++;
		}
		int it2Size = 0;
		for (String i: it2) {
			it2Size++;
		}
		assertEquals(itSize,it2Size,2);
	}
	
	public void testValuesWithConstruct() throws Exception {
		ArrayList<String> one = new ArrayList<String>();
		one.add("Construct");
		ArrayList<String> two = new ArrayList<String>();
		two.add("Constructor");
		it.combine(one, two);
		IterateMe<String> it2 = new IterateMe<String>(one,two);
		
		ArrayList<String> itR = new ArrayList<String>();
		ArrayList<String> it2R = new ArrayList<String>();
		for (String i: it) {
			itR.add(i);
		}
		for (String i: it2) {
			it2R.add(i);
		}
		assertEquals(itR,it2R);
	}
	
}
