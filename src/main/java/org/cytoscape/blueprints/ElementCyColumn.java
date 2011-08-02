package org.cytoscape.blueprints;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;

public class ElementCyColumn implements CyColumn {

	private String name;
	private ElementCyTable table;
	private Class<?> type;
	private boolean isImmutable;
	private boolean isList;
	private boolean isPrimary;
	private ElementVirtualColumnInfo virtInfo;
	
	//Constructor
	ElementCyColumn(ElementCyTable table, String name, boolean isImmutable, Class<?> type, boolean isList, ElementVirtualColumnInfo virtInfo) {
		this.table = table;
		this.name = name;
		this.isImmutable = isImmutable;
		this.type = type;
		this.isList = isList;
		this.isPrimary = false;
		this.virtInfo = virtInfo;
	}
	
	//Return the Column Name
	public void setPrimary() {
		this.isPrimary = true;
	}
	
	//Return the Column Name
	public String getName() {
		return name;
	}

	//Set the Column Name
	synchronized public void setName(String newName) {
		if (this.isImmutable())
			throw new IllegalArgumentException("Attempt to rename Immutable Column");
		if (newName == null)
			throw new NullPointerException("Column name cannot be null.");
		final String oldName = name;
		name = newName;
		table.renameColumn(oldName, newName);
	}

	//Return the Type of the Column
	public Class<?> getType() {
		if (isList) {
			return List.class;
		} else {
			return type;
		}
	}

	@Override
	public Class<?> getListElementType() {
		if (isList) {
			return type;
		} else {
			return null;
		}
	}

	@Override
	public boolean isPrimaryKey() {
		return isPrimary;
	}

	//Return if the Table is Immutable
	public boolean isImmutable() {
		return isImmutable;
	}

	//Return the Columns Table
	public CyTable getTable() {
		return table;
	}

	@Override
	public <T> List<T> getValues(Class<? extends T> type) {
		if (virtInfo.isVirtual()) {
			return (table.getColumn(virtInfo.getSourceColumn())).getValues(type);
		}
		if (type == null) 
			throw new NullPointerException();
		ArrayList<T> result = new ArrayList<T>();
		for (CyRow row : table.getAllRows()) {
			result.add(row.get(this.getName(), type));
		}
		return result;
	}

	@Override
	public VirtualColumnInfo getVirtualColumnInfo() {
		return this.virtInfo;
	}

}
