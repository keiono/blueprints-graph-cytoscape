package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Iterator;

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
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.impls.sail.impls.NativeStoreSailGraph;

public class SailTest {

	private static final String TEMP_DB = "target/sailNaitiveStore";

	@Mock
	private CyEventHelper eventHelper;

	private CyNetwork net;
	private static Graph graphImplementation;

	@BeforeClass
	public static void initialize() throws Exception {
		graphImplementation = new NativeStoreSailGraph(TEMP_DB);
		final File brca1RDF = new File("./src/test/resources/P38398.rdf");
		assertTrue(brca1RDF.exists());

		// Create RDF from file
		((NativeStoreSailGraph) graphImplementation).loadRDF(brca1RDF.toURI().toURL().openStream(),
				"http://purl.uniprot.org/uniprot/P38398", "rdf-xml", "http://purl.uniprot.org/uniprot/P38398");
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

	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedMethod() {
		assertNotNull(net.getNodeCount());
	}

	@Test
	public void testRDF() throws Exception {
		assertEquals(6264, net.getEdgeCount());

		for (final Edge edge : graphImplementation.getEdges()) {
			System.out.println("Edge ID = " + edge.getId());
		}

		final Vertex v1 = graphImplementation
				.getVertex("\"Characterization of a carboxy-terminal BRCA1 interacting protein.\"");

		assertNotNull(v1);
		assertNotNull(v1.getPropertyKeys());
		assertEquals(2, v1.getPropertyKeys().size());

		Iterator<String> itr = v1.getPropertyKeys().iterator();
		String propKey1 = itr.next();
		System.out.println("Prop Key = " + propKey1);
		assertNotNull(v1.getProperty(propKey1));
		System.out.println("Prop Val = " + v1.getProperty(propKey1));

		String propKey2 = itr.next();
		System.out.println("Prop Key = " + propKey2);
		assertNotNull(v1.getProperty(propKey2));
		System.out.println("Prop Val = " + v1.getProperty(propKey2));
		
		

	}
	
	@Test
	public void testSPARQL() throws Exception {
		// Send Sparql Query
		
	}

}
