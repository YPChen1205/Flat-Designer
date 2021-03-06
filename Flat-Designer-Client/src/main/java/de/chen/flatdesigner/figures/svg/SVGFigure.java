/*
 * @(#)SVGFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package de.chen.flatdesigner.figures.svg;

import org.jhotdraw.draw.*;

/**
 * SVGFigure.
 *
 * @author Werner Randelshofer
 * @version $Id: SVGFigure.java 785 2013-12-01 19:16:30Z rawcoder $
 */
public interface SVGFigure extends Figure {
    /**
     * Returns true, if this figure is empty for one of the following
     * reasons:
     * <ul>
     * <li>A group has no children</li>
     * <li>A path has less than two points</li>
     * <li>An ellipse or a rectangle has a width or a height of 0</li>
     * <li>A text has no characters</li>
     * </ul>
     */
    public boolean isEmpty();
}
