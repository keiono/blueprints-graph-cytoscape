package org.cytoscape.blueprints;

import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.AbstractCyTableTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

import static org.junit.Assert.*;

public class ElementCyTableTest extends AbstractCyTableTest {
	
	@Before
	public void setUp() throws Exception {
		table = new ElementCyTable(1, "Table1");
		table2 = new ElementCyTable(2, "Table2");
		attrs = new ElementCyRow(table, new DummyElement(1));
		eventHelper = new DummyCyEventHelper();
		rowSetMicroListenerWasCalled = false;
		rowCreatedMicroListenerWasCalled = false;
		rowAboutToBeDeletedMicroListenerWasCalled = false;
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSpecialStuff() throws Exception {
		assertNotNull(table);
		assertNotNull(table2);
		assertNotNull(attrs);
	}

}
