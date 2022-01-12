package de.rwth.oosc.figures.svg;

import static de.rwth.oosc.figures.svg.SVGAttributeKeys.FILL_GRADIENT;
import static de.rwth.oosc.figures.svg.SVGAttributeKeys.STROKE_GRADIENT;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Collection;
import java.util.LinkedList;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.GrowStroke;

public abstract class AbstractCustomSVGFigure extends SVGAttributedFigure implements SVGFigure {

	private static final long serialVersionUID = 1L;
	
	protected Path2D.Double figurePath;
	
	private transient Shape cachedTransformedShape;
	
	private transient Shape cachedHitShape;

	protected abstract void computePath(double x, double y, double width, double height);
	
	public AbstractCustomSVGFigure() {
		this(0,0,0,0);
	}
	
	public AbstractCustomSVGFigure(double x, double y, double width, double height) {
		figurePath = new Path2D.Double();
		computePath(x, y, width, height);
		SVGAttributeKeys.setDefaults(this);
		setConnectable(false);
	}
	
	public Path2D.Double getFigurePath() {
		return figurePath;
	}
	
	@Override
	public void setBounds(java.awt.geom.Point2D.Double anchor, java.awt.geom.Point2D.Double lead) {
		double x = Math.min(anchor.x, lead.x);
		double y = Math.min(anchor.y, lead.y);
        double width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        double height = Math.max(0.1, Math.abs(lead.y - anchor.y));
        computePath(x, y, width, height);
        invalidate();
	}
	
	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		LinkedList<Handle> handles = (LinkedList<Handle>) super.createHandles(detailLevel);
		if(detailLevel == 0) {
			TransformHandleKit.addTransformHandles(this, handles);
		}
		return handles;
	}

	@Override
	public Double getBounds() {
		return (Double) figurePath.getBounds2D();
	}
	
	public double getWidth() {
		return getBounds().getWidth();
	}
	
	public double getHeight() {
		return getBounds().getHeight();
	}

	@Override
	protected void drawFill(Graphics2D g) {
		if (getWidth() > 0 && getHeight() > 0) {
			g.fill(figurePath);
		}
	}

	@Override
	protected void drawStroke(Graphics2D g) {
		if (getWidth() > 0 && getHeight() > 0) {
			g.draw(figurePath);
		}
	}
	
	@Override
    public Rectangle2D.Double getDrawingArea() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        if (get(TRANSFORM) == null) {
            double g = SVGAttributeKeys.getPerpendicularHitGrowth(this) * 2d + 1;
            Geom.grow(r, g, g);
        } else {
            double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this);
            double width = strokeTotalWidth / 2d;
            width *= Math.max(get(TRANSFORM).getScaleX(), get(TRANSFORM).getScaleY()) + 1;
            Geom.grow(r, width, width);
        }
        return r;
    }

    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    @Override
    public boolean contains(Point2D.Double p) {
        return getHitShape().contains(p);
    }

    private Shape getTransformedShape() {
        if (cachedTransformedShape == null) {
            if (get(TRANSFORM) == null) {
                cachedTransformedShape = figurePath;
            } else {
                cachedTransformedShape = get(TRANSFORM).createTransformedShape(figurePath);
            }
        }
        return cachedTransformedShape;
    }
    
    private Shape getHitShape() {
        if (cachedHitShape == null) {
            if (get(FILL_COLOR) != null || get(FILL_GRADIENT) != null) {
                cachedHitShape = new GrowStroke(
                        (float) SVGAttributeKeys.getStrokeTotalWidth(this) / 2f,
                        (float) SVGAttributeKeys.getStrokeTotalMiterLimit(this)).createStrokedShape(getTransformedShape());
            } else {   
                cachedHitShape = SVGAttributeKeys.getHitStroke(this).createStrokedShape(getTransformedShape());
            }
        }
        return cachedHitShape;
    }

    /**
     * Transforms the figure.
     *
     * @param tx the transformation.
     */
    @Override
    public void transform(AffineTransform tx) {
        if (get(TRANSFORM) != null ||
                (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType()) {
            if (get(TRANSFORM) == null) {
                TRANSFORM.setClone(this, tx);
            } else {
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                set(TRANSFORM,  t);
            }
        } else {
            Point2D.Double anchor = getStartPoint();
            Point2D.Double lead = getEndPoint();
            setBounds(
                    (Point2D.Double) tx.transform(anchor, anchor),
                    (Point2D.Double) tx.transform(lead, lead));
            if (get(FILL_GRADIENT) != null &&
                    !get(FILL_GRADIENT).isRelativeToFigureBounds()) {
                Gradient g = FILL_GRADIENT.getClone(this);
                g.transform(tx);
                set(FILL_GRADIENT,  g);
            }
            if (get(STROKE_GRADIENT) != null &&
                    !get(STROKE_GRADIENT).isRelativeToFigureBounds()) {
                Gradient g = STROKE_GRADIENT.getClone(this);
                g.transform(tx);
                set(STROKE_GRADIENT,  g);
            }
        }
        invalidate();
    }

    @Override
    public void restoreTransformTo(Object geometry) {
        Object[] restoreData = (Object[]) geometry;
        figurePath = (Path2D.Double) ((Path2D.Double) restoreData[0]).clone();
        TRANSFORM.setClone(this, (AffineTransform) restoreData[1]);
        FILL_GRADIENT.setClone(this, (Gradient) restoreData[2]);
        STROKE_GRADIENT.setClone(this, (Gradient) restoreData[3]);
        invalidate();
    }

    @Override
    public Object getTransformRestoreData() {
        return new Object[]{
                    figurePath.clone(),
                    TRANSFORM.getClone(this),
                    FILL_GRADIENT.getClone(this),
                    STROKE_GRADIENT.getClone(this),};
    }


    // CONNECTING
    // COMPOSITE FIGURES
    // CLONING

    @Override
    public AbstractCustomSVGFigure clone() {
        AbstractCustomSVGFigure that = (AbstractCustomSVGFigure) super.clone();
        that.figurePath = (Path2D.Double) this.figurePath.clone();
        that.cachedTransformedShape = null;
        return that;
    }

    // EVENT HANDLING
    @Override
    public boolean isEmpty() {
        Rectangle2D.Double b = getBounds();
        return b.width <= 0 || b.height <= 0;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cachedTransformedShape = null;
        cachedHitShape = null;
    }

}
