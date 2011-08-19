package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cytoscape.blueprints.GraphCytoscape;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableEntry;
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
import com.tinkerpop.blueprints.pgm.impls.sail.SailGraph;
import com.tinkerpop.blueprints.pgm.impls.sail.impls.NativeStoreSailGraph;

public class SailTest {

	private static final String TEMP_DB = "target/sailNaitiveStore";

	@Mock
	private CyEventHelper eventHelper;

	private CyNetwork net;
	private static Graph graphImplementation;
	
	@BeforeClass
	public static void initialize() throws Exception {
		final File dbDir = new File(TEMP_DB);
		
		if(dbDir.exists() == false) {
			graphImplementation = new NativeStoreSailGraph(TEMP_DB);
			final File brca1RDF = new File("./src/test/resources/P38398.rdf");
			assertTrue(brca1RDF.exists());

			final File yeastReactome = new File("./src/test/resources/yeast.owl");
			assertTrue(yeastReactome.exists());

			// Create RDF from file
			((NativeStoreSailGraph) graphImplementation).loadRDF(brca1RDF.toURI().toURL().openStream(),
					"http://purl.uniprot.org/uniprot/P38398", "rdf-xml", "http://purl.uniprot.org/uniprot/P38398");
			System.out.println("RDF loaded: " + brca1RDF.toString());

			((NativeStoreSailGraph) graphImplementation).loadRDF(yeastReactome.toURI().toURL().openStream(),
					"http://www.reactome.org/biopax/68322", "rdf-xml", "http://www.biopax.org/yeast");
			System.out.println("RDF loaded: " + yeastReactome.toString());
		} else {
			graphImplementation = new NativeStoreSailGraph(TEMP_DB);
		}
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
		assertEquals(100100, net.getEdgeCount());

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
		final String query1 = 
				"SELECT ?reaction ?ecnumber WHERE { ?reaction bp:EC-NUMBER \"3.5.3.1\"^^<http://www.w3.org/2001/XMLSchema#string> }";
		
		final List<Map<String, Vertex>> res = ((SailGraph) graphImplementation).executeSparql(query1);
		
		assertNotNull(res);
		
		for(final Map<String, Vertex> entry:res) {
			System.out.println(entry.keySet());
			System.out.println(entry.values());
		}
		assertEquals(1, res.size());
		
		// Since CyNode cannot take URI as its ID, it should be saved as an table entry.
		Collection<CyRow> rows = net.getDefaultNodeTable().getMatchingRows(CyTableEntry.NAME, "http://www.reactome.org/biopax/68322#biochemicalReaction537");
		assertNotNull(rows);
		//assertTrue(rows.size() != 0);
	
	}

}
