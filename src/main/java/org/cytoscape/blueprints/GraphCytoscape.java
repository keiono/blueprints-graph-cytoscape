package org.cytoscape.blueprints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTable.SavePolicy;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.SUIDFactory;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.oupls.GraphSource;


/**
 * "Ouplementation" of CyNetwork
 *
 */
public class GraphCytoscape implements GraphSource, CyNetwork {
	
	private static final String TITLE_SUFFIX = "(Blueprints Graph)";
	private static final String SUID = "SUID";
	
	private final CyEventHelper eventHelper;
	
	// Wrapped Bleuprints-compatible graph
	private final Graph graph;

	// Session-unique ID for this CyNetwork
	private final long suid;
	
	private int nodeIndex;
	private int edgeIndex;

	// Node and Edge Maps
	private Map<Integer, CyNode> nodeIndexMap;
	private Map<Integer, CyEdge> edgeIndexMap;
	private Map<Vertex, CyNode> nodeMap;
	private Map<Edge, CyEdge> edgeMap;

	// Table Maps
	private final Map<String, CyTable> netTableManager;
	private final Map<String, CyTable> nodeTableManager;
	private final Map<String, CyTable> edgeTableManager;
	
	private final Map<Long, Object> nodeSUID2VertexIdMap;
	private final Map<Long, Object> edgeSUID2EdgeIdMap;


	/**
	 * Wrap the given graph with CyNetwork and GraphSource interface.
	 *  
	 * @param graph actual implementation of the Blueprints-compatible graph
	 * @param eventHelper
	 */
	public GraphCytoscape(final Graph graph, final CyEventHelper eventHelper) {
		if(graph == null)
			throw new NullPointerException("Graph parameter cannot be null.");
		if(eventHelper == null)
			throw new NullPointerException("CyEventHelper is null.");
		
		this.graph = graph;
		this.eventHelper = eventHelper;

		this.suid = SUIDFactory.getNextSUID();
		
		initGraph();
		
		this.nodeIndex = 0;
		this.edgeIndex = 0;
		
		this.nodeIndexMap = new HashMap<Integer, CyNode>();
		this.edgeIndexMap = new HashMap<Integer, CyEdge>();
		this.nodeMap = new HashMap<Vertex, CyNode>();
		this.edgeMap = new HashMap<Edge, CyEdge>();
	
		netTableManager = new HashMap<String, CyTable>();
		nodeTableManager = new HashMap<String, CyTable>();
		edgeTableManager = new HashMap<String, CyTable>();
		
		nodeSUID2VertexIdMap = new HashMap<Long, Object>();
		edgeSUID2EdgeIdMap = new HashMap<Long, Object>();
	
		initDefaultTables();

	}
	
	private void initGraph() {
		
	}
	
	@SuppressWarnings("rawtypes")
	private int countGraphObject(Iterable<? extends Element> itr) {
		int count = 0;
		if (itr instanceof Collection)
			count  = ((Collection) itr).size();
		else {
			for (Element elm : itr)
				count++;
		}
		
		return count;
	}

	private void initDefaultTables() {
		
		//FIXME: create on-memory network table
		// Set Default Network Table and Columns
//		final ElementCyTable netTable = new ElementCyTable(suid + " Network " + TITLE_SUFFIX, SUID, Long.class, true,
//				false, SavePolicy.DO_NOT_SAVE, eventHelper);
//		netTable.addRow(new ElementCyRow(netTable, new DummyElement(this.getSUID()), eventHelper));
//		netTable.createColumn(CyTableEntry.NAME, String.class, true);
//		netTableManager.put(CyNetwork.DEFAULT_ATTRS, netTable);

		// Set Default Node Table and Columns
		ElementCyTable nodeTable = new ElementCyTable(this.graph, CyNode.class, suid + " Node " + TITLE_SUFFIX, SUID, Long.class, true,
				false, SavePolicy.DO_NOT_SAVE, eventHelper);
		nodeTable.createColumn(CyTableEntry.NAME, String.class, false);
		nodeTable.createColumn(CyNetwork.SELECTED, Boolean.class, false);
		nodeTableManager.put(CyNetwork.DEFAULT_ATTRS, nodeTable);

		// Set Default Edge Table and Columns
		ElementCyTable edgeTable = new ElementCyTable(this.graph, CyEdge.class, suid + " Edge " + TITLE_SUFFIX, SUID, Long.class, true,
				false, SavePolicy.DO_NOT_SAVE, eventHelper);
		edgeTable.createColumn(CyTableEntry.NAME, String.class, false);
		edgeTable.createColumn(CyNetwork.SELECTED, Boolean.class, false);
		edgeTable.createColumn(CyEdge.INTERACTION, String.class, false);
		edgeTableManager.put(CyNetwork.DEFAULT_ATTRS, edgeTable);
	}


