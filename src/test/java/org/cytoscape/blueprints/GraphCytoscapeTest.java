package org.cytoscape.blueprints;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class GraphCytoscapeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGraphCytoscape() throws Exception {
		final Graph graph = TinkerGraphFactory.createTinkerGraph();
		final GraphCytoscape graphC = new GraphCytoscape(graph);
		
		assertNotNull(graphC);
	}

}
