package org.cytoscape.blueprints;


import static org.junit.Assert.*;

import java.util.Collections;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.DummyCyNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class GraphCytoscapeTest extends AbstractCyNetworkTest{

	@Mock
	CyEventHelper eventHelper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		final Graph graph = TinkerGraphFactory.createTinkerGraph();
		graph.clear();
		net =  new GraphCytoscape(graph, eventHelper);
	}

	
	public void tearDown() throws Exception {
	}
	

	public void testGraphCytoscape() throws Exception {
		assertNotNull(net);
	}
	
	public void testAddNodeThing() throws Exception {	
		
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();

		// add a directed edge
		net.addEdge(n1, n1, false);
		//System.out.println(net.getNeighborList(n1, CyEdge.Type.ANY).size());
		for (CyNode c:net.getNeighborList(n1, CyEdge.Type.ANY)) {
			//System.out.println(c.getIndex());
		}


	}

}
