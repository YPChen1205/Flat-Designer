/*
 * @(#)Gradient.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package de.chen.flatdesigner.figures.svg;

import java.awt.*;
import java.awt.geom.AffineTransform;
import org.jhotdraw.draw.*;

/**
 * Represents an SVG Gradient.
 *
 * @author Werner Randelshofer
 * @version $Id: Gradient.java 785 2013-12-01 19:16:30Z rawcoder $
 */
public interface Gradient extends Cloneable {
    public Paint getPaint(Figure f, double opacity);
    public boolean isRelativeToFigureBounds();
    public void transform(AffineTransform tx);
    public Object clone();
    public void makeRelativeToFigureBounds(Figure f);
}
