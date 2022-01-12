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
	protected void computePath(double x, double y, double width, double height) {
		figurePath = new Path2D.Double();
		figurePath.moveTo(x + width / 2, y);
		figurePath.lineTo(x + width, y + height);
		figurePath.lineTo(x, y + height);
		figurePath.closePath();
	}

}
