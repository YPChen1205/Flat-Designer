package de.rwth.oosc.figures;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Double;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map;

import javax.swing.Action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Dimension2DDouble;

@Deprecated
public class FigureProxy implements Figure {
	private static final long serialVersionUID = 1L;
	protected Figure figure;
	
	public FigureProxy(Figure figure) {
		this.figure = figure;
	}

	@Override
	public void draw(Graphics2D g) {
		figure.draw(g);
	}

	@Override
	public Double getBounds() {
		return figure.getBounds();
	}

	@Override
	public Double getDrawingArea() {
		return figure.getDrawingArea();
	}

	@Override
	public boolean contains(java.awt.geom.Point2D.Double p) {
		return figure.contains(p);
	}

	@Override
	public Object getTransformRestoreData() {
		return figure.getTransformRestoreData();
	}

	@Override
	public void restoreTransformTo(Object restoreData) {
		figure.restoreTransformTo(restoreData);
	}

	@Override
	public void transform(AffineTransform tx) {
		willChange();
		figure.transform(tx);
		figure.set(AttributeKeys.TRANSFORM, (AffineTransform) tx.clone());
		changed();
	}

	@Override
	public <T> void set(AttributeKey<T> key, @Nullable T value) {
		figure.set(key, value);
	}

	@Override
	public <T> @Nullable T get(AttributeKey<T> key) {
		return figure.get(key);
	}

	@Override
	public Map<AttributeKey<?>, Object> getAttributes() {
		return figure.getAttributes();
	}

	@Override
	public Object getAttributesRestoreData() {
		return figure.getAttributesRestoreData();
	}

	@Override
	public void restoreAttributesTo(Object restoreData) {
		figure.restoreAttributesTo(restoreData);
	}
	
	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		return figure.createHandles(detailLevel);
	}

	@Override
	public int getLayer() {
		return figure.getLayer();
	}

	@Override
	public boolean isVisible() {
		return figure.isVisible();
	}

	@Override
	public void setBounds(java.awt.geom.Point2D.Double start, java.awt.geom.Point2D.Double end) {
		figure.setBounds(start, end);
	}

	@Override
	public java.awt.geom.Point2D.Double getStartPoint() {
		return figure.getStartPoint();
	}

	@Override
	public java.awt.geom.Point2D.Double getEndPoint() {
		return figure.getEndPoint();
	}

	@Override
	public Dimension2DDouble getPreferredSize() {
		return figure.getPreferredSize();
	}

	@Override
	public boolean isSelectable() {
		return figure.isSelectable();
	}

	@Override
	public boolean isRemovable() {
		return figure.isRemovable();
	}

	@Override
	public boolean isTransformable() {
		return figure.isTransformable();
	}

	@Override
	public Cursor getCursor(java.awt.geom.Point2D.Double p) {
		return figure.getCursor(p);
	}

	@Override
	public Collection<Action> getActions(java.awt.geom.Point2D.Double p) {
		return figure.getActions(p);
	}

	@Override
	public @Nullable Tool getTool(java.awt.geom.Point2D.Double p) {
		return figure.getTool(p);
	}

	@Override
	public @Nullable String getToolTipText(java.awt.geom.Point2D.Double p) {
		return figure.getToolTipText(p);
	}

	@Override
	public boolean isConnectable() {
		return figure.isConnectable();
	}

	@Override
	public @Nullable Connector findConnector(java.awt.geom.Point2D.Double p, @Nullable ConnectionFigure prototype) {
		return figure.findConnector(p, prototype);
	}

	@Override
	public @Nullable Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
		return figure.findCompatibleConnector(c, isStartConnector);
	}

	@Override
	public Collection<Connector> getConnectors(@Nullable ConnectionFigure prototype) {
		return figure.getConnectors(prototype);
	}

	@Override
	public boolean includes(Figure figure) {
		return figure.includes(figure);
	}

	@Override
	public @Nullable Figure findFigureInside(java.awt.geom.Point2D.Double p) {
		return figure.findFigureInside(p);
	}

	@Override
	public Collection<Figure> getDecomposition() {
		return figure.getDecomposition();
	}

	@Override
	public Figure clone() {
		return new FigureProxy(figure.clone());
	}

	@Override
	public void remap(Map<Figure, Figure> oldToNew, boolean disconnectIfNotInMap) {
		figure.remap(oldToNew, disconnectIfNotInMap);
	}

	@Override
	public void addNotify(Drawing d) {
		figure.addNotify(d);
	}

	@Override
	public void removeNotify(Drawing d) {
		figure.removeNotify(d);
	}

	@Override
	public void willChange() {
		figure.willChange();
	}

	@Override
	public void changed() {
		figure.changed();
	}

	@Override
	public void requestRemove() {
		figure.requestRemove();
	}

	@Override
	public boolean handleDrop(java.awt.geom.Point2D.Double p, Collection<Figure> droppedFigures, DrawingView view) {
		return figure.handleDrop(p, droppedFigures, view);
	}

	@Override
	public boolean handleMouseClick(java.awt.geom.Point2D.Double p, MouseEvent evt, DrawingView view) {
		return figure.handleMouseClick(p, evt, view);
	}

	@Override
	public void addFigureListener(FigureListener l) {
		figure.addFigureListener(l);
	}

	@Override
	public void removeFigureListener(FigureListener l) {
		figure.removeFigureListener(l);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		figure.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		figure.removePropertyChangeListener(listener);
	}

}
