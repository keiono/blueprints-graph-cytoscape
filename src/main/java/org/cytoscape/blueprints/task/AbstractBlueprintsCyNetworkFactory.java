package org.cytoscape.blueprints.task;

import org.cytoscape.blueprints.GraphCytoscape;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;

import com.tinkerpop.blueprints.pgm.Graph;


public abstract class AbstractBlueprintsCyNetworkFactory implements CyNetworkFactory {

	private final CyEventHelper eventHelper;
	private Graph graph;
	
	public AbstractBlueprintsCyNetworkFactory(final CyEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}
	@Override
	public CyNetwork getInstance() {
		if(graph == null)
			throw new NullPointerException("Blueprints' graph object is null.");
		
		return new GraphCytoscape(graph, eventHelper);
	}

	@Override
	public CyNetwork getInstanceWithPrivateTables() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setGraph(final Graph graph) {
		this.graph = graph;
	}

}
