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
		eventHelper = new DummyCyEventHelper();
		table = new ElementCyTable(1, "Table1", eventHelper, false);
		table2 = new ElementCyTable(2, "Table2", eventHelper, false);
		//attrs = new ElementCyRow(table, new DummyElement(1));
		attrs = table.getRow(1l);
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
		CyRow testRow = table.getRow(5l);
		table.createColumn("testIt", Boolean.class, false);
		testRow.set("testIt", true);
		table.getColumn("testIt").setName("testItBetter");
		assertNotNull(table.getColumn("testItBetter"));
		assertNull(table.getColumn("testIt"));
		assertTrue(testRow.get("testItBetter", Boolean.class));
		//assertNull(testRow.get("testIt", Boolean.class));
	}
	
	@Test
	public void testIsPrimaryKey() {
		table.createColumn("notPrimary", Boolean.class, true);
		assertTrue(table.getPrimaryKey().isPrimaryKey());
		assertFalse(table.getColumn("notPrimary").isPrimaryKey());
		
	}

	//What does getMatching Rows do if the object isn't a valid type?
	//the Non List get method doesn't do any exceptions, so I assume they aren't tested
}
