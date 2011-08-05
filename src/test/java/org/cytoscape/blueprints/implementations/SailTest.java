package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.assertNotNull;

import org.cytoscape.blueprints.GraphCytoscape;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.cytoscape.model.CyNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.sail.impls.NativeStoreSailGraph;

public class SailTest {

	
	private static final String TEMP_DB = "target/sailNaitiveStore";
	
	@Mock
	private CyEventHelper eventHelper;
	
	private CyNetwork net;
	private Graph graphImplementation;	
	
	
	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		graphImplementation = new NativeStoreSailGraph(TEMP_DB);
		net = new GraphCytoscape(this.graphImplementation, eventHelper);
	}
	
	@After
	public void shutdown() throws Exception {
		graphImplementation.shutdown();
	}
	
	@Test
	public void testNetworkExists() {
		assertNotNull(net);
	}
	
	@Test
	public void testAddNode() {
		// FIXME: this always fails since Sail does not accepts non-URI ID.
		net.addNode();
		
	}

}
