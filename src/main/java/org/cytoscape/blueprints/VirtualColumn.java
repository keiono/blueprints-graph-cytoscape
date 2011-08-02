package org.cytoscape.blueprints;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;

public class VirtualColumn {
	
	private CyColumn sourceColumn;
	private CyTable sourceTable;
	private CyTable targetTable;
	private CyColumn sourceJoinColumn;
	private CyColumn targetJoinColumn;
	
	
	public VirtualColumn(CyTable sourceTable, String sourceColumnName, CyTable targetTable, String sourceKeyName, String targetKeyName) {
		this.sourceTable = sourceTable;
		this.targetTable = targetTable;
		this.sourceColumn = sourceTable.getColumn(sourceColumnName);
		this.sourceJoinColumn = sourceTable.getColumn(sourceKeyName);
		this.targetJoinColumn = targetTable.getColumn(targetKeyName); 
		
	}

}
