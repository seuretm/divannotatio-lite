/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liwicki
 */
public class MathUtils {

    private static final Logger logger = Logger.getLogger(MathUtils.class);

    private static Point2D getPointIntersection(Line2D l1, Line2D l2) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        return getPointIntersection(l1.getX1(), l1.getY1(), l1.getX2(), l1.getY2(), l2.getX1(), l2.getY1(), l2.getX2(), l2.getY2());
    }

    private static Point2D getPointIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0.0) {
            // Lines are parallel.
            return null;
        }
        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
        if (ua >= 0.0F && ua <= 1.0F && ub >= 0.0F && ub <= 1.0F) {
            // Get the intersection point.
            return new Point((int) (x1 + ua * (x2 - x1)), (int) (y1 + ua * (y2 - y1)));
        }
        return null;
    }

    public static Point2D getCenterOfLine(Line2D selectLine) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        double x = (selectLine.getX1() + selectLine.getX2()) / 2;
        double y = (selectLine.getY1() + selectLine.getY2()) / 2;
        return new Point2D.Double(x, y);
    }

    public static List<Line2D> getLinesForShape(Shape s) throws UnsupportedOperationException {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Line2D> lines = new ArrayList<>();
        if (s instanceof Rectangle) {
            Rectangle r = (Rectangle) s;
            lines.add(new Line2D.Float(r.x, r.y, r.x + r.width, r.y));
            lines.add(new Line2D.Float(r.x + r.width, r.y, r.x + r.width, r.y + r.height));
            lines.add(new Line2D.Float(r.x + r.width, r.y + r.height, r.x, r.y + r.height));
            lines.add(new Line2D.Float(r.x, r.y + r.height, r.x, r.y));
        } else if (s instanceof Polygon) {
            Polygon poly = (Polygon) s;
            for (int i = 0; i < poly.npoints; i++) {
                int j = (i + 1) % poly.npoints;
                lines.add(new Line2D.Float(poly.xpoints[i], poly.ypoints[i], poly.xpoints[j], poly.ypoints[j]));
            }
        } else {
            throw new UnsupportedOperationException("Areas should be only polygons or rectangles");
        }
        return lines;
    }

    public static List<Polygon> getResultingShapes(List<Line2D> lines, Line2D selectLine) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Polygon> resultShapes = new ArrayList<>();
        resultShapes.add(new Polygon());
        resultShapes.add(new Polygon());
        int curShape = 0;
        for (Line2D line : lines) {
            resultShapes.get(curShape).addPoint((int) line.getX1(), (int) line.getY1());
            if (line.intersectsLine(selectLine)) {
                Point2D interSection = MathUtils.getPointIntersection(line, selectLine);
                if (null == interSection) {
                    System.err.println("Warning: the interesectsLine method said the lines intersect, but no intersectionPoint was found");
                    System.err.println("Line1: " + line.getX1() + "," + line.getY1() + " - " + line.getX2() + "," + line.getY2());
                    System.err.println("Line2: " + selectLine.getX1() + "," + selectLine.getY1() + " - " + selectLine.getX2() + "," + selectLine.getY2());
                    interSection = MathUtils.getCenterOfLine(line);
                }
                resultShapes.get(0).addPoint((int) interSection.getX(), (int) interSection.getY());
                resultShapes.get(1).addPoint((int) interSection.getX(), (int) interSection.getY());
                curShape = 1 - curShape;
            }
        }
        return resultShapes;
    }

}