    //Return the Graph Object
    public Graph getGraph() {
    	return graph;
    }

    //Return Network Row in Specified Table
    public CyRow getCyRow(String tableName) {
    	return netTableManager.get(tableName).getRow(this.suid);
    }

    //Return Network Row from Default Table
    public CyRow getCyRow() {
    	return netTableManager.get(CyNetwork.DEFAULT_ATTRS).getRow(this.suid);
    }
    
    //Return the SUID Of the Network
    public long getSUID() {
    	return this.suid;
    }

	// Adds a Node
	public CyNode addNode() {
		// Cytoscape's SUID. This is required
		final Long generatedID = SUIDFactory.getNextSUID();

		Vertex vertex = null;
		try {
			vertex = graph.addVertex(generatedID);
		} catch (Exception ex) {
			// TODO: How can we handle URI ID?
			vertex = graph.addVertex(null);
		}

		final Object vID = vertex.getId();
		nodeSUID2VertexIdMap.put(generatedID, vID);

		// Wrap it as CyNode
		final CyNode vc = new VertexCytoscape(generatedID, vertex, nodeIndex, getDefaultNodeTable(), eventHelper);
		nodeMap.put(vertex, vc);
		nodeIndexMap.put(nodeIndex++, vc);
		return vc;
	}

    //Removes Nodes  !!DELETES EDGES FROM EDGEMAP EFFECTED!! -- not necessarily tested in tests
    public boolean removeNodes(Collection<CyNode> nodes) {
    	for(CyNode node:nodes) {
	    	if (containsNode(node)) {
	    		Long id = Long.parseLong(nodeSUID2VertexIdMap.get(node.getSUID()).toString());
	    		Vertex vr = graph.getVertex(id);
	    		for (CyEdge e: getAdjacentEdgeList(nodeMap.get(vr), CyEdge.Type.ANY)) {
	    			removeEdges(Collections.singletonList(e));
	    		}
	    		nodeSUID2VertexIdMap.remove(node.getSUID());
		    	graph.removeVertex(vr);
		    	nodeIndexMap.remove(node.getIndex());
		    	nodeMap.remove(vr);
	    	} else {
	    		return false;
	    	}
    	}
    	return true;
    }

    //Adds an Edge
    public CyEdge addEdge(CyNode source, CyNode target, boolean isDirected) {
    	if (containsNode(source) && containsNode(target)) {
    		
    		// Cytoscape's SUID. This is required
    		final Long generatedID = SUIDFactory.getNextSUID();
    		
    		Vertex s = graph.getVertex(nodeSUID2VertexIdMap.get(source.getSUID()));
    		Vertex t = graph.getVertex(nodeSUID2VertexIdMap.get(target.getSUID()));
    		
    		Edge edge = null;
    		try {
    			edge = graph.addEdge(generatedID,s,t,"");
    		} catch (Exception ex) {
    			// TODO: How can we handle URI ID?
    			edge = graph.addEdge(null,s,t,"");
    		}

    		final Object vID = edge.getId();
    		edgeSUID2EdgeIdMap.put(generatedID, vID);
    		
    		final EdgeCytoscape ec = new EdgeCytoscape(generatedID, edge, edgeIndex, isDirected, source, target, getDefaultEdgeTable(), eventHelper);
    		edgeMap.put(edge,ec);
    		edgeIndexMap.put(edgeIndex++,ec);
    		this.edgeSUID2EdgeIdMap.put(ec.getSUID(), edge.getId());
    		return ec;
    	}
		throw new RuntimeException();
    }
    
    //Remove Edges
    public boolean removeEdges(Collection<CyEdge> edges) {
	    if (edges == null) { return false; }
    	for(CyEdge edge:edges) {
	    	if (edge != null && containsEdge(edge)) {
	    		Edge er = graph.getEdge(edgeSUID2EdgeIdMap.get(edge.getSUID()));
	    		edgeSUID2EdgeIdMap.remove(edge.getSUID());
		    	graph.removeEdge(er);
		    	edgeIndexMap.remove(edge.getIndex());
		    	edgeMap.remove(er);
	    	} else {
	    		return false;
	    	}
    	}
    	return true;
    }
    
