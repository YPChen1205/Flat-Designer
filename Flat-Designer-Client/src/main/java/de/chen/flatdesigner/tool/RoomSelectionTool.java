package de.chen.flatdesigner.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.tool.DefaultSelectAreaTracker;
import org.jhotdraw.geom.BezierPath;

import de.chen.flatdesigner.figures.FurnitureFigure;
import de.chen.flatdesigner.figures.structure.WallFigure;
import de.chen.flatdesigner.tool.data.Flat;
import de.chen.flatdesigner.tool.data.Room;

public class RoomSelectionTool extends DefaultSelectAreaTracker {

	private static final long serialVersionUID = 1L;

	/**
	 * The bounds of the rubberband.
	 */
	private Rectangle rubberband = new Rectangle();
	/**
	 * Rubberband color. When this is null, the tracker does not draw the
	 * rubberband.
	 */
	private Color rubberbandColor = Color.BLACK;
	/**
	 * Rubberband stroke.
	 */
	private Stroke rubberbandStroke = new BasicStroke();
	/**
	 * The hover handles, are the handles of the figure over which the mouse pointer
	 * is currently hovering.
	 */
	private LinkedList<Handle> hoverHandles = new LinkedList<Handle>();
	/**
	 * The hover Figure is the figure, over which the mouse is currently hovering.
	 */
	@Nullable
	private Figure hoverFigure = null;

	/** Creates a new instance. */
	public RoomSelectionTool() {
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		super.mousePressed(evt);
		clearRubberBand();
		clearRoomSelection();
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		selectGroup(evt.isShiftDown());
		clearRubberBand();

	}

	@Override
	public void mouseDragged(MouseEvent evt) {
		Rectangle invalidatedArea = (Rectangle) rubberband.clone();
		rubberband.setBounds(Math.min(anchor.x, evt.getX()), Math.min(anchor.y, evt.getY()),
				Math.abs(anchor.x - evt.getX()), Math.abs(anchor.y - evt.getY()));
		if (invalidatedArea.isEmpty()) {
			invalidatedArea = (Rectangle) rubberband.clone();
		} else {
			invalidatedArea = invalidatedArea.union(rubberband);
		}
		fireAreaInvalidated(invalidatedArea);
	}

	@Override
	public void mouseMoved(MouseEvent evt) {
		clearRubberBand();
		Point point = evt.getPoint();
		DrawingView view = editor.findView((Container) evt.getSource());
		updateCursor(view, point);
		if (view == null || editor.getActiveView() != view) {
			clearHoverHandles();
		} else {
			// Search first, if one of the selected figures contains
			// the current mouse location, and is selectable.
			// Only then search for other
			// figures. This search sequence is consistent with the
			// search sequence of the SelectionTool.
			Figure figure = null;
			Point2D.Double p = view.viewToDrawing(point);
			for (Figure f : view.getSelectedFigures()) {
				if (f.contains(p)) {
					figure = f;
				}
			}
			if (figure == null) {
				figure = view.findFigure(point);
				while (figure != null && !figure.isSelectable()) {
					figure = view.getDrawing().findFigureBehind(p, figure);
				}
			}

			updateHoverHandles(view, figure);
		}
	}

	@Override
	public void mouseExited(MouseEvent evt) {
		DrawingView view = editor.findView((Container) evt.getSource());
		updateHoverHandles(view, null);
	}

	private void clearRubberBand() {
		if (!rubberband.isEmpty()) {
			fireAreaInvalidated(rubberband);
			rubberband.width = -1;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setStroke(rubberbandStroke);
		g.setColor(rubberbandColor);
		g.drawRect(rubberband.x, rubberband.y, rubberband.width - 1, rubberband.height - 1);
		if (hoverHandles.size() > 0 && !getView().isFigureSelected(hoverFigure)) {
			for (Handle h : hoverHandles) {
				h.draw(g);
			}
		}

		for (var room : rooms) {
			BezierPath roomPath = room.getRoomPath();
			
			g.setColor(new Color(0.2f, 0.6f, 0.7f, 0.3f));
			g.fill(roomPath);

			for (var pathNode : roomPath) {
				g.setColor(Color.orange);
				g.fillOval((int) pathNode.getControlPoint(0).x - 5, (int) pathNode.getControlPoint(0).y - 5, 10, 10);
			}

			double area = room.getArea();
			g.setColor(Color.BLACK);
			int y = (int) roomPath.getCenter().y;
			g.drawString("Total area: " + area, (int) roomPath.getCenter().x, y);
			y += 25;
			g.drawString("Occupied area: " + room.getOccupiedArea(), (int) roomPath.getCenter().x, y);
			y += 25;
			g.drawString("Free area: " + room.getFreeArea(), (int) roomPath.getCenter().x, y);
		}

	} 

	private Set<Room> rooms = new HashSet<>();
	private Flat flat = null;

	private void selectGroup(boolean toggle) {
		List<Figure> figures = getView().getDrawing().getChildren();
		Set<WallFigure> walls = new HashSet<>();
		
		for (Figure f : figures) {
			if (f instanceof WallFigure) {
				walls.add((WallFigure) f);
			}
		}
		flat = new Flat(walls);
		Set<Point2D.Double> intersections = flat.getIntersections(rubberband);

		var components = flat.getAllRooms();
		for (var wholeComponent : components) {

			var component = new AsSubgraph<>(wholeComponent, intersections);

			PatonCycleBase<Point2D.Double, DefaultEdge> cycleBaseDetector = new PatonCycleBase<Point2D.Double, DefaultEdge>(
					component);
			CycleBasis<Point2D.Double, DefaultEdge> cycleBasis = cycleBaseDetector.getCycleBasis();

			rooms = flat.getSelectedRooms(cycleBasis.getCyclesAsGraphPaths());
		}
		
		for (Room room : rooms) {
			for (Figure f : figures) {
				if (f instanceof FurnitureFigure) {
					room.add((FurnitureFigure) f);
				}
			}
		}
	}

	protected void clearHoverHandles() {
		updateHoverHandles(null, null);
	}

	protected void updateHoverHandles(@Nullable DrawingView view, @Nullable Figure f) {
		if (f != hoverFigure) {
			Rectangle r = null;
			if (hoverFigure != null) {
				for (Handle h : hoverHandles) {
					if (r == null) {
						r = h.getDrawingArea();
					} else {
						r.add(h.getDrawingArea());
					}
					h.setView(null);
					h.dispose();
				}
				hoverHandles.clear();
			}
			hoverFigure = f;
			if (hoverFigure != null && f.isSelectable()) {
				hoverHandles.addAll(hoverFigure.createHandles(-1));
				for (Handle h : hoverHandles) {
					h.setView(view);
					if (r == null) {
						r = h.getDrawingArea();
					} else {
						r.add(h.getDrawingArea());
					}
				}
			}
			if (r != null) {
				r.grow(1, 1);
				fireAreaInvalidated(r);
			}
		}
	}

	@Override
	public void activate(DrawingEditor editor) {
		super.activate(editor);
		clearHoverHandles();
	}

	@Override
	public void deactivate(DrawingEditor editor) {
		super.deactivate(editor);
		
		clearHoverHandles();
		clearRoomSelection();
	}

	private void clearRoomSelection() {
		flat = null;
		rooms.clear();
		fireAreaInvalidated(getView().getDrawing().getBounds());
	}

}
