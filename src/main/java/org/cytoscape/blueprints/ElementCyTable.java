package org.cytoscape.blueprints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import com.tinkerpop.blueprints.pgm.Graph;

public class ElementCyTable implements CyTable {

	private long suid;
	private String title;
	
	private Graph dummyGraph;
	
	private final Map<Long, CyRow> rows;
    private final Map<String, CyColumn> cols;
	
	ElementCyTable(long id, String title, Graph graph) {
		this.suid = id;
		rows = new HashMap<Long, CyRow>();
		cols = new HashMap<String, CyColumn>();
		setTitle(title);
		
		//Create Primary Key Column
		createColumn("Primary", Long.class, true);
		dummyGraph = graph;
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
		// TODO Auto-generated method stub
		return null;
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
		
	}

	@Override //Needs Type
	public <T> void createColumn(String columnName, Class<? extends T> type,
			boolean isImmutable) {
		if (getColumn(columnName) != null) {
			throw new IllegalArgumentException("Column Already Exists.");
		}
		cols.put(columnName, new ElementCyColumn(this, columnName, isImmutable, type, false));
	}

	@Override //Needs Type
	public <T> void createListColumn(String columnName,
			Class<T> listElementType, boolean isImmutable) {
		if (getColumn(columnName) != null) {
			throw new IllegalArgumentException("Column Already Exists.");
		}
		cols.put(columnName, new ElementCyColumn(this, columnName, isImmutable, listElementType, true));
	}

	//Returns the Row with the Specified Key
	public CyRow getRow(Object primaryKey) {
		if (primaryKey == null) {
			throw new NullPointerException("Null Primary Key");
		}
		if (rows.get(primaryKey) == null) {
			ElementCyRow newRow = new ElementCyRow(this, dummyGraph.addVertex(primaryKey));
			addRow(newRow);
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
		// TODO Auto-generated method stub
		return null;
	}

	//Return the Number of Rows
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public String addVirtualColumn(String virtualColumn, String sourceColumn,
			CyTable sourceTable, String sourceJoinKey, String targetJoinKey,
			boolean isImmutable) {
		// TODO Auto-generated method stub
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
		cols.put(newName, cols.remove(oldName));
	}

}
