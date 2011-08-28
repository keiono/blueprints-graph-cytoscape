package org.cytoscape.blueprints.task;

import org.cytoscape.event.CyEventHelper;

import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph;

public class OrientDBCyNetworkFactory extends AbstractBlueprintsCyNetworkFactory {

	public OrientDBCyNetworkFactory(String orientDBURL, CyEventHelper eventHelper) {
		super(eventHelper);
		setGraph(new OrientGraph(orientDBURL));
	}

}
