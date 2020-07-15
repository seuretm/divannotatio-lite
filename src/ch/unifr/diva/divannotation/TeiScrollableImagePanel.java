/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation;

import ch.unifr.tei.TeiHisDoc;
import ch.unifr.tei.facsimile.surfacegrp.surface.zone.*;
import ch.unifr.tei.teiheader.filedesc.sourcedesc.msdesc.physdesc.handdesc.handnote.TeiHandNote;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathias Seuret
 */
@SuppressWarnings("Convert2streamapi")
public abstract class TeiScrollableImagePanel extends ScrollableImagePanel {

    private static final Logger logger = Logger.getLogger(TeiScrollableImagePanel.class);

    protected BufferedImage bi;
    protected TeiHisDoc tei;
    protected boolean multiselect = false;
    protected List<ZONE_TYPES> selectableZoneTypes;
    protected ZONE_TYPES currentZoneType;
    protected ZONE_TYPES defaultZoneType;
    protected int pressX = 0;
    protected int pressY = 0;
    protected int selectedPointIndex = 0;
    private boolean onlyPaintCurrentZoneType = false;
    private List<TeiZone> selectedZones = new ArrayList<>();

    protected TeiScrollableImagePanel(GUI gui) {
        super(gui);
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        selectableZoneTypes = new ArrayList<>();
        initActionMappings();
    }

    @Override
    protected void initActionMappings() {
        super.initActionMappings();
        initActionMapping(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete", this::deletePressed);
        initActionMapping(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape", this::escapePressed);
    }

    protected String deletePressed(ActionEvent e) {
        return ACTION_NOTHING;
    }

    protected String escapePressed(ActionEvent e) {
        return ACTION_NOTHING;
    }

    private void drawTeiShape(Graphics2D g, Shape shape) {
        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            g.drawRect(
                    getPanelX(rectangle.x),
                    getPanelY(rectangle.y),
                    (int) (panel.getZoom() * rectangle.width),
                    (int) (panel.getZoom() * rectangle.height)
            );
        } else if (shape instanceof Polygon) {
            Polygon poly = (Polygon) shape;
            for (int i = 0; i < poly.npoints; i++) {
                int j = (i + 1) % poly.npoints;
                g.drawLine(
                        getPanelX(poly.xpoints[i]),
                        getPanelY(poly.ypoints[i]),
                        getPanelX(poly.xpoints[j]),
                        getPanelY(poly.ypoints[j])
                );
            }
        }
    }

    protected void drawSelectedTeiShape(Graphics2D g, Shape shape) {
        if (shape == null) {
            return;
        }
        g.setStroke(new BasicStroke(4));
        g.setColor(new Color(255, 255, 0));
        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            drawRectangleWithPoints(rectangle, g);
        } else if (shape instanceof Polygon) {
            Polygon poly = (Polygon) shape;
            drawPolygonWithPoints(poly, g);
        }
    }

