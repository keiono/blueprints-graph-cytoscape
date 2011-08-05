package org.cytoscape.blueprints;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SUIDFactory;

import com.tinkerpop.blueprints.pgm.Edge;

/**
 * Wrapper for Blueprints edge.
 *
 */
public class EdgeCytoscape implements CyEdge {

	private final Edge edge;
	private final int index;
	private final boolean isDirected;
	private final CyNode sourceNode;
	private final CyNode targetNode;
	
	private ElementCyRow row;
	
	private CyEventHelper eventHelper;
	
	EdgeCytoscape(final Edge edge, final int index, final boolean isDirected, final CyNode s, final CyNode t, CyTable table, final CyEventHelper eventHelper) {
		this.edge = edge;
		this.index = index;
		this.isDirected= isDirected;
		this.sourceNode = s;
		this.targetNode = t;
		
		this.eventHelper = eventHelper;
		
		row = new ElementCyRow(table, this.edge, eventHelper);/*
		row.set("name", "");
		row.set("selected", false);
		row.set("interaction", "");*/
	}
	
	@Override
	public CyRow getCyRow(String tableName) {
		return row;
	}

	@Override
	public CyRow getCyRow() {
		// TODO Auto-generated method stub
		return row;
	}

	// Return SUID Of edge
	public long getSUID() {
		return Long.parseLong(edge.getId().toString());
	}

	// Return Index of Edge
	public int getIndex() {
		return index;
	}

	// Return the Source Vertex
	public CyNode getSource() {
		return sourceNode;
	}

	// Return the Target Vertex
	public CyNode getTarget() {
		return targetNode;
	}

	//Return Whether the Edge is Directed
	public boolean isDirected() {
		return isDirected;
	}

}
