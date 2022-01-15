package de.rwth.oosc.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.tool.AbstractTool;
import org.jhotdraw.draw.tool.DefaultSelectAreaTracker;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.geom.BezierPath;

import de.rwth.oosc.figures.structure.WallFigure;

public class RoomSelectionTool extends DefaultSelectAreaTracker{
	
	private static final long serialVersionUID = 1L;

    /**
     * The bounds of the rubberband. 
     */
    private Rectangle rubberband = new Rectangle();
    /**
     * Rubberband color. When this is null, the tracker does not
     * draw the rubberband.
     */
    private Color rubberbandColor = Color.BLACK;
    /**
     * Rubberband stroke.
     */
    private Stroke rubberbandStroke = new BasicStroke();
    /**
     * The hover handles, are the handles of the figure over which the
     * mouse pointer is currently hovering.
     */
    private LinkedList<Handle> hoverHandles = new LinkedList<Handle>();
    /**
     * The hover Figure is the figure, over which the mouse is currently
     * hovering.
     */
    @Nullable private Figure hoverFigure = null;

    /** Creates a new instance. */
    public RoomSelectionTool() {
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        clearRubberBand();
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        selectGroup(evt.isShiftDown());
        clearRubberBand();

    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        Rectangle invalidatedArea = (Rectangle) rubberband.clone();
        rubberband.setBounds(
                Math.min(anchor.x, evt.getX()),
                Math.min(anchor.y, evt.getY()),
                Math.abs(anchor.x - evt.getX()),
                Math.abs(anchor.y - evt.getY()));
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
        
        for (Point2D.Double p : selectedPoints) {
        	g.setColor(Color.RED);
        	g.fillOval((int)p.getX()-5, (int)p.getY()-5, 10, 10);
        }
        
        for (var entry : componentColorMap.entrySet()) {
        	var graph = entry.getKey();
        	g.setColor(entry.getValue());
        	for (Point2D.Double p : graph.vertexSet()) {
        		g.fillOval((int)(p.x)-5, (int)(p.y)-5, 10, 10);
        	}
        }
        
        for (var entry : outmostPointsMap.entrySet()) {
        	g.setColor(entry.getValue());
        	for (var p : entry.getKey()) {
        		g.drawRect((int) p.x, (int) p.y, 15, 15);
        	}
        }
        
        for (var entry : cyclePoints.entrySet()) {
        	g.setColor(entry.getValue());
        	for (var p : entry.getKey()) {
        		g.drawRect((int) p.x, (int) p.y, entry.getValue().getRed(), entry.getValue().getRed());
        	}
        }
    }
    
    private Collection<Point2D.Double> selectedPoints = new HashSet<>();
    private Collection<BezierPath> selectedRoom = new HashSet<>();
    private Map<Graph<Point2D.Double, DefaultEdge>, Color> componentColorMap = new HashMap<>();    
    private Map<Set<Point2D.Double>, Color> outmostPointsMap = new HashMap<>();
    private Map<Set<Point2D.Double>, Color> cyclePoints = new HashMap<>();
    
    private void selectGroup(boolean toggle) {
    	List<Figure> figures = getView().getDrawing().getChildren();
    	Set<WallFigure> walls = new HashSet<>();
    	
    	for (Figure f : figures) {
    		if (f instanceof WallFigure) {
    			walls.add((WallFigure)f);
    		}
    	}
    	selectedPoints = new HashSet<>();
    	Flat flat = new Flat(walls);
    	Set<Point2D.Double> intersections = flat.getIntersections(rubberband);
    	
    	componentColorMap.clear();
    	outmostPointsMap.clear();
    	
    	Graph<Point2D.Double, DefaultEdge> g = new AsSubgraph<>(flat.getFlatGraph(), intersections);
//		selectedPoints.addAll(g.vertexSet());
		// TODO: get components for SELECTED rooms
    	var components = flat.getAllRooms();
		for (var wholeComponent : components) {
			
			var component = new AsSubgraph<>(wholeComponent, intersections);
			PatonCycleBase<Point2D.Double, DefaultEdge> cycleBaseDetector = new PatonCycleBase<Point2D.Double, DefaultEdge>(component);
			CycleBasis<Point2D.Double, DefaultEdge> cycleBasis = cycleBaseDetector.getCycleBasis();
			for (var cyclePath : cycleBasis.getCyclesAsGraphPaths()) {
				Color cycleColor = new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
				cyclePoints.put(new HashSet(cyclePath.getVertexList()), cycleColor);
			}
			// find outermost points
			Set<Point2D.Double> outermostPoints = new HashSet<>();
			Optional<Point2D.Double> minPX = component.vertexSet().stream().min((v1, v2) -> {
				return v1.x <= v2.x ? -1 : 1;
			});
			if (minPX.isPresent()) {
				outermostPoints.add(minPX.get());
			}
			Optional<Point2D.Double> minPY = component.vertexSet().stream().min((v1, v2) -> {
				return v1.y <= v2.y ? -1 : 1;
			});
			if (minPY.isPresent()) {
				outermostPoints.add(minPY.get());
			}
			Optional<Point2D.Double> maxPX = component.vertexSet().stream().max((v1, v2) -> {
				return v1.x >= v2.x ? 1 : -1;
			});
			if (maxPX.isPresent()) {
				outermostPoints.add(maxPX.get());
			}
			Optional<Point2D.Double> maxPY = component.vertexSet().stream().max((v1, v2) -> {
				return v1.y >= v2.y ? 1 : -1;
			});
			if (maxPY.isPresent()) {
				outermostPoints.add(maxPY.get());
			}
			
			
			outmostPointsMap.put(outermostPoints, new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
			componentColorMap.put(component, new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
		}
//    	MutableGraph<Point2D.Double> selectedRoomGraph = gCopied;
//    	
//    	for (EndpointPair<Point2D.Double> e : selectedRoomGraph.edges()) {
//    		BezierPath p = new BezierPath();
//    		p.moveTo(e.nodeV().x, e.nodeV().y);
//    		p.lineTo(e.nodeU().x, e.nodeU().y);
//    	}
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
    }

}
