package de.rwth.oosc.tool;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jhotdraw.geom.Geom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.oosc.figures.structure.WallFigure;

public class Flat {

	private static final Logger logger = LoggerFactory.getLogger(Flat.class);
	
	private Graph<Point2D.Double, DefaultEdge> flatGraph;
	
	public Flat(Collection<WallFigure> walls) {
		computeFlatGraph(walls);
	}
	
	public Set<Point2D.Double> getIntersections(Rectangle rect) {
		Set<Point2D.Double> containedPoints = new HashSet<>();
		
		for (Point2D.Double p : flatGraph.vertexSet()) {
			if (rect.contains(p)) {
				containedPoints.add(p);
			}
		}
		
		return containedPoints;
	}
	
	private void computeFlatGraph(Collection<WallFigure> walls) {
		
		Set<Line2D.Double> lines = new HashSet<>();
		for (WallFigure wall : walls) {
			lines.addAll(wall.getLines());
		}
		
		// calculate intersection points
		
		SimpleGraph<Point2D.Double, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		lines.forEach((line1) -> {
			List<Point2D.Double> intersections = new ArrayList<>();
		
			lines.forEach((line2) -> {
				if (line1 != line2 && line1.intersectsLine(line2)) {
					intersections.add(Geom.intersect(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.y1, line2.x2, line2.y2));
				}
			});
			
			intersections.sort(new Comparator<Point2D.Double>() {

				@Override
				public int compare(Point2D.Double p1, Point2D.Double p2) {
					if (p1.x == p2.x) {
						return (p1.y >= p2.y) ? 1 : -1;
					}
					
					return (p1.x > p2.x) ? 1 : -1;
				}
			});
			
			for (int i = 0; i < intersections.size() - 1; i++) {
				Point2D.Double p1 = new Point2D.Double((int)intersections.get(i).x, (int)intersections.get(i).y);
				Point2D.Double p2 = new Point2D.Double((int)intersections.get(i+1).x, (int)intersections.get(i+1).y);
				if (!graph.containsVertex(p1)) {
					graph.addVertex(p1);
				}
				if (!graph.containsVertex(p2)) {
					graph.addVertex(p2);
				}
				graph.addEdge(p1, p2);
			}
		});
		flatGraph = graph;
	}
	
	public boolean contains(Point2D.Double p) {
		return false;
	}
	
	public Graph<Point2D.Double, DefaultEdge> getFlatGraph() {
		return flatGraph;
	}
	
	public Set<Graph<Point2D.Double, DefaultEdge>> getAllRooms() {
		Set<Graph<Point2D.Double, DefaultEdge>> rooms = new HashSet<>();
		
		if (flatGraph == null) {
			return rooms;
		}
		
		// compute components of the graph
		BiconnectivityInspector<Point2D.Double, DefaultEdge> inspector = new BiconnectivityInspector<>(flatGraph);
		Set<Graph<Point2D.Double, DefaultEdge>> components = inspector.getConnectedComponents();
		
		// remove all components which have no cycles
		Set<Graph<Point2D.Double, DefaultEdge>> cyclicComponents = new HashSet<>();
		for (Graph<Point2D.Double, DefaultEdge> c : components) {
			PatonCycleBase<Point2D.Double, DefaultEdge> cycleBase = new PatonCycleBase<>(flatGraph);
			CycleBasis<Point2D.Double, DefaultEdge> cb = cycleBase.getCycleBasis();
			
			if (cb.getLength() > 0) {
				List<Point2D.Double> acyclicVertices = c.vertexSet().stream().filter((vertex) -> {
					Set<GraphPath<Point2D.Double, DefaultEdge>> paths = cb.getCyclesAsGraphPaths();
					for (var path : paths) {
						if (path.getVertexList().contains(vertex)) {
							return false;
						}
					}
					
					return true;
				}).toList();
				// remove vertices which are not part of a cycle
				c.removeAllVertices(acyclicVertices);
				cyclicComponents.add(c);
			}
		}
		
		// every component contains a room
		// now we find the largest possible room with
		// computing the convex hull on every component graph
		/*for (Graph<Point2D.Double, DefaultEdge> c : cyclicComponents) {
			// compute the leftmost point
			Point2D.Double[] vertexArray = c.vertexSet().toArray(new Point2D.Double[0]);
			int leftMostIndex = 0;
			for (int i = 1; i < vertexArray.length; i++) {
				if (vertexArray[i].x < vertexArray[leftMostIndex].x) {
					leftMostIndex = i;
				}
			}
			
			Vector<Point2D.Double> hull = new Vector<>();
			int p = leftMostIndex, q;
			do {
				hull.add(vertexArray[p]);
				
				
			} while(p != leftMostIndex);
		}*/
		
		return cyclicComponents;
	}
}
