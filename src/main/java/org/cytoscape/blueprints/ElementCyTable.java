package org.cytoscape.blueprints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.RowsCreatedEvent;

public class ElementCyTable implements CyTable {

	private long suid;
	private String title;
	private boolean isImmutable;
	
	private int virtualColumnReferences;
	
	final private CyEventHelper eventHelper;
	
	private final Map<Long, CyRow> rows;
    private final Map<String, CyColumn> cols;
	
	ElementCyTable(long id, String title, final CyEventHelper eventHelper) {
		this.suid = id;
		rows = new HashMap<Long, CyRow>();
		cols = new HashMap<String, CyColumn>();
		setTitle(title);
		
		//Create Primary Key Column
		createColumn("Primary", Long.class, true);
		
		this.isImmutable = false;
		
		this.eventHelper = eventHelper;
		
		this.virtualColumnReferences = 0;
	}
	
	@Override
	public long getSUID() {
		return suid;
	}

	@Override
	public boolean isPublic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Mutability getMutability() {
		if (isImmutable)
			return Mutability.PERMANENTLY_IMMUTABLE;
		if (virtualColumnReferences == 0)
			return Mutability.MUTABLE;
		else
			return Mutability.IMMUTABLE_DUE_TO_VIRT_COLUMN_REFERENCES;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		
	}

	@Override
	public CyColumn getPrimaryKey() {
		return getColumn("Primary");
	}

	@Override
	public CyColumn getColumn(String columnName) {
		return cols.get(columnName);
	}

	// Return All Columns
	public Collection<CyColumn> getColumns() {
		return cols.values();
	}

	@Override
	public void deleteColumn(String columnName) {
		cols.remove(columnName);
		if (eventHelper != null)
			eventHelper.fireEvent(new ColumnDeletedEvent(this, columnName));
	}

	@Override //Needs Type
	public <T> void createColumn(String columnName, Class<? extends T> type,
			boolean isImmutable) {
		if (type == List.class)
			throw new IllegalArgumentException("Use createListColumn for Lists");
		if (columnName == null) 
			throw new NullPointerException("Null Column name");
		if (type == null) 
			throw new NullPointerException("Null Column Type");
		if (getColumn(columnName) != null) {
			throw new IllegalArgumentException("Column Already Exists.");
		}
		if (type == Integer.class || 
				type == Boolean.class || 
				type == Long.class || 
				type == Double.class || 
				type == String.class) {
			
			cols.put(columnName, new ElementCyColumn(this, columnName, isImmutable, type, false));
			if (eventHelper != null)
				eventHelper.fireEvent(new ColumnCreatedEvent(this, columnName));
		} else {
			throw new IllegalArgumentException("Invalid Column type");
		}
	}
	
	@Override //Needs Type
	public <T> void createListColumn(String columnName,
			Class<T> listElementType, boolean isImmutable) {
		if (columnName == null) 
			throw new NullPointerException("Null Column name");
		if (listElementType == null) 
			throw new NullPointerException("Null listElement Type");
		if (getColumn(columnName) != null) {
			throw new IllegalArgumentException("Column Already Exists.");
		}
		if (listElementType == Integer.class || 
			listElementType == Boolean.class || 
			listElementType == Long.class || 
			listElementType == Double.class || 
			listElementType == String.class) {
				cols.put(columnName, new ElementCyColumn(this, columnName, isImmutable, listElementType, true));
				if (eventHelper != null)
					eventHelper.fireEvent(new ColumnCreatedEvent(this, columnName));
		} else {
			throw new IllegalArgumentException("Invalid List Column type");
			
		}
	}

	//Returns the Row with the Specified Key
	public CyRow getRow(Object primaryKey) {
		if (primaryKey == null) {
			throw new NullPointerException("Null Primary Key");
		}
		if (rows.get(primaryKey) == null) {
			ElementCyRow newRow = new ElementCyRow(this, new DummyElement(primaryKey));
			addRow(newRow);
			eventHelper.addEventPayload((CyTable)this, (Object)primaryKey, RowsCreatedEvent.class);
			return newRow;
		} else {
			return rows.get(primaryKey);
		}
	}

	//Checks if the Row with the Specified Key Exists
	public boolean rowExists(Object primaryKey) {
		return rows.containsKey(primaryKey);
	}

	//Returns A List of all Rows
	public List<CyRow> getAllRows() {
		return new ArrayList<CyRow>(rows.values());
	}

	@Override
	public String getLastInternalError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<CyRow> getMatchingRows(String columnName, Object value) {
		Set<CyRow> matches = new HashSet<CyRow>();
		for (CyRow row : this.getAllRows()) {
			if (row.get(columnName, value.getClass()) == value) {
				matches.add(row);
			}
		}
		return matches;
	}

	//Return the Number of Rows
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public String addVirtualColumn(String virtualColumn, String sourceColumn,
			CyTable sourceTable, String sourceJoinKey, String targetJoinKey,
			boolean isImmutable) {
		++((ElementCyTable)sourceTable).virtualColumnReferences;
		return null;
	}

	@Override
	public void addVirtualColumns(CyTable sourceTable, String sourceJoinKey,
			String targetJoinKey, boolean isImmutable) {
		// TODO Auto-generated method stub
	}

	@Override
	public SavePolicy getSavePolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSavePolicy(SavePolicy policy) {
		// TODO Auto-generated method stub
		
	}
	
	//Add A CyRow to the Table
	public boolean addRow(ElementCyRow row) {
		return (rows.put(row.getID(), row) != null);
	}
	
	public void renameColumn(String oldName, String newName) {
		for (CyRow row : this.getAllRows()) {
			if (this.getColumn(oldName) != null) {
				if (this.getColumn(oldName).getType() == List.class) {
					row.set(newName, row.getList(oldName,this.getColumn(oldName).getListElementType()));
				} else {
					row.set(newName, row.get(oldName,this.getColumn(oldName).getType()));
				}
				row.set(oldName, null);
				System.out.println(oldName + " " + newName);
			}
		}
		cols.put(newName, cols.remove(oldName));
	}


	@Override
	public void swap(CyTable otherTable) {
		
		final ElementCyTable other = (ElementCyTable)otherTable;
		
		final String tempTitle = title;
		title = other.getTitle();
		other.setTitle(tempTitle);
		
	}

}