    protected void drawRectangleWithPoints(Rectangle rectangle, Graphics2D g) {
        int x = getPanelX(rectangle.x);
        int y = getPanelY(rectangle.y);
        int w = (int) (panel.getZoom() * rectangle.width);
        int h = (int) (panel.getZoom() * rectangle.height);
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(10));
        g.drawLine(x, y, x, y);
        g.drawLine(x + w, y, x + w, y);
        g.drawLine(x, y + h, x, y + h);
        g.drawLine(x + w, y + h, x + w, y + h);
    }

    protected void drawPolygonWithPoints(Polygon poly, Graphics2D g) {
        for (int i = 0; i < poly.npoints; i++) {
            int j = (i + 1) % poly.npoints;
            g.drawLine(
                    getPanelX(poly.xpoints[i]),
                    getPanelY(poly.ypoints[i]),
                    getPanelX(poly.xpoints[j]),
                    getPanelY(poly.ypoints[j])
            );
        }
        for (int i = 0; i < poly.npoints; i++) {
            g.setStroke(new BasicStroke(10));
            g.drawLine(
                    getPanelX(poly.xpoints[i]),
                    getPanelY(poly.ypoints[i]),
                    getPanelX(poly.xpoints[i]),
                    getPanelY(poly.ypoints[i])
            );
        }
    }

    protected List<TeiZone> getSelectedZones(Rectangle r, ZONE_TYPES type) {
        if (type == ZONE_TYPES.DECO_ZONE) {
            return getSelectedDecoZones(r);
        } else {
            List<TeiZone> selectedTextZones = getSelectedTextZones(r, TeiZoneType.MAIN_TEXT, true);
            List<TeiZone> selectedCommentZones = getSelectedTextZones(r, TeiZoneType.COMMENT_TEXT, true);
            if (type == ZONE_TYPES.TEXT_ZONE) {
                return selectedTextZones;
            }
            if (type == ZONE_TYPES.COMMENT_ZONE) {
                return selectedCommentZones;
            }
            selectedTextZones = getSelectedTextZones(r, TeiZoneType.MAIN_TEXT, false);
            selectedCommentZones = getSelectedTextZones(r, TeiZoneType.COMMENT_TEXT, false);
            List<TeiZone> selectedTextSegZones = new ArrayList<>();
            for (TeiZone selectedTextZone : selectedTextZones) {
                List<TeiZone> selectedTextSegZonest = getSelectedTextSegZones((TeiTextZone) selectedTextZone, r, true);
                selectedTextSegZones.addAll(selectedTextSegZonest);
            }
            if (type == ZONE_TYPES.TEXTSEG_ZONE) {
                return selectedTextSegZones;
            }
            selectedTextSegZones.clear();
            for (TeiZone selectedTextZone : selectedTextZones) {
                List<TeiZone> selectedTextSegZonest = getSelectedTextSegZones((TeiTextZone) selectedTextZone, r, false);
                selectedTextSegZones.addAll(selectedTextSegZonest);
            }
            List<TeiZone> selectedCommentSegZones = new ArrayList<>();
            for (TeiZone selectedCommentZone : selectedCommentZones) {
                List<TeiZone> selectedCommentSegZonest = getSelectedTextSegZones((TeiTextZone) selectedCommentZone, r, true);
                selectedCommentSegZones.addAll(selectedCommentSegZonest);
            }
            if (type == ZONE_TYPES.COMMENTSEG_ZONE) {
                return selectedCommentSegZones;
            }
            selectedCommentSegZones.clear();
            for (TeiZone selectedCommentZone : selectedCommentZones) {
                List<TeiZone> selectedCommentSegZonest = getSelectedTextSegZones((TeiTextZone) selectedCommentZone, r, false);
                selectedCommentSegZones.addAll(selectedCommentSegZonest);
            }
            selectedTextSegZones.addAll(selectedCommentSegZones);
            List<TeiZone> selectedWordZones = new ArrayList<>();
            for (TeiZone selectedTextSegZone : selectedTextSegZones) {
                List<TeiZone> selectedWordZonest = getSelectedWordZones((TeiTextSegZone) selectedTextSegZone, r, true);
                selectedWordZones.addAll(selectedWordZonest);
            }
            if (type == ZONE_TYPES.WORD_ZONE) {
                return selectedWordZones;
            }
            selectedWordZones.clear();
            for (TeiZone selectedTextSegZone : selectedTextSegZones) {
                List<TeiZone> selectedWordZonest = getSelectedWordZones((TeiTextSegZone) selectedTextSegZone, r, false);
                selectedWordZones.addAll(selectedWordZonest);
            }
            List<TeiZone> selectedCharZones = new ArrayList<>();
            for (TeiZone selectedWordZone : selectedWordZones) {
                List<TeiZone> selectedCharZonest = getSelectedCharZones((TeiWordZone) selectedWordZone, r, true);
                selectedCharZones.addAll(selectedCharZonest);
            }
            if (type == ZONE_TYPES.CHAR_ZONE) {
                return selectedCharZones;
            }
        }
        return null;
    }

    /**
     * Returns the smallest Zone of type type which contains pt
     *
     * @param pt   the point which is selecting the zone
     * @param type the preferred type of the zone
     * @return the selected zone or null if no zone of the given type is
     * selected
     */
    protected TeiZone getSelectedZone(Point2D pt, ZONE_TYPES type) {
        TeiDecorationZone selectedDecoZone = getSelectedDecoZone(pt);
        if (type == ZONE_TYPES.DECO_ZONE) {
            return selectedDecoZone;
        }
        TeiTextZone selectedAnyTextZone = getSelectedTextZone(pt, null);
        TeiTextZone selectedTextZone = getSelectedTextZone(pt, TeiZoneType.MAIN_TEXT);
        TeiTextZone selectedCommentZone = getSelectedTextZone(pt, TeiZoneType.COMMENT_TEXT);
        if (type == ZONE_TYPES.TEXT_ZONE) {
            return selectedTextZone;
        }
        if (type == ZONE_TYPES.COMMENT_ZONE) {
            return selectedCommentZone;
        }
        TeiTextSegZone selectedTextSegZone = getSelectedTextSegZone(selectedTextZone, pt);
        if (type == ZONE_TYPES.TEXTSEG_ZONE) {
            return selectedTextSegZone;
        }
        TeiTextSegZone selectedCommentSegZone = getSelectedTextSegZone(selectedCommentZone, pt);
        if (type == ZONE_TYPES.COMMENTSEG_ZONE) {
            return selectedCommentSegZone;
        }
        if (selectedTextSegZone == null && selectedCommentSegZone != null) {
            selectedTextSegZone = selectedCommentSegZone;
        }
        if (selectedTextSegZone == null) {
            selectedTextSegZone = getSelectedTextSegZone(selectedAnyTextZone, pt);
        }
        TeiWordZone selectedWordZone = getSelectedWordZone(selectedTextSegZone, pt);
        if (type == ZONE_TYPES.WORD_ZONE) {
            return selectedWordZone;
        }
        TeiCharZone selectedCharZone = getSelectedCharZone(selectedWordZone, pt);
        if (type == ZONE_TYPES.CHAR_ZONE) {
            return selectedCharZone;
        }
        if (type == ZONE_TYPES.ALL_ZONES) {
            List<TeiZone> zones = new ArrayList<>();
            if (null != selectedCharZone) {
                zones.add(selectedCharZone);
            }
            if (null != selectedCommentSegZone) {
                zones.add(selectedCommentSegZone);
            }
            if (null != selectedWordZone) {
                zones.add(selectedWordZone);
            }
            if (null != selectedTextSegZone) {
                zones.add(selectedTextSegZone);
            }
            if (null != selectedTextZone) {
                zones.add(selectedTextZone);
            }
            if (null != selectedCommentZone) {
                zones.add(selectedCommentZone);
            }
            if (null != selectedDecoZone) {
                zones.add(selectedDecoZone);
            }
            return getSmallestZone(zones);
        }
        return null;
    }

    private TeiDecorationZone getSelectedDecoZone(Point2D pt) {
        // Try every text zone of the page
        TeiDecorationZone selectedDecoZone;
        List<TeiDecorationZone> teiDecoZones = getSelectedDecoZones(pt);
        if (teiDecoZones.size() == 0) {
            selectedDecoZone = null;
        } else if (teiDecoZones.size() == 1) {
            selectedDecoZone = teiDecoZones.get(0);
        } else {
            selectedDecoZone = (TeiDecorationZone) getSmallestZone(teiDecoZones);
        }
        return selectedDecoZone;
    }

    private List<TeiZone> getSelectedDecoZones(Rectangle r) {
        List<TeiZone> teiDecoZones = new ArrayList<>();
        for (TeiDecorationZone zone : gui.page.getDecorationZones()) {
            // If clic is inside the zone
            if (r.contains(zone.getArea().getShape().getBounds())) {
                teiDecoZones.add(zone);
            }
        }
        return teiDecoZones;
    }

    private List<TeiDecorationZone> getSelectedDecoZones(Point2D pt) {
        List<TeiDecorationZone> teiDecoZones = new ArrayList<>();
        for (TeiDecorationZone zone : gui.page.getDecorationZones()) {
            // If clic is inside the zone
            if (zone.getArea().contains(pt)) {
                teiDecoZones.add(zone);
            }
        }
        return teiDecoZones;
    }

    private TeiTextZone getSelectedTextZone(Point2D pt, TeiZoneType type) {
        // Try every text zone of the page
        TeiTextZone selectedTextZone;
        List<TeiTextZone> teiTextZones = getSelectedTextZones(type, pt);
        if (teiTextZones.size() == 0) {
            selectedTextZone = null;
        } else if (teiTextZones.size() == 1) {
            selectedTextZone = teiTextZones.get(0);
        } else {
            selectedTextZone = (TeiTextZone) getSmallestZone(teiTextZones);
        }
        return selectedTextZone;
    }

    private TeiZone getSmallestZone(List<? extends TeiZone> teiZones) {
        TeiZone z = null;
        double minArea = Float.MAX_VALUE;
        for (TeiZone teiZone : teiZones) {
            Rectangle2D r = teiZone.getArea().getShape().getBounds2D();
            double area = r.getWidth() * r.getHeight();
            if (area < minArea) {
                z = teiZone;
                minArea = area;
            }
        }
        return z;
    }

    private List<TeiZone> getSelectedTextZones(Rectangle r, TeiZoneType type, boolean fullyInside) {
        List<TeiZone> zones = new ArrayList<>();
        for (TeiTextZone zone : gui.page.getTextZones()) {
            if (getZoneIsSelectedByRect(zone, type, fullyInside, r)) {
                zones.add(zone);
            }
        }
        return zones;
    }

    private boolean getZoneIsSelectedByRect(TeiZone zone, TeiZoneType type, boolean fullyInside, Rectangle r) {
        // If clic is inside the zone
        if (zone.getType() == type || null == type) {
            if (fullyInside & r.contains(zone.getArea().getShape().getBounds())) {
                return true;
            }
            if (!fullyInside & r.intersects(zone.getArea().getShape().getBounds())) {
                return true;
            }
        }
        return false;
    }

    private List<TeiTextZone> getSelectedTextZones(TeiZoneType type, Point2D pt) {
        List<TeiTextZone> teiTextZones = new ArrayList<>();
        for (TeiTextZone zone : gui.page.getTextZones()) {
            // If clic is inside the zone
            if (zone.getType() == type && zone.getArea().contains(pt)) {
                teiTextZones.add(zone);
            }
        }
        return teiTextZones;
    }

    private TeiTextSegZone getSelectedTextSegZone(TeiTextZone selectedTextZone, Point2D pt) {
        if (selectedTextZone == null) {
            return null;
        }
        // Try every text zone of the page
        TeiTextSegZone selectedTextSegZone;
        List<TeiTextSegZone> selectedTextSegZones = getSelectedTextSegZones(selectedTextZone, pt);
        if (selectedTextSegZones.isEmpty()) {
            selectedTextSegZone = null;
        } else if (selectedTextSegZones.size() == 1) {
            selectedTextSegZone = selectedTextSegZones.get(0);
        } else {
            selectedTextSegZone = (TeiTextSegZone) getSmallestZone(selectedTextSegZones);
        }
        return selectedTextSegZone;
    }

    private TeiWordZone getSelectedWordZone(TeiTextSegZone selectedTextSegZone, Point2D pt) {
        if (selectedTextSegZone == null) {
            return null;
        }
        // Try every text zone of the page
        TeiWordZone selectedWordZone;
        List<TeiWordZone> selectedWordZones = getSelectedWordZones(selectedTextSegZone, pt);
        if (selectedWordZones.isEmpty()) {
            selectedWordZone = null;
        } else if (selectedWordZones.size() == 1) {
            selectedWordZone = selectedWordZones.get(0);
        } else {
            selectedWordZone = (TeiWordZone) getSmallestZone(selectedWordZones);
        }
        return selectedWordZone;
    }

    private TeiCharZone getSelectedCharZone(TeiWordZone selectedWordZone, Point2D pt) {
        if (selectedWordZone == null) {
            return null;
        }
        // Try every text zone of the page
        TeiCharZone selectedCharZone;
        List<TeiCharZone> selectedCharZones = getSelectedCharZones(selectedWordZone, pt);
        if (selectedCharZones.isEmpty()) {
            selectedCharZone = null;
        } else if (selectedCharZones.size() == 1) {
            selectedCharZone = selectedCharZones.get(0);
        } else {
            selectedCharZone = (TeiCharZone) getSmallestZone(selectedCharZones);
        }
        return selectedCharZone;
    }

    private List<TeiZone> getSelectedTextSegZones(TeiTextZone selectedTextZone, Rectangle r, boolean fullyInside) {
        List<TeiZone> selectedTextSegZones = new ArrayList<>();
        for (TeiTextSegZone zone : selectedTextZone) {
            if (getZoneIsSelectedByRect(zone, TeiZoneType.UNKNOWN, fullyInside, r)) {
                selectedTextSegZones.add(zone);
            }
        }
        return selectedTextSegZones;
    }

    private List<TeiZone> getSelectedWordZones(TeiTextSegZone selectedTextSegZone, Rectangle r, boolean fullyInside) {
        List<TeiZone> selectedWordZones = new ArrayList<>();
        for (TeiWordZone zone : selectedTextSegZone.getWordZones()) {
            if (getZoneIsSelectedByRect(zone, TeiZoneType.UNKNOWN, fullyInside, r)) {
                selectedWordZones.add(zone);
            }
        }
        return selectedWordZones;
    }

    private List<TeiZone> getSelectedCharZones(TeiWordZone selectedWordZone, Rectangle r, boolean fullyInside) {
        List<TeiZone> selectedCharZones = new ArrayList<>();
        for (TeiCharZone zone : selectedWordZone.getCharZones()) {
            // If clic is inside the zone
            if (getZoneIsSelectedByRect(zone, null, fullyInside, r)) {
                selectedCharZones.add(zone);
            }
        }
        return selectedCharZones;
    }

    private List<TeiTextSegZone> getSelectedTextSegZones(TeiTextZone selectedTextZone, Point2D pt) {
        List<TeiTextSegZone> selectedTextSegZones = new ArrayList<>();
        for (TeiTextSegZone zone : selectedTextZone) {
            // If clic is inside the zone
            if (zone.getArea().contains(pt)) {
                selectedTextSegZones.add(zone);
            }
        }
        return selectedTextSegZones;
    }

    private List<TeiWordZone> getSelectedWordZones(TeiTextSegZone selectedTextSegZone, Point2D pt) {
        List<TeiWordZone> selectedWordZones = new ArrayList<>();
        for (TeiWordZone zone : selectedTextSegZone.getWordZones()) {
            // If clic is inside the zone
            if (zone.getArea().contains(pt)) {
                selectedWordZones.add(zone);
            }
        }
        return selectedWordZones;
    }

    private List<TeiCharZone> getSelectedCharZones(TeiWordZone selectedWordZone, Point2D pt) {
        List<TeiCharZone> selectedCharZones = new ArrayList<>();
        for (TeiCharZone zone : selectedWordZone.getCharZones()) {
            // If clic is inside the zone
            if (zone.getArea().contains(pt)) {
                selectedCharZones.add(zone);
            }
        }
        return selectedCharZones;
    }

    protected void toggleSelectedZone(TeiZone selectedZone) {
        if (multiselect) {
            for (TeiZone selectedZone1 : selectedZones) {
                if (selectedZone1 == selectedZone) {
                    selectedZones.remove(selectedZone1);
                    return;
                }
            }
            selectedZones.add(selectedZone);
        } else if (this.getSelectedZone() == selectedZone) {
            this.selectedZones.clear();
        } else {
            this.selectedZones.clear();
            this.selectedZones.add(selectedZone);
        }
    }

    protected boolean isSelected(TeiZone zone) {
        if (zone==null) {
            return false;
        }
        return multiselect && selectedZones.contains(zone) || (!multiselect) && (getSelectedZone() == zone);
    }

    protected void updateShape(Point2D drag) throws UnsupportedOperationException {
        if (getSelectedZones().size() > 1 || getSelectedZones().isEmpty()) {
            return;
        }
        TeiZone zone = getFirstSelectedZone();
        Shape shape = zone.getArea().getShape();
        if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            updateRectangle(r, drag);
        } else if (shape instanceof Polygon) {
            Polygon p = (Polygon) shape;
            updatePolygon(p, drag);
        } else {
            //should not happen
            throw new UnsupportedOperationException("The selected shape is neither a rectangle, nor a polygon - this is impossible.");
        }
        zone.updateParentSize();
    }

    protected boolean handlePressedShape(Point2D press) throws UnsupportedOperationException {
        Shape shape = getFirstSelectedZone().getArea().getShape();
        if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            int[][] points = {{r.x, r.y}, {r.x + r.width, r.y}, {r.x + r.width, r.y + r.height}, {r.x, r.y + r.height}};
            if (getSelectedPointOfRectangle(points, press)) {
                return true;
            }
            //updateRectangle(points, r, press);
        } else if (shape instanceof Polygon) {
            Polygon p = (Polygon) shape;
            if (getSelectedPointOfPolygon(p, press)) {
                return true;
            }
            //updatePolygon(p, press);
        } else {
            //should not happen
            throw new UnsupportedOperationException("The selected shape is neither a rectangle, nor a polygon - this is impossible.");
        }
        return false;
    }

    private void updatePolygon(Polygon p, Point2D press) {
        p.xpoints[selectedPointIndex] = (int) Math.round(press.getX());
        p.ypoints[selectedPointIndex] = (int) Math.round(press.getY());
        p.invalidate();
    }

    protected void updateRectangle(Rectangle r, Point2D press) {
        final int TL = 0;
        final int TR = 1;
        final int BR = 2;
        final int BL = 3;
        final int X = 0;
        final int Y = 1;
        
        int[][] points = {{r.x, r.y}, {r.x + r.width, r.y}, {r.x + r.width, r.y + r.height}, {r.x, r.y + r.height}};
        
        int px = (int) Math.round(press.getX());
        int py = (int) Math.round(press.getY());
        int mx = r.x + r.width / 2;
        int my = r.y + r.height / 2;
        
        int neighX = -1;
        int neighY = -1;
        
        if (px<=mx && py<=my) {
            selectedPointIndex = TL;
            neighX = BL;
            neighY = TR;
        } else if (px>mx && py<=my) {
            selectedPointIndex = TR;
            neighX = BR;
            neighY = TL;
        } else if (px<=mx && py>my) {
            selectedPointIndex = BL;
            neighX = TL;
            neighY = BR;
        } else if (px>mx && py>my) {
            selectedPointIndex = BR;
            neighX = TR;
            neighY = BL;
        }
        
        points[selectedPointIndex][X] = px;
        points[selectedPointIndex][Y] = py;
        points[neighX][X] = px;
        points[neighY][Y] = py;
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int p=0; p<4; p++) {
            minX = Math.min(minX, points[p][X]);
            minY = Math.min(minY, points[p][Y]);
            maxX = Math.max(maxX, points[p][X]);
            maxY = Math.max(maxY, points[p][Y]);
        }
        
        r.setBounds(minX, minY, maxX-minX, maxY-minY);
    }

    private boolean getSelectedPointOfPolygon(Polygon p, Point2D press) {
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < p.xpoints.length; i++) {
            Point2D point = new Point2D.Float(p.xpoints[i], p.ypoints[i]);
            if (press.distance(point) < minDistance) {
                minDistance = press.distance(point);
                selectedPointIndex = i;
            }
        }
        if (minDistance < LineSeg.MAX_DISTANCE_TO_POINT_FOR_POINT_SELECTION) {
            return true;
        }
        selectedPointIndex = -1;
        return false;
    }

    protected boolean getSelectedPointOfRectangle(int[][] points, Point2D press) {
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            Point2D point = new Point2D.Float(points[i][0], points[i][1]);
            if (press.distance(point) < minDistance) {
                minDistance = press.distance(point);
                selectedPointIndex = i;
            }
        }
        if (minDistance < LineSeg.MAX_DISTANCE_TO_POINT_FOR_POINT_SELECTION) {
            return true;
        }
        selectedPointIndex = -1;
        return false;
    }

    private void paintTextSegZones(Graphics2D g, TeiTextZone zone, TeiHandNote hn) {
        checkTextSegZonesPaintWidth(g);
        boolean paint;
        paint = shouldBePainted(ZONE_TYPES.TEXTSEG_ZONE);
        paint |= shouldBePainted(ZONE_TYPES.COMMENTSEG_ZONE);
        // Draw non-selected zones at the background
        if (paint) for (TeiTextSegZone z : zone) {
            if (!isSelected(z)) {
                if ((hn != null) && (z.getHandNote() != null)
                        && (hn.getId().equals(z.getHandNote().getId()))) {
                    g.setColor(new Color(0, 255, 255));
                } else if (z.getHandNote() == null) {
                    if (zone.getType() == TeiZoneType.MAIN_TEXT) {
                        g.setColor(new Color(255, 0, 255));
                    } else if (zone.getType() == TeiZoneType.COMMENT_TEXT) {
                        g.setColor(new Color(255, 0, 127));
                    } else {
                        g.setColor(new Color(127, 127, 127));
                    }
                } else {
                    g.setColor(new Color(0, 0, 255));
                }
                drawTeiShape(g, z.getArea().getShape());
            }
        }
        for (TeiTextSegZone z : zone) {
            paintWordZones(g, z, hn);
        }
        checkTextSegZonesPaintWidth(g);

        // Draw selected zones at the foreground
        if (paint) for (TeiTextSegZone z : zone) {
            if (isSelected(z)) {
                g.setColor(new Color(255, 255, 0));
                drawTeiShape(g, z.getArea().getShape());
            }
        }

    }

    private void checkTextSegZonesPaintWidth(Graphics2D g) {
        if (currentZoneType == ZONE_TYPES.TEXTSEG_ZONE || currentZoneType == ZONE_TYPES.COMMENTSEG_ZONE) {
            g.setStroke(new BasicStroke(3));
        } else {
            g.setStroke(new BasicStroke(1));
        }
    }

    private void paintCharZones(Graphics2D g, TeiWordZone zone, TeiHandNote hn) {
        if (!selectableZoneTypes.contains(ZONE_TYPES.CHAR_ZONE)) {
            return;
        }
        checkCharZonesPaintWidth(g);
        boolean paint = shouldBePainted(ZONE_TYPES.CHAR_ZONE);
        // Draw non-selected zones at the background
        if (paint) for (TeiCharZone z : zone.getCharZones()) {
            if (!isSelected(z)) {
                if ((hn != null) && (z.getHandNote() != null)
                        && (hn.getId().equals(z.getHandNote().getId()))) {
                    g.setColor(new Color(0, 255, 255));
                } else if (z.getHandNote() == null) {
                    g.setColor(new Color(255, 0, 255));
                } else {
                    g.setColor(new Color(0, 0, 255));
                }
                drawTeiShape(g, z.getArea().getShape());
            }
        }

        // Draw selected zones at the foreground
        if (paint) for (TeiCharZone z : zone.getCharZones()) {
            if (isSelected(z)) {
                g.setColor(new Color(255, 255, 0));
                drawTeiShape(g, z.getArea().getShape());
            }
        }
    }

    private void checkCharZonesPaintWidth(Graphics2D g) {
        if (currentZoneType == ZONE_TYPES.CHAR_ZONE) {
            g.setStroke(new BasicStroke(3));
        } else {
            g.setStroke(new BasicStroke(1));
        }
    }

    private void paintWordZones(Graphics2D g, TeiTextSegZone zone, TeiHandNote hn) {
        if (!selectableZoneTypes.contains(ZONE_TYPES.WORD_ZONE)) {
            return;
        }
        checkWordZonesPaintWidth(g);
        boolean paint = shouldBePainted(ZONE_TYPES.WORD_ZONE);
        // Draw non-selected zones at the background
        if (paint) for (TeiWordZone z : zone.getWordZones()) {
            if (!isSelected(z)) {
                if ((hn != null) && (z.getHandNote() != null)
                        && (hn.getId().equals(z.getHandNote().getId()))) {
                    g.setColor(new Color(0, 255, 255));
                } else if (z.getHandNote() == null) {
                    g.setColor(new Color(255, 0, 255));
                } else {
                    g.setColor(new Color(0, 0, 255));
                }
                drawTeiShape(g, z.getArea().getShape());
            }
        }
        for (TeiWordZone z : zone.getWordZones()) {
            paintCharZones(g, z, hn);
        }
        checkWordZonesPaintWidth(g);
        // Draw selected zones at the foreground
        if (paint) for (TeiWordZone z : zone.getWordZones()) {
            if (isSelected(z)) {
                g.setColor(new Color(255, 255, 0));
                drawTeiShape(g, z.getArea().getShape());
            }
        }
    }

    private void checkWordZonesPaintWidth(Graphics2D g) {
        if (currentZoneType == ZONE_TYPES.WORD_ZONE) {
            g.setStroke(new BasicStroke(3));
        } else {
            g.setStroke(new BasicStroke(1));
        }
    }

    protected void paintTextZones(Graphics2D g, TeiHandNote hn) {
        boolean paint;
        paint = shouldBePainted(ZONE_TYPES.TEXT_ZONE);
        paint |= shouldBePainted(ZONE_TYPES.COMMENT_ZONE);
        if (paint) for (TeiTextZone zone : gui.page.getTextZones()) {
            checkTextZonesPaintWidth(g, zone);
            if (!isSelected(zone)) {
                if ((hn != null) && (zone.getHandNote() != null)
                        && (hn.getId().equals(zone.getHandNote().getId()))) {
                    g.setColor(new Color(0, 255, 255));
                } else if (zone.getHandNote() == null) {
                    if (zone.getType() == TeiZoneType.MAIN_TEXT) {
                        g.setColor(new Color(255, 0, 255));
                    } else if (zone.getType() == TeiZoneType.COMMENT_TEXT) {
                        g.setColor(new Color(255, 0, 127));
                    } else {
                        g.setColor(new Color(127, 127, 127));
                    }
                } else {
                    g.setColor(new Color(0, 0, 255));
                }
                drawTeiShape(g, zone.getArea().getShape());
            }

        }
        for (TeiTextZone zone : gui.page.getTextZones()) {
            paintTextSegZones(g, zone, hn);
        }
        if (paint) for (TeiTextZone zone : gui.page.getTextZones()) {
            checkTextZonesPaintWidth(g, zone);
            if (isSelected(zone)) {
                g.setColor(new Color(255, 255, 0));
                drawTeiShape(g, zone.getArea().getShape());
            }

        }

    }

    private void checkTextZonesPaintWidth(Graphics2D g, TeiTextZone zone) {
        g.setStroke(new BasicStroke(1));
        if (currentZoneType == ZONE_TYPES.TEXT_ZONE) {
            if (zone.getType() == TeiZoneType.MAIN_TEXT) {
                g.setStroke(new BasicStroke(4));
            }
        }
        if (currentZoneType == ZONE_TYPES.COMMENT_ZONE) {
            if (zone.getType() == TeiZoneType.COMMENT_TEXT) {
                g.setStroke(new BasicStroke(4));
            }
        }
    }

    protected void paintDecorationZones(Graphics2D g) {
        if (currentZoneType == ZONE_TYPES.DECO_ZONE) {
            g.setStroke(new BasicStroke(4));
        } else {
            g.setStroke(new BasicStroke(1));
        }
        if (shouldBePainted(ZONE_TYPES.DECO_ZONE)) {
            for (TeiDecorationZone zone : gui.page.getDecorationZones()) {
                if (getSelectedZones().contains(zone)) {
                    g.setColor(new Color(255, 255, 0));
                } else {
                    g.setColor(new Color(255, 255, 255));
                }

                this.drawTeiShape(g, zone.getArea().getShape());
            }
        }
    }

    private boolean shouldBePainted(ZONE_TYPES type) {
        return !(onlyPaintCurrentZoneType & currentZoneType != type & currentZoneType != ZONE_TYPES.ALL_ZONES);
    }

    public List getSelectableZoneTypes() {
        return selectableZoneTypes;
    }

    public ZONE_TYPES getDefaultZoneType() {
        return defaultZoneType;
    }

    protected void onFormFocusGained() {
        gui.updateSharedPanelItems();
        currentZoneType = defaultZoneType;
    }

    public ZONE_TYPES getCurrentZoneType() {
        return currentZoneType;
    }

    public void setCurrentZoneType(ZONE_TYPES currentZoneType) {
        this.currentZoneType = currentZoneType;
        selectedZones.clear();
        invalidate();
        repaint();
    }

    protected List<TeiZone> getSelectedZones() {
        return selectedZones;
    }

    private TeiZone getSelectedZone() {
        if (multiselect && selectedZones.size() > 1) {
            System.err.println("Warning: getSelectedZone called in multiselect.\n Call getFirstSelectedZone instead.");
        }
        if (selectedZones.size() == 0) {
            return null;
        }
        return selectedZones.get(0);
    }

    @Override
    public void paintPanel(Graphics2D g) {
        gui.setvOffset(getOffsetX());
        gui.sethOffset(getOffsetY());
    }

    protected TeiZone getFirstSelectedZone() {
        if (selectedZones.size() == 0) {
            return null;
        }
        return selectedZones.get(0);
    }

    public void setOnlyPaintCurrentZoneType(boolean onlyPaintCurrentZoneType) {
        this.onlyPaintCurrentZoneType = onlyPaintCurrentZoneType;
        invalidate();
        repaint();
    }

    public enum ZONE_TYPES {
        ALL_ZONES, TEXT_ZONE, TEXTSEG_ZONE, COMMENTSEG_ZONE, DECO_ZONE, WORD_ZONE, CHAR_ZONE, COMMENT_ZONE
    }
}