	// Returns Current Node Count
	public int getNodeCount() {
		// TODO: this is necessary to handle updates outside of the CyNetwork API call.
		// TODO: are there any alternative?
		return countGraphObject(graph.getVertices());
	}

    //Returns Current Edge Count
    public int getEdgeCount() {
    	// TODO: this is necessary to handle updates outside of the CyNetwork API call.
		// TODO: are there any alternative?
    	return countGraphObject(graph.getEdges());
    }

    //Return List of Nodes
    public List<CyNode> getNodeList() {
    	final ArrayList<CyNode> nodeList = new ArrayList<CyNode>(nodeMap.values());
		return nodeList;
    }
    
    //Return List of Edges
    public List<CyEdge> getEdgeList() {
    	final ArrayList<CyEdge> edgeList = new ArrayList<CyEdge>(edgeMap.values());
		return edgeList;
    }
    
    @Override
	public boolean containsNode(final CyNode node) {
		if(node == null)
			return false;
		
		final Object vertexID = nodeSUID2VertexIdMap.get(node.getSUID());
		if(graph.getVertex(vertexID) == null)
			return false;
		else
			return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsEdge(final CyEdge edge) {
		if (edge == null)
			return false;

		final Object edgeID = edgeSUID2EdgeIdMap.get(edge.getSUID());
		if (graph.getEdge(edgeID) == null)
			return false;
		else
			return true;
	}
    
    //Checks if the Nodes Are Connected by an Edge
    public boolean containsEdge(CyNode from, CyNode to) {
    	ArrayList<CyEdge> commonCheck = new ArrayList<CyEdge>();
    	if (containsNode(from) && containsNode(to) && from != to) {
    		Vertex f = graph.getVertex(nodeSUID2VertexIdMap.get(from.getSUID()));
    		Vertex t = graph.getVertex(nodeSUID2VertexIdMap.get(to.getSUID()));
    		for(Edge e: new IterateMe<Edge>(f.getOutEdges(),f.getInEdges())){
    			CyEdge ce = edgeMap.get(e);
    			commonCheck.add(ce);
    		}
    		for(Edge e: new IterateMe<Edge>(t.getOutEdges(),t.getInEdges())){
    			CyEdge ce = edgeMap.get(e);
    			if (commonCheck.contains(ce)) {
					return true;
				}
    		}
    	}
    	return false;
    }
    
    //Return the Node With the Specified Index
    public CyNode getNode(int index) {
    	if (nodeIndexMap.containsKey(index)) {
    		return nodeIndexMap.get(index);
    	}
    	return null;
    }

    //Return the Edge With the Specified Index
    public CyEdge getEdge(int index) {
    	if (edgeIndexMap.containsKey(index)) {
    		return edgeIndexMap.get(index);
    	}
    	return null;
    }

    //Return a List of Neighboring Nodes to the Specified Node
    public List<CyNode> getNeighborList(CyNode node, Type edgeType) {
    	ArrayList<CyNode> result = new ArrayList<CyNode>();
    	if (containsNode(node)) {
	    	Vertex vertex = graph.getVertex(nodeSUID2VertexIdMap.get(node.getSUID()));
			switch (edgeType) {
				case OUTGOING:
					for(Edge e: vertex.getOutEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (ce.isDirected()) {
							result.add(ce.getTarget());
						}
					}
				break;
				case INCOMING:
					for(Edge e: vertex.getInEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (ce.isDirected()) {
							result.add(ce.getSource());
						}
					}
				break;
				case DIRECTED:
					for(Edge e: vertex.getInEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (ce.isDirected()) {
							result.add(ce.getSource());
						}
					}
					for(Edge e: vertex.getOutEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (ce.isDirected() && !result.contains(ce)) {
							result.add(ce.getTarget());
						}
					}
				break;
				case UNDIRECTED:
					for(Edge e: vertex.getInEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (!ce.isDirected()) {
							result.add(ce.getSource());
						}
					}
					for(Edge e: vertex.getOutEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (!ce.isDirected() && !result.contains(ce)) {
							result.add(ce.getTarget());
						}
					}
				break;
				case ANY:
					for(Edge e: vertex.getInEdges()) {
						CyEdge ce = edgeMap.get(e);
						result.add(ce.getSource());
					}
					for(Edge e: vertex.getOutEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (!result.contains(ce.getTarget())){
							result.add(ce.getTarget());
						}
					}
				break;
			}
    	}
    	return result;
    }
    
    //Return the Edges from the Specified Node
    public List<CyEdge> getAdjacentEdgeList(CyNode node, Type edgeType) {
    	ArrayList<CyEdge> result = new ArrayList<CyEdge>();
    	if (containsNode(node)) {
	    	Vertex vertex = graph.getVertex(nodeSUID2VertexIdMap.get(node.getSUID()));
			switch (edgeType) {
				case OUTGOING:
					for(Edge e: vertex.getOutEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (ce.isDirected()) {
							result.add(ce);
						}
					}
				break;
				case INCOMING:
					for(Edge e: vertex.getInEdges()) {
						CyEdge ce = edgeMap.get(e);
						if (ce.isDirected()) {
							result.add(ce);
						}
					}
				break;
				case DIRECTED:
					for(Edge e: new IterateMe<Edge>(vertex.getInEdges(),vertex.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (ce.isDirected() && !result.contains(ce)) {
							result.add(ce);
						}
		    		}
				break;
				case UNDIRECTED:
					for(Edge e: new IterateMe<Edge>(vertex.getInEdges(),vertex.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (!ce.isDirected() && !result.contains(ce)) {
							result.add(ce);
						}
		    		}
				break;
				case ANY:
					for(Edge e: new IterateMe<Edge>(vertex.getInEdges(),vertex.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (!result.contains(ce)){
							result.add(ce);
						}
		    		}
				break;
			}
    	}
    	return result;
    }

    //Get the list of edged connecting the Specified Nodes
    public List<CyEdge> getConnectingEdgeList(CyNode source, CyNode target, Type edgeType) {
    	ArrayList<CyEdge> commonCheck = new ArrayList<CyEdge>();
    	ArrayList<CyEdge> result = new ArrayList<CyEdge>();
    	if (containsNode(source) && containsNode(target)) {
    		Vertex s = graph.getVertex(nodeSUID2VertexIdMap.get(source.getSUID()));
    		Vertex t = graph.getVertex(nodeSUID2VertexIdMap.get(target.getSUID()));
			switch (edgeType) {
				case OUTGOING:
					for(Edge e: new IterateMe<Edge>(t.getInEdges(),s.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (ce.isDirected() && commonCheck.contains(ce)) {
							result.add(ce);
						}
		    			if (ce.isDirected()) {
							commonCheck.add(ce);
						}
		    		}
				break;
				case INCOMING:
					for(Edge e: new IterateMe<Edge>(s.getInEdges(),t.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (ce.isDirected() && commonCheck.contains(ce)) {
							result.add(ce);
						}
		    			if (ce.isDirected()) {
							commonCheck.add(ce);
						}
		    		}
				break;
				case DIRECTED:
					for(Edge e: new IterateMe<Edge>(s.getInEdges(),s.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (ce.isDirected()) {
							commonCheck.add(ce);
						}
		    		}
					for(Edge e: new IterateMe<Edge>(t.getInEdges(),t.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (ce.isDirected() && commonCheck.contains(ce)) {
							result.add(ce);
						}
		    		}
				break;
				case UNDIRECTED:
					for(Edge e: new IterateMe<Edge>(s.getInEdges(),s.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (!ce.isDirected()) {
							commonCheck.add(ce);
						}
		    		}
					for(Edge e: new IterateMe<Edge>(t.getInEdges(),t.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (!ce.isDirected() && commonCheck.contains(ce)) {
							result.add(ce);
						}
		    		}
				break;
				case ANY:
					for(Edge e: new IterateMe<Edge>(s.getInEdges(),s.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
						commonCheck.add(ce);
		    		}
					for(Edge e: new IterateMe<Edge>(t.getInEdges(),t.getOutEdges())){
		    			CyEdge ce = edgeMap.get(e);
		    			if (commonCheck.contains(ce)) {
							result.add(ce);
						}
		    		}
				break;
			}
    	}
    	return result;
    }

    @Override
    public CyTable getDefaultNetworkTable() {
	return this.netTableManager.get(CyNetwork.DEFAULT_ATTRS);
    }

    @Override
    public CyTable getDefaultNodeTable() {
    		return this.nodeTableManager.get(CyNetwork.DEFAULT_ATTRS);
    }

    @Override
    public CyTable getDefaultEdgeTable() {
	return this.edgeTableManager.get(CyNetwork.DEFAULT_ATTRS);
    }
    
}
