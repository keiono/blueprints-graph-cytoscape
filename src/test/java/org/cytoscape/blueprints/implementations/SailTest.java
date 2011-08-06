package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.blueprints.GraphCytoscape;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.cytoscape.model.CyNetwork;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.impls.sail.impls.NativeStoreSailGraph;

public class SailTest {

	
	private static final String TEMP_DB = "target/sailNaitiveStore";
	
	@Mock
	private CyEventHelper eventHelper;
	
	private CyNetwork net;
	private static Graph graphImplementation;
	
	@BeforeClass
	public static void initialize() {
		graphImplementation = new NativeStoreSailGraph(TEMP_DB);
	}

	@AfterClass
	public static void shutdown() throws Exception {
		graphImplementation.shutdown();
	}
	
	
	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		net = new GraphCytoscape(graphImplementation, eventHelper);
	}
	
	@After
	public void clear() throws Exception {
		graphImplementation.clear();
	}
	
	@Test
	public void testNetworkExists() {
		assertNotNull(net);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUnsupportedMethod() {
		assertNotNull(net.getNodeCount());
	}
	
	@Test
	public void testAddNode() {
		// FIXME: this always fails since Sail does not accepts non-URI ID.
		net.addNode();
		
	}

}
