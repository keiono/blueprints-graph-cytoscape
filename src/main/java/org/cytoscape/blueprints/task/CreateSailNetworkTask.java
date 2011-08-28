package org.cytoscape.blueprints.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.ValuedTask;

import com.tinkerpop.blueprints.pgm.impls.sail.impls.NativeStoreSailGraph;

public class CreateSailNetworkTask implements ValuedTask<CyNetwork> {
	
	private final String databaseLocation;
	private final AbstractBlueprintsCyNetworkFactory factory;
	
	public CreateSailNetworkTask(final String databaseLocation, final AbstractBlueprintsCyNetworkFactory factory) {
		if(databaseLocation == null)
			throw new NullPointerException("database location is null.");
		if(factory == null)
			throw new NullPointerException("network factory is null.");
			
		this.databaseLocation = databaseLocation;
		this.factory = factory;
	}

	@Override
	public CyNetwork run(TaskMonitor taskMonitor) throws Exception {
		factory.setGraph(new NativeStoreSailGraph(databaseLocation));
		return factory.getInstance();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	
}
