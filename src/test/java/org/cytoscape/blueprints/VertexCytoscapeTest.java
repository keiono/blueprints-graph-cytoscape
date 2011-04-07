package org.cytoscape.blueprints;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class VertexCytoscapeTest {
	
	private VertexCytoscape node;

	@Before
	public void setUp() throws Exception {
		final Graph graph = TinkerGraphFactory.createTinkerGraph();
		node = new VertexCytoscape(graph.getVertex("1"), 1);
	}

	@After
	public void tearDown() throws Exception {
		node = null;
	}

	@Test
	public void testGetCyRowString() {
	}

	@Test
	public void testGetCyRow() {
	}

	@Test
	public void testGetSUID() {
		assertNotNull(node.getSUID());
	}

	@Test
	public void testGetIndex() {
		assertEquals(1, node.getIndex());
	}

	@Test
	public void testGetNetwork() {
		assertNull(node.getNetwork());
	}

	@Test
	public void testSetNetwork() {
	}

}
