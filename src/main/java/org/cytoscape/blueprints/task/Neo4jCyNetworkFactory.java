package org.cytoscape.blueprints.task;

import org.cytoscape.event.CyEventHelper;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;

public class Neo4jCyNetworkFactory extends AbstractBlueprintsCyNetworkFactory {

	public Neo4jCyNetworkFactory(String neo4jDB, CyEventHelper eventHelper) {
		super(eventHelper);
		setGraph(new Neo4jGraph(neo4jDB));
	}

}
