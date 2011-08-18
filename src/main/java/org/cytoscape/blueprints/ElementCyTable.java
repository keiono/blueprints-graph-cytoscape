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
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.RowsCreatedEvent;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * CyTable implementation created from Property Graph Model's node/edge properties.
 *
 */
public class ElementCyTable implements CyTable {
	
	// Property graph which is storing actual data.
	private final Graph graph;
	
	// Table created from property graph SHOULD be associated with Vertex or Edge. 
	private final Class<? extends CyTableEntry> tableType;
	
	// This cannot be final since we need to support swap operation.
	private long suid;
	
	// This may NOT be unique.
	private String title;
	
	private boolean isImmutable;
	private final boolean isPublic;
	
	private SavePolicy savePolicy;
	
	private final String primaryKeyName;
	
	private int virtualColumnReferences;
	
	final private CyEventHelper eventHelper;
	
	private Map<Object, CyRow> rows;
	private Map<String, CyColumn> cols;

	/**
	 * Creates table from {@link Graph} properties.
	 * 
	 * @param title
	 * @param primaryKeyName
	 * @param primaryKeyType
	 * @param isPublic
	 * @param isImmutable
	 * @param savePolicy
	 * @param eventHelper
	 */
	ElementCyTable(final Graph graph, Class<? extends CyTableEntry> tableType, final String title, final String primaryKeyName, final Class<?> primaryKeyType,
			final boolean isPublic, final boolean isImmutable, final SavePolicy savePolicy, 	final CyEventHelper eventHelper) {

		if(graph == null)
			throw new NullPointerException("Property graph is null.");
		if(tableType == null)
			throw new NullPointerException("tableType is null.");
		if(eventHelper == null)
			throw new NullPointerException("eventHelper is null.");
		this.eventHelper = eventHelper;
		this.graph = graph;
		this.tableType = tableType;
		
		// Table's session-unique ID.
		this.suid = SUIDFactory.getNextSUID();
		
		this.isImmutable = isImmutable;
		this.isPublic = isPublic;
		this.savePolicy = savePolicy;
		
		this.primaryKeyName = primaryKeyName;
		
		rows = new HashMap<Object, CyRow>();
		cols = new HashMap<String, CyColumn>();

		setTitle(title);

		// Create Primary Key Column
		createColumn(primaryKeyName, primaryKeyType, true);
		((ElementCyColumn) getColumn(primaryKeyName)).setPrimary();

		this.virtualColumnReferences = 0;		
	}
	
	@Override
	public long getSUID() {
		return suid;
	}

