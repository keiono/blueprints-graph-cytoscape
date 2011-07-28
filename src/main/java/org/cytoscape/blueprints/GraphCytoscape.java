package org.cytoscape.blueprints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.SUIDFactory;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.oupls.GraphSource;

public class GraphCytoscape implements GraphSource, CyNetwork {

    private final Graph graph;
    private final long suid;
    
    private int nodeID;
    private int edgeID;
    
    private final CyEventHelper eventHelper;
    
    //Node and Edge Maps
    private Map<Integer, CyNode> nodeIndexMap;
    private Map<Integer, CyEdge> edgeIndexMap;
    private Map<Vertex, CyNode> nodeMap;
    private Map<Edge, CyEdge> edgeMap;

    //Table Maps
    private final Map<String, CyTable> netTableManager;
    private final Map<String, CyTable> nodeTableManager;
    private final Map<String, CyTable> edgeTableManager;

    //Constructor
    public GraphCytoscape(final Graph graph) {
		this.graph = graph;
		initGraph();
	
		this.suid = SUIDFactory.getNextSUID();
		
		this.nodeID = 0;
		this.edgeID = 0;
		
		this.nodeIndexMap = new HashMap<Integer, CyNode>();
		this.edgeIndexMap = new HashMap<Integer, CyEdge>();
		this.nodeMap = new HashMap<Vertex, CyNode>();
		this.edgeMap = new HashMap<Edge, CyEdge>();
	
		netTableManager = new HashMap<String, CyTable>();
		nodeTableManager = new HashMap<String, CyTable>();
		edgeTableManager = new HashMap<String, CyTable>();
	
		initDefaultTables();
		
		this.eventHelper = new DummyCyEventHelper();
    }

    private void initDefaultTables() {
    	//Set Default Network Table and Columns
    	ElementCyTable netTable = new ElementCyTable(SUIDFactory.getNextSUID(), CyNetwork.DEFAULT_ATTRS, eventHelper);
    	netTable.addRow(new ElementCyRow(netTable, new DummyElement(this.getSUID())));
    	netTable.createColumn("name", String.class, true);
    	netTableManager.put(netTable.getTitle(), netTable);
    	
    	//Set Default Node Table and Columns
    	ElementCyTable nodeTable = new ElementCyTable(SUIDFactory.getNextSUID(),CyNetwork.DEFAULT_ATTRS, eventHelper);
    	nodeTable.createColumn("name", String.class, false);
    	nodeTable.createColumn("selected", Boolean.class, false);
    	nodeTableManager.put(nodeTable.getTitle(), nodeTable);
    	
    	//Set Default Edge Table and Columns
    	ElementCyTable edgeTable = new ElementCyTable(SUIDFactory.getNextSUID(),CyNetwork.DEFAULT_ATTRS, eventHelper);
    	edgeTable.createColumn("name", String.class, false);
    	edgeTable.createColumn("selected", Boolean.class, false);
    	edgeTable.createColumn("interaction", String.class, false);
    	edgeTableManager.put(edgeTable.getTitle(), edgeTable);
    }
    
    private void initGraph() {
    
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

    //Adds a Node
    public CyNode addNode() {
    	final Vertex vertex = graph.addVertex(SUIDFactory.getNextSUID());
		VertexCytoscape vc = new VertexCytoscape(vertex, nodeID, getDefaultNodeTable());
		nodeMap.put(vertex,vc);
		nodeIndexMap.put(nodeID++,vc);
		return vc;
    }

    //Removes Nodes  !!DELETES EDGES FROM EDGEMAP EFFECTED!! -- not necessarily tested in tests
    public boolean removeNodes(Collection<CyNode> nodes) {
    	for(CyNode node:nodes) {
	    	if (containsNode(node)) {
	    		Vertex vr = graph.getVertex(node.getSUID());
	    		for (CyEdge e: getAdjacentEdgeList(nodeMap.get(vr), CyEdge.Type.ANY)) {
	    			Edge er = graph.getEdge(e.getSUID());
	    			edgeIndexMap.remove(e.getIndex());
			    	edgeMap.remove(er);
	    		}
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
    		Vertex s = graph.getVertex(source.getSUID());
    		Vertex t = graph.getVertex(target.getSUID());
    		final Edge edge = graph.addEdge(SUIDFactory.getNextSUID(),s, t, "");
    		EdgeCytoscape ec = new EdgeCytoscape(edge, edgeID, isDirected, source, target, getDefaultEdgeTable());
    		edgeMap.put(edge,ec);
    		edgeIndexMap.put(edgeID++,ec);
    		return ec;
    	}
		throw new RuntimeException();
    }
    
    //Remove Edges
    public boolean removeEdges(Collection<CyEdge> edges) {
	    if (edges == null) { return false; }
    	for(CyEdge edge:edges) {
	    	if (edge != null && containsEdge(edge)) {
	    		Edge er = graph.getEdge(edge.getSUID());
		    	graph.removeEdge(er);
		    	edgeIndexMap.remove(edge.getIndex());
		    	edgeMap.remove(er);
	    	} else {
	    		return false;
	    	}
    	}
    	return true;
    }
    
    //Returns Current Node Count
    public int getNodeCount() {
    	return nodeIndexMap.size();
    }

    //Returns Current Edge Count
    public int getEdgeCount() {
    	return edgeIndexMap.size();
    }

    //Return List of Nodes
    public List<CyNode> getNodeList() {
		return new ArrayList<CyNode>(nodeMap.values());
    }
    
    //Return List of Edges
    public List<CyEdge> getEdgeList() {
		return new ArrayList<CyEdge>(edgeMap.values());
    }
    
    //Checks if the Specified Node Exists
    public boolean containsNode(CyNode node) {
    	return (graph.getVertex(node.getSUID()) != null);
    }

    //Checks if the Specified Edge Exists
    public boolean containsEdge(CyEdge edge) {
    	return (graph.getEdge(edge.getSUID()) != null);
    }
    
    //Checks if the Nodes Are Connected by an Edge
    public boolean containsEdge(CyNode from, CyNode to) {
    	ArrayList<CyEdge> commonCheck = new ArrayList<CyEdge>();
    	if (containsNode(from) && containsNode(to) && from != to) {
    		Vertex f = graph.getVertex(from.getSUID());
    		Vertex t = graph.getVertex(to.getSUID());
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
	    	Vertex vertex = graph.getVertex(node.getSUID());
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
	    	Vertex vertex = graph.getVertex(node.getSUID());
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
    		Vertex s = graph.getVertex(source.getSUID());
    		Vertex t = graph.getVertex(target.getSUID());
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
    
    //Provides a Method for Combining Iterables
    private <T> ArrayList<T> combineIt(Iterable<T>... t){
    	ArrayList<T> list = new ArrayList<T>();
    	for (Iterable<T> it : t) {
    		for (T l: it) {
    			list.add(l);
    		}
    	}
    	return list;
    }
}
