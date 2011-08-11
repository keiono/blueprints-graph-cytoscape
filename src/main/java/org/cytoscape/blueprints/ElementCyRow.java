package org.cytoscape.blueprints;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyPayloadEvent;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsCreatedEvent;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;

public class ElementCyRow implements CyRow {

	private final Element ele;
	private final CyTable table;
	
	private final CyEventHelper eventHelper;
	
	ElementCyRow (final CyTable table, final Element ele, final CyEventHelper eventHelper) {
		this.ele = ele;
		this.table = table;
		
		this.eventHelper = eventHelper;
		
		// FIXME: this does not work for Sail.  We may need some consition to handle special case?
		// Automatically add the SUID as the Primary Key
		ele.setProperty(table.getPrimaryKey().getName(), ele.getId());
		
		//if (!table.rowExists(ele.getId())) {
			//((ElementCyTable)table).addRow(this);
		//}
	}

	@Override
	public <T> T get(String columnName, Class<? extends T> type) {
		if (table.getColumn(columnName) != null) {
			VirtualColumnInfo virtual = table.getColumn(columnName).getVirtualColumnInfo();
			if (virtual.isVirtual()) {
				return virtual.getSourceTable().getRow(this.getID()).get(virtual.getSourceColumn(), type);
			}
		} else {
			throw new IllegalArgumentException("No Such Column");
		}
		Object o = ele.getProperty(columnName); //wrong type, o null
		return type.cast(o);
	}

	@Override
	public <T> List<T> getList(String columnName, Class<T> listElementType) {
		if (table.getColumn(columnName) != null) {
			VirtualColumnInfo virtual = table.getColumn(columnName).getVirtualColumnInfo();
			if (virtual.isVirtual()) {
				return virtual.getSourceTable().getRow(this.getID()).getList(virtual.getSourceColumn(), listElementType);
			}
		} else {
			throw new IllegalArgumentException("No Such Column");
		}
		if (table.getColumn(columnName).getType() != List.class)
			throw new IllegalArgumentException("Not a list, please use get()");
		if (table.getColumn(columnName).getListElementType() != listElementType)
			throw new IllegalArgumentException("Invalid List Element Type");
		Object o = ele.getProperty(columnName); //check valid column
		if(o instanceof List) {
			return (List<T>) o;
		}
		return null;
	}

	@Override
	public <T> void set(String columnName, T value) {
		if (columnName == null)
			throw new NullPointerException("Accessing Null Column");
		if (table.getColumn(columnName) != null) {
			VirtualColumnInfo virtual = table.getColumn(columnName).getVirtualColumnInfo();
			if (virtual.isVirtual()) {
				virtual.getSourceTable().getRow(this.getID()).set(virtual.getSourceColumn(), value);
			}
		} else {
			throw new IllegalArgumentException("No Such Column");
		}
		if (value == null) {
			ele.removeProperty(columnName);
			eventHelper.addEventPayload(null, null, null);
		} else if (!table.getColumn(columnName).getType().isAssignableFrom(value.getClass()))
			throw new IllegalArgumentException("Values of wrong type" + table.getColumn(columnName).getType() + " vs " + value.getClass() );
		else {
			ele.setProperty(columnName, value);
			eventHelper.addEventPayload(null, null, null);
		}
	}

	@Override
	public boolean isSet(String columnName) {
		if (table.getColumn(columnName) != null) {
			VirtualColumnInfo virtual = table.getColumn(columnName).getVirtualColumnInfo();
			if (virtual.isVirtual()) {
				//Should this use getMatchingRows for different key matching?
				return virtual.getSourceTable().getRow(this.getID()).isSet(virtual.getSourceColumn());
			}
			return (ele.getProperty(columnName) != null);
		} else {
			return false;
		}
	}

	@Override
	public Map<String, Object> getAllValues() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(String key : ele.getPropertyKeys()) {
			map.put(key, ele.getProperty(key));
		}
		return map;
	}

	@Override
	public Object getRaw(String columnName) {
		VirtualColumnInfo virtual = table.getColumn(columnName).getVirtualColumnInfo();
		if (virtual.isVirtual()) {
			//This should return the getRaw method for the proper row
		}
		return ele.getProperty(columnName);
	}

	@Override
	public CyTable getTable() {
		return table;
	}
	
	//Return Elements SUID
	public Long getID() {
		return Long.parseLong(ele.getId().toString());
	}

}
