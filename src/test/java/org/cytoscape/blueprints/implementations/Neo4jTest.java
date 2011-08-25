package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.*;

import java.io.File;

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

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.util.TransactionalGraphHelper;
import com.tinkerpop.blueprints.pgm.util.TransactionalGraphHelper.CommitManager;

/**
 * Test CyNetwork based on Neo4j implementation
 * 
 */
public class Neo4jTest extends AbstractCyNetworkTest {

	private static final String TEMP_DB = "target" + File.separator + "neo4jDB";

	@Mock
	private CyEventHelper eventHelper;

	// Singleton. Actual Neo4j DB.
	private static Graph graphImplementation;

	@BeforeClass
	public static void initialize() {
		graphImplementation = new Neo4jGraph(TEMP_DB);
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
	public void clearDB() throws Exception {
		// Remove all nodes and edges.
		//final CommitManager manager = TransactionalGraphHelper.createCommitManager(
				//(TransactionalGraph) graphImplementation, 1);
		((Neo4jGraph) graphImplementation).clear();
		//manager.close();
	}

	@Test
	public void testNeo4j() {
		assertNotNull(this.net);
	}

	@Test
	public void testExistingDB() {
		// Creates very simple DB before calling CyNetwork methods
		final Vertex a = graphImplementation.addVertex(null);
		final Vertex b = graphImplementation.addVertex(null);
		a.setProperty("name", "node A");
		b.setProperty("name", "node B");
		final Edge e = graphImplementation.addEdge(null, a, b, "interacts_with");
		e.setProperty("type", "pp");

		assertEquals(2, net.getNodeCount());
		assertEquals(1, net.getEdgeCount());

	}

	@Test
	public void testPerformance() {

		// TODO: we need utility class to handle transactions.
		long start = System.currentTimeMillis();

		final CommitManager manager = TransactionalGraphHelper.createCommitManager(
				(TransactionalGraph) graphImplementation, 1000);
		for (int i = 0; i < 1000; i++) {
			net.addNode();
			manager.incrCounter();
		}
		manager.close();
		final long time = (System.currentTimeMillis() - start);
		System.out.println("Nodes added in " + time + " msec.");
		
		assertEquals(1000, net.getNodeCount());
		
		// If we does not use transaction utility method, this can be minutes or hours...
		assertTrue(2000>time);
		
		graphImplementation.clear();
		
		System.out.println("Clearing is bad...");
		
	}

}
