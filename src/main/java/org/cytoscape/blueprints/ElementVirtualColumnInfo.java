package org.cytoscape.blueprints;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;

public class ElementVirtualColumnInfo implements VirtualColumnInfo {

	private boolean isVirtual;
	private String sourceColumn;
	private String sourceJoinKey;
	private String targetJoinKey;
	private CyTable sourceTable;
	private boolean isImmutable;
	
	//Default Non-Virtual Constructor
	ElementVirtualColumnInfo() {
		this.isVirtual = false;
		this.sourceColumn = null;
		this.sourceJoinKey = null;
		this.targetJoinKey = null;
		this.sourceTable = null;
		this.isImmutable = false;
	}
	
	//Virtual Constructor
	ElementVirtualColumnInfo(String sourceColumn, String sourceJoinKey, String targetJoinKey, CyTable sourceTable, boolean isImmutable) {
		this.isVirtual = true;
		this.sourceColumn = sourceColumn;
		this.sourceJoinKey = sourceJoinKey;
		this.targetJoinKey = targetJoinKey;
		this.sourceTable = sourceTable;
		this.isImmutable = isImmutable;
	}
	
	@Override
	public boolean isVirtual() {
		return isVirtual;
	}

	@Override
	public String getSourceColumn() {
		return sourceColumn;
	}

	@Override
	public String getSourceJoinKey() {
		return sourceJoinKey;
	}

	@Override
	public String getTargetJoinKey() {
		return targetJoinKey;
	}

	@Override
	public CyTable getSourceTable() {
		return sourceTable;
	}

	@Override
	public boolean isImmutable() {
		return isImmutable;
	}

}
