package org.cytoscape.blueprints;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * Wrapper for Blueprints vertex.
 *
 */
public class VertexCytoscape implements CyNode {
	
	private final Vertex vertex;
	private final int index;
	
	private CyNetwork nestedNetwork;
	
	VertexCytoscape(final Vertex vertex, final int index) {
		this.vertex = vertex;
		this.index = index;
	}

	public CyRow getCyRow(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	public CyRow getCyRow() {
		// TODO Auto-generated method stub
		return null;
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
