package org.cytoscape.blueprints;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.pgm.Element;

public class DummyElement implements Element {

	private Object id;
	private Map<String, Object> propertyMap;
	
	DummyElement(Object id) {
		this.id = id;
		this.propertyMap = new HashMap<String, Object>();
	}
	
	@Override
	public Object getId() {
		return id;
	}

	@Override
	public Object getProperty(String arg0) {
		return propertyMap.get(arg0);
	}

	@Override
	public Set<String> getPropertyKeys() {
		return propertyMap.keySet();
	}

	@Override
	public Object removeProperty(String arg0) {
		return propertyMap.remove(arg0);
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		propertyMap.put(arg0, arg1);
	}

}
