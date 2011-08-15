package org.cytoscape.blueprints;


import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class GraphCytoscapeTest extends AbstractCyNetworkTest{

	@Mock
	CyEventHelper eventHelper;

	// Singleton.
	private static Graph graph;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		graph = TinkerGraphFactory.createTinkerGraph();
		graph.clear();
		net = new GraphCytoscape(graph, eventHelper);
	}
	

	@Test
	public void testGraphCytoscape() throws Exception {
		assertNotNull(net);
	}
	
	@Test
	public void testPreexistingData() throws Exception {
		//Add Remove Vertex and NodeCount
		Vertex a = graph.addVertex(1l);
		Vertex b = graph.addVertex(2l);
		graph.removeVertex(a);
		Vertex c = graph.addVertex(3l);
		assertEquals(2,net.getNodeCount());
		
		//Add Remove Edge and Edge Count
		Edge ea = graph.addEdge(1l, b, c, "");
		Edge eb = graph.addEdge(2l, b, c, "");
		graph.removeEdge(ea);
		Edge ec = graph.addEdge(3l, b, c, "");
		assertEquals(2,net.getEdgeCount());
		
		//Remove Node with Edges and Counts
		graph.removeVertex(b);
		graph.removeVertex(c);
		assertEquals(0,net.getNodeCount());
		assertEquals(0,net.getEdgeCount());	
	}
	
	@Test
	public void testPreexistGetLists() throws Exception {
		Vertex a = graph.addVertex(1l);
		Vertex b = graph.addVertex(2l);
		Vertex c = graph.addVertex(3l);
		assertEquals("getNodeList() Not Syncronized with Graph",3,net.getNodeList().size());
		
		Edge ea = graph.addEdge(1l, a, c, "");
		Edge eb = graph.addEdge(2l, a, b, "");
		Edge ec = graph.addEdge(3l, b, c, "");
		assertEquals("getEdgeList() Not Syncronized with Graph",3,net.getEdgeList().size());
	}
}
