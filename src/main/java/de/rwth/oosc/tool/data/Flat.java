package de.rwth.oosc.tool.data;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphWalk;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.Geom;

import de.rwth.oosc.figures.structure.WallFigure;

public class Flat {

	private FlatGraph flatGraph;

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
		FlatGraph graph = new FlatGraph();
		lines.forEach((line1) -> {
			List<Point2D.Double> intersections = new ArrayList<>();

			lines.forEach((line2) -> {
				if (line1 != line2 && line1.intersectsLine(line2)) {
					intersections.add(Geom.intersect(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.y1,
							line2.x2, line2.y2));
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
				Point2D.Double p1 = new Point2D.Double((int) intersections.get(i).x, (int) intersections.get(i).y);
				Point2D.Double p2 = new Point2D.Double((int) intersections.get(i + 1).x,
						(int) intersections.get(i + 1).y);
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

	public Set<GraphPath<Point2D.Double, DefaultEdge>> getSelectedRooms(
			Set<GraphPath<Point2D.Double, DefaultEdge>> cyclePaths) {
		Map<GraphPath<Point2D.Double, DefaultEdge>, BezierFigure> roomMap = new HashMap<>();
		for (var path : cyclePaths) {
			BezierPath p = new BezierPath();
			BezierFigure f = new BezierFigure();
			f.set(AttributeKeys.STROKE_COLOR,
					new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));

			Iterator<Point2D.Double> it = path.getVertexList().iterator();
			Point2D.Double point = it.next();
			p.moveTo(point.x, point.y);
			while (it.hasNext()) {
				point = it.next();
				p.lineTo(point.x, point.y);
			}
			p.setClosed(true);

			f.setBezierPath(p);
			roomMap.put(path, f);
		}

		Set<GraphPath<Point2D.Double, DefaultEdge>> rooms = new HashSet<>();
		Set<GraphPath<Point2D.Double, DefaultEdge>> roomsToAdd = new HashSet<>(cyclePaths);

		if (cyclePaths.size() > 0) {
			if (cyclePaths.size() == 1) {
				return cyclePaths;
			}

			// remove all rooms contained in other rooms
			for (var path1 : cyclePaths) {
				for (var path2 : cyclePaths) {
					if (path1.equals(path2)) {
						continue;
					}
					if (containsAllPoints(roomMap.get(path1), roomMap.get(path2).getBezierPath())) {
						roomsToAdd.remove(path2);
					}
				}
			}

			// union all rooms
			List<GraphPath<Point2D.Double, DefaultEdge>> roomsToAddList = new ArrayList<>(roomsToAdd);
			Collections.sort(roomsToAddList, new Comparator<GraphPath<Point2D.Double, DefaultEdge>>() {

				@Override
				public int compare(GraphPath<Double, DefaultEdge> r1, GraphPath<Double, DefaultEdge> r2) {
					List<Point2D.Double> r1List = r1.getVertexList();
					List<Point2D.Double> r2List = r2.getVertexList();
					int res = 0;

					double minP1 = java.lang.Double.MAX_VALUE;
					double minP2 = java.lang.Double.MAX_VALUE;
					for (var p1 : r1List) {
						if (p1.x < minP1) {
							minP1 = p1.x;
						}
					}

					for (var p2 : r2List) {
						if (p2.x < minP2) {
							minP2 = p2.x;
						}
					}

					if (minP1 < minP2) {
						res = -1;
					} else if (minP2 < minP1) {
						res = 1;
					}

					return res;
				}
			});

			var it = roomsToAddList.iterator();
			var room1 = it.next();
			var room1Vertices = new ArrayList<>(room1.getVertexList());
			room1Vertices.remove(0);

			List<Point2D.Double> connectedVertices = new ArrayList<Point2D.Double>();

			while (it.hasNext()) {
				connectedVertices = new ArrayList<Point2D.Double>();
				var room2 = it.next();
				var room2Vertices = new ArrayList<>(room2.getVertexList());
				room2Vertices.remove(0);
				var commonVertices = new ArrayList<Point2D.Double>(room1Vertices);
				commonVertices.retainAll(room2Vertices);

				if (commonVertices.size() == 0) {
					rooms.add(room1);
					rooms.add(room2);
					room1Vertices = room2Vertices;
					continue;
				}

				var firstCommon = commonVertices.get(0);
				int firstCommonPos = room1Vertices.indexOf(firstCommon);

				var room1Rest = new ArrayList<Point2D.Double>();
				room1Rest.addAll(room1Vertices.subList(firstCommonPos, room1Vertices.size()));
				room1Rest.addAll(room1Vertices.subList(0, firstCommonPos));
				room1Rest.removeAll(commonVertices);
				Point2D.Double c1 = findConnector(room1Vertices, room1Rest);

				var firstCommonPos2 = room2Vertices.indexOf(firstCommon);
				var room2Rest = new ArrayList<Point2D.Double>();
				room2Rest.addAll(room2Vertices.subList(firstCommonPos2, room2Vertices.size()));
				room2Rest.addAll(room2Vertices.subList(0, firstCommonPos2));
				room2Rest.removeAll(commonVertices);
				Point2D.Double c2 = findConnector(room2Vertices, room2Rest);

				if (c1.equals(c2)) {
					Collections.reverse(room2Rest);
					c2 = findConnector(room2Vertices, room2Rest);
				}

				connectedVertices.add(c1);
				connectedVertices.addAll(room1Rest);
				connectedVertices.add(c2);
				connectedVertices.addAll(room2Rest);
				connectedVertices.add(c1);

				room1 = new GraphWalk<Point2D.Double, DefaultEdge>(flatGraph, connectedVertices, 0);
				room1Vertices = new ArrayList<>(room1.getVertexList());
				room1Vertices.remove(0);
			}
			rooms.add(room1);
		}

		return rooms;
	}

	public Point2D.Double findConnector(List<Point2D.Double> original, List<Point2D.Double> rest) {
		List<Point2D.Double> common = new ArrayList<>(original);
		common.removeAll(rest);

		List<Point2D.Double> list = new ArrayList<>(original);
		list.addAll(original);
		int i = list.subList(1, original.size() + 1).indexOf(rest.get(0));
		return common.contains(list.get(i)) ? list.get(i) : list.get(i + 2);
	}

	private static boolean containsAllPoints(BezierFigure f, BezierPath path) {
		boolean containsAll = true;

		if (path.size() == 0) {
			return false;
		}

		for (var node : path) {
			containsAll = containsAll && f.contains(node.getControlPoint(0));
		}

		return containsAll;
	}

	public double computePathArea(BezierPath path) {
		if (!path.isClosed()) {
			return 0;
		}

		double area = 0;
		int last = 0;
		for (int i = 1; i < path.size(); i++) {
			area += (Math.abs(path.get(last).x[0] + path.get(i).x[0])
					* Math.abs(path.get(last).y[0] - path.get(i).y[0])) / 2;
			last = i;
		}
		area += Math.abs(path.get(last).x[0] + path.get(0).x[0]) * Math.abs(path.get(last).y[0] - path.get(0).y[0]) / 2;

		return area;
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

		return cyclicComponents;
	}
}
