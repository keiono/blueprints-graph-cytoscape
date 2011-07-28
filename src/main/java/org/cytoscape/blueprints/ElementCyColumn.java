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
	
	//Constructor
	ElementCyColumn(ElementCyTable table, String name, boolean isImmutable, Class<?> type, boolean isList) {
		this.table = table;
		setName(name);
		this.isImmutable = isImmutable;
		this.type = type;
		this.isList = isList;
	}
	
	//Return the Column Name
	public String getName() {
		return name;
	}

	//Set the Column Name
	public void setName(String newName) {
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
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

}