	@Override
	public boolean isPublic() {
		return isPublic;
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
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public CyColumn getPrimaryKey() {
		return getColumn(primaryKeyName);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	synchronized public CyColumn getColumn(final String columnName) {
			return cols.get(columnName);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public Collection<CyColumn> getColumns() {
		return cols.values();
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public void deleteColumn(final String columnName) {
		if(columnName == null)
			return;
		
		if (getColumn(columnName) != null && getColumn(columnName).isImmutable())
			throw new IllegalArgumentException("Cannot delete Immutable Column: " + columnName);
		cols.remove(columnName);
		
		eventHelper.fireEvent(new ColumnDeletedEvent(this, columnName));
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public <T> void createColumn(final String columnName, Class<? extends T> type, boolean isImmutable) {
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
			
			cols.put(columnName, new ElementCyColumn(this, columnName, isImmutable, type, false, new ElementVirtualColumnInfo()));
			
			eventHelper.fireEvent(new ColumnCreatedEvent(this, columnName));
		} else {
			throw new IllegalArgumentException("Invalid Column type");
		}
	}
	
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public <T> void createListColumn(final String columnName, Class<T> listElementType, boolean isImmutable) {
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
				cols.put(columnName, new ElementCyColumn(this, columnName, isImmutable, listElementType, true, new ElementVirtualColumnInfo()));
				if (eventHelper != null)
					eventHelper.fireEvent(new ColumnCreatedEvent(this, columnName));
		} else {
			throw new IllegalArgumentException("Invalid List Column type");
		}
	}


	/**
	 *  {@inheritDoc}
	 */
	@Override
	public CyRow getRow(final Object primaryKeyValue) {
		if (primaryKeyValue == null)
			throw new NullPointerException("Null Primary Key");

		final CyRow row = rows.get(primaryKeyValue);
		if (row == null) {
			final Element graphElement;
			if(this.tableType == CyNode.class)
				graphElement = graph.getVertex(primaryKeyValue);   //Find actual index
			else
				graphElement = graph.getEdge(primaryKeyValue);
			
			if(graphElement == null)
				throw new NullPointerException("No such element in the prioperty graph.");
			
			// FIXME: need to use actual data from property graph.
			final ElementCyRow newRow = new ElementCyRow(this, graphElement, eventHelper);
			addRow(newRow);
			eventHelper.addEventPayload((CyTable)this, (Object)primaryKeyValue, RowsCreatedEvent.class);
			return newRow;
		} else
			return row;
	}

	//Checks if the Row with the Specified Key Exists
	@Override
	public boolean rowExists(Object primaryKey) {
		return rows.containsKey(primaryKey);
	}

	//Returns A List of all Rows
	@Override
	public List<CyRow> getAllRows() {
		return new ArrayList<CyRow>(rows.values());
	}

	@Override
	public String getLastInternalError() {
		// TODO IMPLEMENT THIS
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
		String virtColName = getValidColumnName(virtualColumn);
		ElementVirtualColumnInfo virtInfo = new ElementVirtualColumnInfo(sourceColumn, sourceJoinKey, targetJoinKey, sourceTable, isImmutable);
		if (sourceTable.getColumn(sourceColumn).getType() == List.class) {
			cols.put(virtColName, new ElementCyColumn(this, virtColName, isImmutable, sourceTable.getColumn(sourceColumn).getListElementType(), true, virtInfo));
		} else {
			cols.put(virtColName, new ElementCyColumn(this, virtColName, isImmutable, sourceTable.getColumn(sourceColumn).getType(), false, virtInfo));
		}
		++((ElementCyTable)sourceTable).virtualColumnReferences;
		return virtColName;
	}

	private String getValidColumnName(String name) {
		int i = 0;
		String newName = name;
		while (cols.containsKey(newName)) {
			i++;
			newName = name + "-" + i;
		}
		return newName;
	}
	
	@Override
	public void addVirtualColumns(CyTable sourceTable, String sourceJoinKey,
			String targetJoinKey, boolean isImmutable) {
	}

	@Override
	public SavePolicy getSavePolicy() {
		return this.savePolicy;
	}

	@Override
	public void setSavePolicy(final SavePolicy savePolicy) {
		this.savePolicy = savePolicy;
	}
	
	//Add A CyRow to the Table
	public boolean addRow(ElementCyRow row) {
		return (rows.put(row.getID(), row) != null);
	}
	
	synchronized public void renameColumn(String oldName, String newName) {
		
		cols.put(newName, cols.get(oldName));
		
		for (CyRow row : this.getAllRows()) {
				if (this.getColumn(oldName).getType() == List.class) {
					row.set(newName, row.getList(oldName,this.getColumn(oldName).getListElementType()));
				} else {
					row.set(newName, row.get(oldName,this.getColumn(oldName).getType()));
				}
				row.set(oldName, null);
		}
		cols.remove(oldName);
	}


	@Override
	public void swap(CyTable otherTable) {

		final ElementCyTable other = (ElementCyTable)otherTable;
		
		final long tempSuid = suid;
		suid = other.suid;
		other.suid = tempSuid;
		
		final String tempTitle = title;
		title = other.title;
		other.title = tempTitle;
		
		final boolean tempIsImmutable = isImmutable;
		isImmutable = other.isImmutable;
		other.isImmutable = tempIsImmutable;
		
		final int tempVirtualColumnReferences = virtualColumnReferences;
		virtualColumnReferences = other.virtualColumnReferences;
		other.virtualColumnReferences = tempVirtualColumnReferences;
		
		final Map<Object, CyRow> tempRows = rows;
		rows = other.rows;
		other.rows = tempRows;
		
		final Map<String, CyColumn> tempCols = cols;
		cols = other.cols;
		other.cols = tempCols;
		
	}
	
	public void setRowEvent(Object key) {
		eventHelper.addEventPayload((CyTable)this, key, RowsCreatedEvent.class);
	}

}
