package de.rwth.oosc.figures.svg;

import java.awt.geom.Path2D;

public class SVGTriangleFigure extends AbstractCustomSVGFigure {

	private static final long serialVersionUID = 1L;

	public SVGTriangleFigure() {
		super(0,0,0,0);
	}

    @Override
    public SVGTriangleFigure clone() {
        SVGTriangleFigure that = (SVGTriangleFigure) super.clone();
        return that;
    }

	@Override
	protected Path2D.Double computePath(double x, double y, double width, double height) {
		Path2D.Double path = new Path2D.Double();
		path.moveTo(x + width / 2, y);
		path.lineTo(x + width, y + height);
		path.lineTo(x, y + height);
		path.closePath();
		return path;
	}

}
