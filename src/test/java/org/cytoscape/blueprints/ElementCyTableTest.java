package org.cytoscape.blueprints;

import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.AbstractCyTableTest;
import org.cytoscape.model.CyRow;
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetListWithNonList() {
		table.createColumn("someList", Boolean.class, false);
		attrs.getList("someList", Boolean.class);
	}
	
	@Test
	public void testRenameColumnApplysToRows() {
		table.createColumn("testIt", Boolean.class, false);
		attrs.set("testIt", true);
		table.getColumn("testIt").setName("testItBetter");
		assertNotNull(table.getColumn("testItBetter"));
		assertNull(table.getColumn("testIt"));
		assertTrue(attrs.get("testItBetter", Boolean.class));
		assertNull(attrs.get("testIt", Boolean.class));
	}

	//What does getMatching Rows do if the object isn't a valid type?
	//the Non List get method doesn't do any exceptions, so I assume they aren't tested
}
