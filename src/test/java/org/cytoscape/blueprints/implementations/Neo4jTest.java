package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.*;

import org.cytoscape.blueprints.GraphCytoscape;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.cytoscape.model.CyNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;

/**
 * Test CyNetwork based on Neo4j implementation
 * 
 */
public class Neo4jTest extends AbstractCyNetworkTest {

	private static final String TEMP_DB = "target/neo4jDB";
	
	@Mock
	private CyEventHelper eventHelper;
	
	private Graph graphImplementation;	
	
	
	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		graphImplementation = new Neo4jGraph(TEMP_DB);
		net = new GraphCytoscape(this.graphImplementation, eventHelper);
	}
	
	@After
	public void shutdown() throws Exception {
		graphImplementation.shutdown();
	}
	
	@Test
	public void testNeo4j() {
		assertNotNull(this.net);
		//net.addNode();
		// First, manipulate data model through Blueprints API
//		a = graphImplementation.addVertex(null);
//		b = graphImplementation.addVertex(null);
//		a.setProperty("name","node A");
//		b.setProperty("name","node B");
//		Edge e = graphImplementation.addEdge(null, a, b, "interacts_with");
//		e.setProperty("type", "pp");
	}

}
