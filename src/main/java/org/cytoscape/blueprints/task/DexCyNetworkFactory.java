package org.cytoscape.blueprints.task;

import org.cytoscape.event.CyEventHelper;

import com.tinkerpop.blueprints.pgm.impls.dex.DexGraph;

public class DexCyNetworkFactory extends AbstractBlueprintsCyNetworkFactory {

	public DexCyNetworkFactory(String dexDBPath, CyEventHelper eventHelper) {
		super(eventHelper);
		setGraph(new DexGraph(dexDBPath));
	}

}
