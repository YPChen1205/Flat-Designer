package de.rwth.oosc.tool.data;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class FlatGraph extends SimpleGraph<Point2D.Double, DefaultEdge> {

	private static final long serialVersionUID = 1L;

	public FlatGraph() {
		super(DefaultEdge.class);
	}
	
	public Set<Point2D.Double> getNeighborsOf(Point2D.Double vertex) {
		Set<Point2D.Double> neighbors = new HashSet<>();
		Set<DefaultEdge> edges = edgesOf(vertex);
		
		for (DefaultEdge edge : edges) {
			Point2D.Double source = getEdgeSource(edge);
			if (source.equals(vertex)) {
				neighbors.add(getEdgeTarget(edge));
			} else {
				neighbors.add(source);
			}
		}
		
		return neighbors;
	}

}
