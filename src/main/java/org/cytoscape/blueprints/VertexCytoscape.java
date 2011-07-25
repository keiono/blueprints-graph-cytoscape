package org.cytoscape.blueprints;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SUIDFactory;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * Wrapper for Blueprints vertex.
 *s
 */
public class VertexCytoscape implements CyNode {
	
	private final Vertex vertex;
	private final int index;
	
	private CyNetwork nestedNetwork;
	
	private ElementCyRow row;
	
	VertexCytoscape(final Vertex vertex, final int index, CyTable table) {
		this.vertex = vertex;
		this.index = index;
		
		row = new ElementCyRow(table, this.vertex);
	}

	public CyRow getCyRow(String tableName) {
		return row;
	}

	public CyRow getCyRow() {
		// TODO Auto-generated method stub
		return row;
	}

	public long getSUID() {
		return Long.parseLong(vertex.getId().toString());
	}

	public int getIndex() {
		return index;
	}

	public CyNetwork getNetwork() {
		return nestedNetwork;
	}

	public void setNetwork(final CyNetwork network) {
		this.nestedNetwork = network;
	}

}
