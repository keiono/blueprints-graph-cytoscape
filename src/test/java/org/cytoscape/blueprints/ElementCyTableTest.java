package org.cytoscape.blueprints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.AbstractCyTableTest;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable.SavePolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ElementCyTableTest extends AbstractCyTableTest {
	
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		this.eventHelper = new DummyCyEventHelper();;
		table = new ElementCyTable("Table1", "SUID", Long.class, true,
				false, SavePolicy.DO_NOT_SAVE, eventHelper);
		table2 = new ElementCyTable("Table2", "SUID", Long.class, true,
				false, SavePolicy.DO_NOT_SAVE, eventHelper);
		
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
