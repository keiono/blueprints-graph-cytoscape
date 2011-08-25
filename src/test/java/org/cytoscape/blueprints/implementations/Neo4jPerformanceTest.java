package org.cytoscape.blueprints.implementations;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.pgm.TransactionalGraph.Mode;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph;
import com.tinkerpop.blueprints.pgm.util.TransactionalGraphHelper;
import com.tinkerpop.blueprints.pgm.util.TransactionalGraphHelper.CommitManager;

public class Neo4jPerformanceTest {

	private static final String TEMP_DB = "target" + File.separator + "orientTestDB";
	private static final int NUMBER_OF_NODES = 1000;
	private static final int NUMBER_OF_ITERATION = 10;

	private static TransactionalGraph graphImplementation;

	@BeforeClass
	public static void initialize() {
		graphImplementation = new OrientGraph(TEMP_DB);
		graphImplementation.setTransactionMode(Mode.MANUAL);
		graphImplementation.startTransaction();
		graphImplementation.clear();
		graphImplementation.stopTransaction(Conclusion.SUCCESS);
	}

	@AfterClass
	public static void shutdown() throws Exception {
		graphImplementation.shutdown();
	}

	@Before
	public void setup() {
		graphImplementation.setTransactionMode(Mode.AUTOMATIC);
	}

	@After
	public void clearDB() throws Exception {
		graphImplementation.setTransactionMode(Mode.MANUAL);
		graphImplementation.startTransaction();
		graphImplementation.clear();
		graphImplementation.stopTransaction(Conclusion.SUCCESS);
	}

	@Test
	public void testWithManager() {
		for (int j = 0; j < NUMBER_OF_ITERATION; j++) {
			long start = System.currentTimeMillis();

			final CommitManager manager = TransactionalGraphHelper.createCommitManager(graphImplementation,
					NUMBER_OF_NODES);
			for (int i = 0; i < NUMBER_OF_NODES; i++) {
				graphImplementation.addVertex(null);
				manager.incrCounter();
			}
			manager.close();
			final long time = (System.currentTimeMillis() - start);
			System.out.println((j+1) + ": With helper, add node transaction finished in " + time + " msec.");

			checkNumberOfNodes(j + 1);
		}
	}

	@Test
	public void testWithoutManager() {
		for (int j = 0; j < NUMBER_OF_ITERATION; j++) {
			long start = System.currentTimeMillis();

			graphImplementation.setTransactionMode(Mode.MANUAL);
			graphImplementation.startTransaction();

			for (int i = 0; i < NUMBER_OF_NODES; i++)
				graphImplementation.addVertex(null);

			graphImplementation.stopTransaction(Conclusion.SUCCESS);
			final long time = (System.currentTimeMillis() - start);
			System.out.println((j+1) + ": Without helper, add node transaction finished in " + time + " msec.");

			graphImplementation.startTransaction();
			checkNumberOfNodes(j+1);
			graphImplementation.stopTransaction(Conclusion.SUCCESS);
		}

	}

	private void checkNumberOfNodes(int loopCount) {
		Iterable<Vertex> iterable = graphImplementation.getVertices();
		int counter = 0;
		final Iterator<Vertex> itr = iterable.iterator();
		while (itr.hasNext()) {
			counter++;
			itr.next();
		}
		assertEquals(NUMBER_OF_NODES * loopCount, counter);
	}

}
