package de.chen.flatdesigner.tool.data;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jhotdraw.geom.BezierPath;

import de.chen.flatdesigner.figures.FurnitureFigure;

public class Room {

	private BezierPath roomPath;
	private Set<FurnitureFigure> furnitures;
	
	private transient Optional<Double> area;
	private transient Optional<Double> occupiedArea;
	
	public Room(GraphPath<Point2D.Double, DefaultEdge> roomPath) {
		this(roomPath, new HashSet<>());
	}
	
	public Room(GraphPath<Point2D.Double, DefaultEdge> roomPath, Set<FurnitureFigure> furnitures) {
		this.roomPath = computePath(roomPath);
		this.furnitures = new HashSet<>(furnitures);
		area = Optional.empty();
		occupiedArea = Optional.empty();
	}
	
	private static BezierPath computePath(GraphPath<Point2D.Double, DefaultEdge> roomGraphPath) {
		List<Point2D.Double> vertexList = roomGraphPath.getVertexList();
		BezierPath roomPath = new BezierPath();
		if (vertexList.size() > 0) {
			Point2D.Double p = roomGraphPath.getStartVertex();
			roomPath.moveTo(p.x, p.y);
			for (int i = 1; i < vertexList.size(); i++) {
				p = vertexList.get(i);
				roomPath.lineTo(p.x, p.y);
			}
			roomPath.setClosed(true);
		}
		return roomPath;
	}
	
	public BezierPath getRoomPath() {
		return roomPath.clone();
	}
	
	protected void setRoomPath(BezierPath roomPath) {
		this.roomPath = roomPath;
	}
	
	public double getArea() {
		if (area.isPresent()) {
			return area.get();
		}
		
		if (!roomPath.isClosed()) {
			return 0;
		}

		double area = 0;
		int last = 0;
		for (int i = 1; i < roomPath.size(); i++) {
			area += (Math.abs(roomPath.get(last).x[0] + roomPath.get(i).x[0])
					* Math.abs(roomPath.get(last).y[0] - roomPath.get(i).y[0])) / 2;
			last = i;
		}
		area += Math.abs(roomPath.get(last).x[0] + roomPath.get(0).x[0]) * Math.abs(roomPath.get(last).y[0] - roomPath.get(0).y[0]) / 2;
		this.area = Optional.of(area);
		return area;
	}
	
	public double getOccupiedArea() {
		if (occupiedArea.isPresent()) {
			return occupiedArea.get();
		}
		
		double area = 0;
		
		for (var furniture : furnitures) {
			area += furniture.getBounds().width * furniture.getBounds().height;
		}
		this.occupiedArea = Optional.of(area);
		return area;
	}
	
	public double getFreeArea() {
		return getArea() - getOccupiedArea();
	}
	
	public boolean add(FurnitureFigure furniture) {
		boolean success = false;
		
		if (getRoomPath().contains(furniture.getBounds())) {
			furnitures.add(furniture);
			success = true;
		}
		
		return success;
	}
}
