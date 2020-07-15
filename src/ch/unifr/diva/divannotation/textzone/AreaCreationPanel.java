/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.textzone;

import ch.unifr.diva.divaservices.DivaServicesCommunicator;
import ch.unifr.diva.divaservices.returntypes.DivaServicesResponse;
import ch.unifr.tei.facsimile.surfacegrp.surface.zone.*;
import ch.unifr.tei.utils.TeiArea;
import ch.unifr.tei.utils.TeiAreaPoly;
import ch.unifr.tei.utils.TeiAreaRect;
import com.mashape.unirest.http.exceptions.UnirestException;
import ch.unifr.diva.divaservicesv2.APIv2;
import ch.unifr.diva.divaservicesv2.PolyListResult;
import ch.unifr.diva.divannotation.GUI;
import ch.unifr.diva.divannotation.MathUtils;
import ch.unifr.diva.divannotation.TeiScrollableImagePanel;
import ch.unifr.diva.divannotation.internationalization.Texts;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Mathias Seuret
 */
@SuppressWarnings({"FieldCanBeLocal", "Convert2streamapi"})
public class AreaCreationPanel extends TeiScrollableImagePanel {

    private static final Logger logger = Logger.getLogger(AreaCreationPanel.class);

    private JButton createTextArea = new JButton(Texts.getText(Texts.CREATE_A_NEW_TEXT_AREA));
    private JButton createDecorationArea = new JButton(Texts.getText(Texts.CREATE_A_NEW_DECORATION_AREA));
    private JButton createCommentArea = new JButton(Texts.getText(Texts.CREATE_A_NEW_COMMENT_AREA));
    private JButton createTextSegArea = new JButton(Texts.getText(Texts.CREATE_A_NEW_TEXT_LINE_AREA));
    private JButton createCommentSegArea = new JButton(Texts.getText(Texts.CREATE_A_NEW_COMMENT_LINE_AREA));
    private JButton selectWithRectangle = new JButton(Texts.getText(Texts.SELECT_ALL_AREAS_IN_RECTANGLE));
    private JButton convertToTextArea = new JButton(Texts.getText(Texts.CONVERT_TO_TEXT_AREA));
    private JButton convertToDecoArea = new JButton(Texts.getText(Texts.CONVERT_TO_DECORATION_AREA));
    private JButton convertToCommentArea = new JButton(Texts.getText(Texts.CONVERT_TO_COMMENT_AREA));
    private JButton extractOrGroupArea = new JButton(Texts.getText(Texts.EXTRACT_OR_GROUP_AREAS));
    private JButton deleteZone = new JButton(Texts.getText(Texts.DELETE_SELECTED_ZONE));
    private JButton generateRectangles = new JButton(Texts.getText(Texts.AUTO_GENERATE_RECT_LINES));
    private JButton generatePolygons = new JButton(Texts.getText(Texts.AUTO_GENERATE_POLYG_LINES));
    private JButton generateDanielPolygons = new JButton(Texts.getText(Texts.AUTO_GENERATE_DANIEL_LINES));
    private JButton generateDanielPolygons2 = new JButton(Texts.getText(Texts.AUTO_GENERATE_DANIEL_LINES2));
    private JButton splitCurrentArea = new JButton(Texts.getText(Texts.SPLIT_CURRENT_AREA));
    private JButton splitCurrentAreaIntoSubCategory = new JButton(Texts.getText(Texts.SPLIT_CURRENT_AREA_INTO_SUB));
    private JButton mergeSelectedAreas = new JButton(Texts.getText(Texts.MERGE_SELECTED_AREAS));
    private Rectangle selectRect = null;
    private Polygon selectPoly = null;
    private String ds2ImageName = null; // image name on the diva services

    public AreaCreationPanel(GUI gui) {
        super(gui);
        multiselect = true;
        selectableZoneTypes.add(ZONE_TYPES.TEXT_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.COMMENT_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.DECO_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.TEXTSEG_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.COMMENTSEG_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.WORD_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.CHAR_ZONE);
        selectableZoneTypes.add(ZONE_TYPES.ALL_ZONES);
        defaultZoneType = ZONE_TYPES.ALL_ZONES;
        currentZoneType = defaultZoneType;

        leftPanel.add(new JLabel("First draw a rectangle or polygon and then:"));
        initializeButton(createTextArea, Texts.getText(Texts.CREATE_A_NEW_TEXT_AREA_OUT), AreaCreationPanel.this::createTextZone);
        initializeButton(createTextSegArea, Texts.getText(Texts.CREATE_A_NEW_TEXT_LINE_AREA_OUT), AreaCreationPanel.this::createTextSegZone);
        initializeButton(createCommentArea, Texts.getText(Texts.CREATE_A_NEW_COMMENT_AREA_OUT), this::createCommentZone);
        //initializeButton(createCommentSegArea, Texts.getText(Texts.CREATE_A_NEW_COMMENT_LINE_AREA_OUT), AreaCreationPanel.this::createCommentSegZone);
        //initializeButton(createDecorationArea, Texts.getText(Texts.CREATE_A_NEW_DECORATION_AREA_OUT), AreaCreationPanel.this::createDecoZone);
        //initializeButton(selectWithRectangle, Texts.getText(Texts.MULTISELECT_ALL_AREAS_OF_THE_TYPE), AreaCreationPanel.this::selectZones);
        leftPanel.add(new JLabel(" "));
        //leftPanel.add(new JLabel("Or select a Text/Comment/\nDeco Area and:"));
        //initializeButton(convertToTextArea, Texts.getText(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_TEXT), AreaCreationPanel.this::convertToTextZone);
        //initializeButton(convertToCommentArea, Texts.getText(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_COMMENT), AreaCreationPanel.this::convertToCommentZone);
        //initializeButton(convertToDecoArea, Texts.getText(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_DECORATION), AreaCreationPanel.this::convertToDecoZone);
        //initializeButton(extractOrGroupArea, Texts.getText(Texts.MAKE_A_NEW_TEXT_OR_COMMENT_AREA_OUT_OF_TH), AreaCreationPanel.this::extractOrGroupZone);
        leftPanel.add(new JLabel(" "));
        initializeButton(deleteZone, Texts.getText(Texts.DELETE_THE_CURRENTLY_SELECTED_AREAS), AreaCreationPanel.this::deleteZones);
        initializeButton(generateDanielPolygons, Texts.getText(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY), AreaCreationPanel.this::generateDanielPolygons);
        initializeButton(generateDanielPolygons2, Texts.getText(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY), AreaCreationPanel.this::generateDanielPolygons2);
        initializeButton(generateRectangles, Texts.getText(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION), AreaCreationPanel.this::generateRectangles);
        initializeButton(generatePolygons, Texts.getText(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY), AreaCreationPanel.this::generatePolygons);
        //initializeButton(splitCurrentArea, Texts.SPLIT_CURRENT_AREA_TOOL, AreaCreationPanel.this::splitCurrentZone);
        //initializeButton(splitCurrentAreaIntoSubCategory, Texts.SPLIT_CURRENT_AREA_INTO_SUB_TOOL, AreaCreationPanel.this::splitCurrentZoneIntoSub);
        //initializeButton(mergeSelectedAreas, Texts.MERGE_SELECTED_AREAS_TOOL, AreaCreationPanel.this::mergeSelectedZones);
        leftPanel.add(new JLabel(" "));
        leftPanel.revalidate();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
                ds2ImageName = null;
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                // Nothing to do
            }
        });

    }

    private void initializeButton(JButton button, String toolTip, ActionListener act) {
        leftPanel.add(button);
        button.setToolTipText(toolTip);
        button.addActionListener(act);
    }

    @Override
    public void mouseDraggedEvent(MouseEvent me) {
        int x = getRealX(me.getX());
        int y = getRealY(me.getY());
        Point2D press = new Point2D.Float(x, y);
        if (selectedPointIndex >= 0) {
            if (selectRect != null) {
                updateRectangle(selectRect, press);
            } else {
                updateShape(new Point2D.Float(x, y));
            }
        }
        selectPoly = null;
        repaint();
    }

    @Override
    public void mouseClickedEvent(MouseEvent me) {
        int x = getRealX(me.getX());
        int y = getRealY(me.getY());
        if (null != selectPoly) {
            selectPoly.addPoint(x, y);
        } else {
            if (null != selectRect) if (selectRect.height > 0 & selectRect.height > 0) {
                return;
            }
            TeiZone selectedZone = getSelectedZone(new Point2D.Float(x, y), currentZoneType);
            if (null == selectedZone) {
                this.getSelectedZones().clear();
                selectPoly = new Polygon();
                selectPoly.addPoint(x, y);
            } else {
                toggleSelectedZone(selectedZone);
            }
        }
        selectRect = null;
        repaint();
    }

    @Override
    public void mousePressedEvent(MouseEvent me) {
        pressX = getRealX(me.getX());
        pressY = getRealY(me.getY());
        Point2D press = new Point2D.Float(pressX, pressY);
        if (getSelectedZones().size() == 1) {
            if (handlePressedShape(press)) {
                return;
            }
            //getSelectedZones().clear();
        }
        if (getSelectedZone(press, currentZoneType) == null) {
            if (selectRect != null) {
                Rectangle r = selectRect;
                int[][] points = {{r.x, r.y}, {r.x + r.width, r.y}, {r.x + r.width, r.y + r.height}, {r.x, r.y + r.height}};
                if (!getSelectedPointOfRectangle(points, press)) {
                    selectRect = new Rectangle(pressX, pressY, 0, 0);
                    selectedPointIndex = 2;
                }
            } else {
                selectRect = new Rectangle(pressX, pressY, 0, 0);
                selectedPointIndex = 2;
            }
        }

    }

    @Override
    public void mouseReleasedEvent(MouseEvent me) {
    }

    @Override
    public void paintPanel(Graphics2D g) {
        getPanel().setZoom(gui.getZoom());
        super.paintPanel(g);
        g.setColor(Color.BLACK);
        if (null != selectRect) {
            drawRectangleWithPoints(selectRect, g);
        }
        if (null != selectPoly) {
            drawPolygonWithPoints(selectPoly, g);
        }

        paintTextZones(g, null);
        paintDecorationZones(g);
        if (getSelectedZones().size() == 1) {
            this.drawSelectedTeiShape(g, getFirstSelectedZone().getArea().getShape());
        }

        if (gui.page.getTextZones().size() > 0) {
            gui.unlockTabs(4);
        }
    }

    private void createZone(ZONE_TYPES type) {

        if (null == selectRect && null == selectPoly) {
            JOptionPane.showMessageDialog(null, "First either draw a rectangle (by dragging the size) or a polygon (by pointwise clicking).");
            return;
        }
        logger.debug("Creating a new zone");
        if (null != type) {
            TeiArea teiArea = null != selectRect ? new TeiAreaRect(selectRect) : new TeiAreaPoly(selectPoly);
            switch (type) {
                case TEXT_ZONE: {
                    TeiTextZone tt = gui.page.addTextZone(teiArea);
                    tt.setType(TeiZoneType.MAIN_TEXT);
                    break;
                }
                case DECO_ZONE: {
//                    TeiDecorationZone tt =
                    gui.page.addDecorationZone(teiArea);
                    break;
                }
                case COMMENT_ZONE: {
                    TeiTextZone tt = gui.page.addTextZone(teiArea);
                    tt.setType(TeiZoneType.COMMENT_TEXT);
                    break;
                }
                case TEXTSEG_ZONE: {
                    TeiTextZone tt = getOverlappingZone(gui, teiArea, TeiZoneType.MAIN_TEXT);
                    tt.addTextSegZone(teiArea);
                    break;
                }
                case COMMENTSEG_ZONE: {
                    TeiTextZone tt = getOverlappingZone(gui, teiArea, TeiZoneType.COMMENT_TEXT);
                    tt.addTextSegZone(teiArea);
                    break;
                }
                default:
                    break;
            }
        }
        selectRect = null;
        selectPoly = null;
        repaint();
    }
    
    public static TeiTextZone getOverlappingZone(GUI gui, TeiArea a, TeiZoneType t) {
        TeiTextZone res = null;
        Rectangle ra = a.getShape().getBounds();
        int max = 0;
        for (TeiTextZone tz : gui.page.getTextZones()) {
            if (tz.getType()!=t) {
                continue;
            }
            Rectangle rtz = tz.getArea().getShape().getBounds();
            
            Rectangle intersect = rtz.intersection(ra);
            System.out.println("ra:  ["+ra.x+","+ra.y+" "+ra.width+"x"+ra.height);
            System.out.println("rtz: ["+rtz.x+","+rtz.y+" "+rtz.width+"x"+rtz.height);
            System.out.println("Intersection: ["+intersect.x+","+intersect.y+" "+intersect.width+"x"+intersect.height);
            
            if (intersect.width < 0 || intersect.height < 0) {
                continue;
            }
            
            int area = intersect.width * intersect.height;
            if (area>max) {
                max = area;
                res = tz;
            }
        }
        if (res==null) {
            res = gui.page.addTextZone(a);
            res.setType(t);
        }
        
        return res;
    }

    private void createTextZone(ActionEvent evt) {
        createZone(ZONE_TYPES.TEXT_ZONE);
    }

    private void createTextSegZone(ActionEvent evt) {
        createZone(ZONE_TYPES.TEXTSEG_ZONE);
    }

    private void createDecoZone(ActionEvent evt) {
        createZone(ZONE_TYPES.DECO_ZONE);
    }

    private void createCommentZone(ActionEvent evt) {
        createZone(ZONE_TYPES.COMMENT_ZONE);
    }

    private void createCommentSegZone(ActionEvent evt) {
        createZone(ZONE_TYPES.COMMENTSEG_ZONE);
    }

    private void selectZones(ActionEvent evt) {
        if (null == selectRect) {
            JOptionPane.showMessageDialog(null, "First draw a rectangle (by dragging the size).");
            return;
        }
        if (currentZoneType == ZONE_TYPES.ALL_ZONES) {
            JOptionPane.showMessageDialog(null, "Please first specify the zonetype to select (with the radiobuttons in the bottom)");
            return;
        }
        List<TeiZone> zones = getSelectedZones(selectRect, currentZoneType);
        if (zones != null) {
            getSelectedZones().addAll(zones);
        }
    }

    private void convertZone(ZONE_TYPES type) {
        if (getSelectedZones().size() == 0) {
            JOptionPane.showMessageDialog(null, "Select an area first.");
            return;
        }
        if (getFirstSelectedZone() instanceof TeiTextSegZone) {
            JOptionPane.showMessageDialog(null, "A single text line cannot be converted - convert the parent or separate from parent first.");
            return;
        }
        if (null != type) {
            for (TeiZone selectedZone : getSelectedZones()) {

                switch (type) {
                    case TEXT_ZONE: {
                        if (selectedZone instanceof TeiTextZone) {
                            selectedZone.setType(TeiZoneType.MAIN_TEXT);
                        } else if (selectedZone instanceof TeiDecorationZone) {
                            gui.page.removeDecorationZone((TeiDecorationZone) selectedZone);
                            gui.page.addTextZone(selectedZone.getArea());
                        }
                        break;
                    }
                    case DECO_ZONE: {
                        if (selectedZone instanceof TeiTextZone) {
                            if (selectedZone.getNumberOfChildZones() > 0) {
                                JOptionPane.showMessageDialog(null, "The textArea still has children\nIt cannot be converted to a decoration.\nDelete the children first.");
                                return;
                            } else {
                                gui.page.removeTextZone((TeiTextZone) selectedZone);
                                gui.page.addDecorationZone(selectedZone.getArea());
                            }
//                        } else if (selectedZone instanceof TeiDecorationZone) {
//                            //nothing
                        }
                        break;
                    }
                    case COMMENT_ZONE: {
                        if (selectedZone instanceof TeiTextZone) {
                            selectedZone.setType(TeiZoneType.COMMENT_TEXT);
                        } else if (selectedZone instanceof TeiDecorationZone) {
                            gui.page.removeDecorationZone((TeiDecorationZone) selectedZone);
                            gui.page.addTextZone(selectedZone.getArea());
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        getSelectedZones().clear();
    }

    private void convertToTextZone(ActionEvent evt) {
        convertZone(ZONE_TYPES.TEXT_ZONE);
    }

    private void convertToDecoZone(ActionEvent evt) {
        convertZone(ZONE_TYPES.DECO_ZONE);

    }

    private void convertToCommentZone(ActionEvent evt) {
        convertZone(ZONE_TYPES.COMMENT_ZONE);

    }

    private void extractOrGroupZone(ActionEvent evt) {
        if (getSelectedZones().size() == 0) {
            JOptionPane.showMessageDialog(null, "Select an area first.");
            return;
        }
        if (getSelectedZones().size() > 1) {
            TeiZoneType type = getFirstSelectedZone().getType();
            for (TeiZone selectedZone : getSelectedZones()) {
                if (selectedZone.getType() != type) {
                    JOptionPane.showMessageDialog(null, "All selected Zones should have the same type.");
                    return;
                }
                if (!(selectedZone instanceof TeiTextSegZone)) {
                    JOptionPane.showMessageDialog(null, "Only line areas can be extracted or grouped.");
                    return;
                }
            }
        }
        TeiTextSegZone fZone = (TeiTextSegZone) getFirstSelectedZone();
        TeiTextZone newParent = gui.page.addTextZone(fZone.getArea());
        for (TeiZone selectedZone : getSelectedZones()) {
            if (selectedZone instanceof TeiTextSegZone) {
                TeiTextSegZone zone = (TeiTextSegZone) selectedZone;
                if (zone.getParent().getNumberOfChildZones() == 1) {
                    gui.page.removeTextZone(zone.getParent());
                }
                TeiZoneType t = zone.getParent().getType();
                zone.getParent().removeTextSegZone(zone);
                newParent.setType(t);
                newParent.addTextSegZone(zone.getArea());
            } else {
                JOptionPane.showMessageDialog(null, "Only line areas can be extracted.");
                return;
            }
        }
        newParent.fitChildrenBounds();
        getSelectedZones().clear();
    }

    @Override
    protected String deletePressed(ActionEvent e) {
        if (getSelectedZones().size() > 0) {
            deleteZones(e);
            repaint();
            return ACTION_PERFORMED;
        }
        if (null != selectRect) {
            selectRect = null;
            repaint();
            return ACTION_PERFORMED;
        }
        if (null != selectPoly) {
            selectPoly = null;
            repaint();
            return ACTION_PERFORMED;
        }
        return super.deletePressed(e); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String escapePressed(ActionEvent e) {
        if (getSelectedZones().size() > 0) {
            getSelectedZones().clear();
            repaint();
            return ACTION_PERFORMED;
        }
        if (null != selectRect) {
            selectRect = null;
            repaint();
            return ACTION_PERFORMED;
        }
        if (null != selectPoly) {
            selectPoly = null;
            repaint();
            return ACTION_PERFORMED;
        }
        return super.escapePressed(e); //To change body of generated methods, choose Tools | Templates.
    }

    private void deleteZones(ActionEvent evt) {
        if (getSelectedZones().size() == 0) {
            return;
        }
        for (TeiZone selectedZone : getSelectedZones()) {
            deleteZone(selectedZone);
        }
        getSelectedZones().clear();
        repaint();
    }

    private void deleteZone(TeiZone selectedZone) {
        if (selectedZone instanceof TeiTextZone) {
            gui.page.removeTextZone((TeiTextZone) selectedZone);
        } else if (selectedZone instanceof TeiDecorationZone) {
            gui.page.removeDecorationZone((TeiDecorationZone) selectedZone);
        } else if (selectedZone instanceof TeiTextSegZone) {
            TeiTextSegZone z = (TeiTextSegZone) selectedZone;
            TeiTextZone p = z.getParent();
            p.removeTextSegZone(z);
        } else if (selectedZone instanceof TeiWordZone) {
            TeiWordZone z = (TeiWordZone) selectedZone;
            TeiTextSegZone p = z.getParent();
            p.removeWordZone(z);
        } else if (selectedZone instanceof TeiCharZone) {
            TeiCharZone z = (TeiCharZone) selectedZone;
            TeiWordZone p = z.getParent();
            p.removeCharZone(z);
        }
    }

    private boolean splitZone(List<Line2D> lines, Line2D selectLine, TeiZone zone, boolean toSubCategory) {
        List<Polygon> resultShapes = MathUtils.getResultingShapes(lines, selectLine);
        if (zone instanceof TeiDecorationZone) {
            if (toSubCategory) {
                System.err.println("Warning: DecorationZones have no sub-zones. Will generate two DecorationZones instead.");
            }
            TeiDecorationZone z = (TeiDecorationZone) zone;
            gui.page.removeDecorationZone(z);
            gui.page.addDecorationZone(new TeiAreaPoly(resultShapes.get(0)));
            gui.page.addDecorationZone(new TeiAreaPoly(resultShapes.get(1)));
        } else if (zone instanceof TeiTextZone) {
            TeiTextZone z = (TeiTextZone) zone;
            if (zone.getNumberOfChildZones() > 0) {
                JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_ERR_NOT_EMPTY));
                return true;
            }
            if (toSubCategory) {
                z.addTextSegZone(new TeiAreaPoly(resultShapes.get(0)));
                z.addTextSegZone(new TeiAreaPoly(resultShapes.get(1)));
                if (getCurrentZoneType() == ZONE_TYPES.TEXT_ZONE) {
                    setCurrentZoneType(ZONE_TYPES.TEXTSEG_ZONE);
                } else {
                    setCurrentZoneType(ZONE_TYPES.COMMENTSEG_ZONE);
                }
                gui.updateSharedPanelItems();
            } else {
                gui.page.removeTextZone(z);
                TeiTextZone newZ;
                newZ = gui.page.addTextZone(new TeiAreaPoly(resultShapes.get(0)));
                newZ.setType(z.getType());
                newZ = gui.page.addTextZone(new TeiAreaPoly(resultShapes.get(1)));
                newZ.setType(z.getType());
            }
        } else if (zone instanceof TeiTextSegZone) {
            if (zone.getNumberOfChildZones() > 0) {
                JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_ERR_NOT_EMPTY));
                return true;
            }
            TeiTextSegZone z = (TeiTextSegZone) zone;
            if (toSubCategory) {
                z.addWordZone(new TeiAreaPoly(resultShapes.get(0)));
                z.addWordZone(new TeiAreaPoly(resultShapes.get(1)));
                setCurrentZoneType(ZONE_TYPES.WORD_ZONE);
                gui.updateSharedPanelItems();
            } else {
                TeiTextZone p = z.getParent();
                p.removeTextSegZone(z);
                p.addTextSegZone(new TeiAreaPoly(resultShapes.get(0)));
                p.addTextSegZone(new TeiAreaPoly(resultShapes.get(1)));
            }
        } else if (zone instanceof TeiWordZone) {
            if (zone.getNumberOfChildZones() > 0) {
                JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_ERR_NOT_EMPTY));
                return true;
            }
            TeiWordZone z = (TeiWordZone) zone;
            if (toSubCategory) {
                z.addCharZone(new TeiAreaPoly(resultShapes.get(0)));
                z.addCharZone(new TeiAreaPoly(resultShapes.get(1)));
                setCurrentZoneType(ZONE_TYPES.CHAR_ZONE);
                gui.updateSharedPanelItems();
            } else {
                TeiTextSegZone p = z.getParent();
                p.removeWordZone(z);
                p.addWordZone(new TeiAreaPoly(resultShapes.get(0)));
                p.addWordZone(new TeiAreaPoly(resultShapes.get(1)));
            }
        } else if (zone instanceof TeiCharZone) {
            if (toSubCategory) {
                JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_ERR_NO_CHAR));
                return true;
            } else {
                TeiCharZone z = (TeiCharZone) zone;
                TeiWordZone p = z.getParent();
                p.removeCharZone(z);
                p.addCharZone(new TeiAreaPoly(resultShapes.get(0)));
                p.addCharZone(new TeiAreaPoly(resultShapes.get(1)));
            }
        }

        return false;
    }

    private void splitCurrentZone(ActionEvent evt) {
        splitCurrentZone(false);
    }

    private boolean splitCurrentZone(boolean toSubCategory) throws UnsupportedOperationException {
        if (null == selectPoly || selectPoly.npoints != 2) {
            JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_HINT_MESSAGE));
            return true;
        }
        Line2D selectLine = new Line2D.Float(selectPoly.xpoints[0], selectPoly.ypoints[0], selectPoly.xpoints[1], selectPoly.ypoints[1]);
        Point2D pt = MathUtils.getCenterOfLine(selectLine);
        TeiZone zone = getSelectedZone(pt, currentZoneType);
        if (null == zone) {
            JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_ERR_MESSAGE));
            return true;
        }
        Shape s = zone.getArea().getShape();
        List<Line2D> lines = MathUtils.getLinesForShape(s);
        int numberOfIntersections = 0;
        for (Line2D line : lines) {
            if (line.intersectsLine(selectLine)) {
                numberOfIntersections++;
            }
        }
        if (numberOfIntersections != 2) {
            JOptionPane.showMessageDialog(null, Texts.getText(Texts.SPLIT_ZONE_ONLY_FOR_TWO));
            return true;
        }
        if (splitZone(lines, selectLine, zone, toSubCategory)) {
            return true;
        }
        selectPoly = null;
        return false;
    }

    private void splitCurrentZoneIntoSub(ActionEvent evt) {
        splitCurrentZone(true);
    }

    private void mergeSelectedZones(ActionEvent evt) {
        //TODO: 5 (Marcus) implement merging zones;
        throw new UnsupportedOperationException("Merging zones is not handled yet.");
    }

    private void formFocusGained(java.awt.event.FocusEvent evt) {
        System.out.println("formFocusGained");
        getSelectedZones().clear();
        gui.updateSharedPanelItems();
        try {
            bi = gui.getImage();
            setImage(bi);
            getPanel().setZoom(gui.getZoom());
            gui.updateSharedPanelItems();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not load the image from the selected graphic\n" + gui.graphic.getURL() + "\n"
                            + "Reason given:\n" + e.getMessage(), "Loading error",
                    JOptionPane.ERROR_MESSAGE
            );
            gui.tabbedPane.setSelectedIndex(2);
        }
    }

    private void generateRectangles(ActionEvent evt) {
        if (getSelectedZones().size() != 1) {
            JOptionPane.showMessageDialog(null, "Please select a single Text or Comment Area.");
            return;
        }
        TeiZone zone = getFirstSelectedZone();
        if (!(zone instanceof TeiTextZone)) {
            JOptionPane.showMessageDialog(null, "Line segmentation only works on Text or Comment Areas.");
            return;
        }
        TeiTextZone selectedZone = (TeiTextZone) zone;
        if (!deleteLinesIfAny(selectedZone)) {
            return;
        }

//        WaitingDialogForDIVAServices d =
        new WaitingDialogForDIVAServices(this, "Contacting DIVAServices. Please wait...") {
            @Override
            public String method() {
                doRequestRectangles(selectedZone);
                return null;
            }
        };
        this.getSelectedZones().clear();
        repaint();
    }

    private void doRequestRectangles(TeiTextZone selectedZone) {
        logger.debug("Requesting lines (rectangle)");
        Rectangle rect = selectedZone.getArea().getShape().getBounds();
        DivaServicesCommunicator communicator = new DivaServicesCommunicator("http://divaservices.unifr.ch/api/v1");
        DivaServicesResponse<Rectangle> resp = communicator.runHistogramTextLineExtraction(bi, rect);
        List<Rectangle> rects = resp.getHighlighter().getData();

        for (Rectangle r : rects) {
            selectedZone.addTextSegZone(new TeiAreaRect(r));
        }

        logger.debug("The selected zone has " + selectedZone.getTextSegZones().size() + " text segments");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean deleteLinesIfAny(TeiTextZone selectedZone) throws HeadlessException {
        if (selectedZone.getNumberOfChildZones() > 0) {
            int sel = JOptionPane.showConfirmDialog(this, "This area has already text lines.\n"
                            + "An automatic text line Segmentation would delete all existing text lines and references.\n"
                            + "This would also result in loosing possible references from the <text> elements\n"
                            + "Do you want to continue?",
                    "Delete existing lines?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.CANCEL_OPTION) {
                return false;
            }
            selectedZone.removeTextSegZones();
        }
        return true;
    }

    private void generatePolygons(ActionEvent evt) {
        if (getSelectedZones().size() != 1) {
            JOptionPane.showMessageDialog(null, "Please select a single Text or Comment Area.");
            return;
        }
        TeiZone zone = getFirstSelectedZone();
        if (!(zone instanceof TeiTextZone)) {
            JOptionPane.showMessageDialog(null, "Line segmentation only works on Text or Comment Areas.");
            return;
        }
        TeiTextZone selectedZone = (TeiTextZone) zone;
        if (!deleteLinesIfAny(selectedZone)) {
            return;
        }
//        WaitingDialogForDIVAServices d =
        new WaitingDialogForDIVAServices(this, "Contacting DIVAServices. Please wait...") {
            @Override
            public String method() {
                doRequestPolygons(selectedZone);
                return null;
            }
        };

        this.getSelectedZones().clear();
    }
    
    private void generateDanielPolygons(ActionEvent evt) {
        if (getSelectedZones().size() != 1) {
            JOptionPane.showMessageDialog(null, "Please select a single Text or Comment Area.");
            return;
        }
        TeiZone zone = getFirstSelectedZone();
        if (!(zone instanceof TeiTextZone)) {
            JOptionPane.showMessageDialog(null, "Line segmentation only works on Text or Comment Areas.");
            return;
        }
        TeiTextZone selectedZone = (TeiTextZone) zone;
        if (!deleteLinesIfAny(selectedZone)) {
            return;
        }
        
        new WaitingDialogForDIVAServices(this, "Contacting DIVAServices. Please wait...") {
            @Override
            public String method() {
                doRequestDanielPolygons(selectedZone);
                return null;
            }
        };

        this.getSelectedZones().clear();
    }
    
    private void generateDanielPolygons2(ActionEvent evt) {
        if (getSelectedZones().size() != 1) {
            JOptionPane.showMessageDialog(null, "Please select a single Text or Comment Area.");
            return;
        }
        TeiZone zone = getFirstSelectedZone();
        if (!(zone instanceof TeiTextZone)) {
            JOptionPane.showMessageDialog(null, "Line segmentation only works on Text or Comment Areas.");
            return;
        }
        TeiTextZone selectedZone = (TeiTextZone) zone;
        if (!deleteLinesIfAny(selectedZone)) {
            return;
        }
        
        new WaitingDialogForDIVAServices(this, "Contacting DIVAServices. Please wait...") {
            @Override
            public String method() {
                doRequestDanielPolygons2(selectedZone);
                return null;
            }
        };

        this.getSelectedZones().clear();
    }

    private void doRequestPolygons(TeiTextZone selectedZone) {
        logger.debug("Requesting lines (polygon)");
        Rectangle r = selectedZone.getArea().getShape().getBounds();

        DivaServicesCommunicator communicator = new DivaServicesCommunicator("http://divaservices.unifr.ch/api/v1");
        DivaServicesResponse<Polygon> resp = communicator.runSeamCarvingTextlineExtraction(bi, r, 0.0003f, 3.0f, 4, false);
//        List<Polygon> polygons = resp.getHighlighter().getData();

        for (Polygon p : resp.getHighlighter()) {
            selectedZone.addTextSegZone(new TeiAreaPoly(p));
        }
        
        repaint();

        logger.debug("The selected zone has " + selectedZone.getTextSegZones().size() + " text segments");
    }
    
    private void doRequestDanielPolygons(TeiTextZone selectedZone) {
        logger.debug("Requesting lines (polygon)");
        Rectangle boundingBox = selectedZone.getArea().getShape().getBounds();

        if (ds2ImageName==null) {
            try {
                ds2ImageName = APIv2.getImageName(bi);
            } catch (UnirestException ex) {
                JOptionPane.showMessageDialog(null, "Failed to send the image to the DivaServices. Reason:\n"+ex.getMessage());
                return;
            }
        }
        
        PolyListResult res = null;
        
        try {
            res = APIv2.extractPolygons(ds2ImageName, 6, 0.0007, 0.12, 3, boundingBox);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to request polygons. Reason:\n"+ex.getMessage());
            return;
        }
        
        List<Polygon> polys = null;
        
        try {
            polys = res.getResult();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to receive polygons. Reason:\n"+ex.getMessage());
            return;
        }
        
        for (Polygon p : polys) {
            selectedZone.addTextSegZone(new TeiAreaPoly(p));
        }
        
        repaint();

        logger.debug("The selected zone has " + selectedZone.getTextSegZones().size() + " text segments");
    }
    
    private void doRequestDanielPolygons2(TeiTextZone selectedZone) {
        logger.debug("Requesting lines (polygon)");
        Rectangle boundingBox = selectedZone.getArea().getShape().getBounds();
        
        int targWidth = 1024;
        BufferedImage targ;
        float ratioX;
        float ratioY;
        if (bi.getWidth()>targWidth) {
            ratioX  = targWidth / (float)bi.getWidth();
            int newWidth  = Math.round(ratioX * bi.getWidth());
            int newHeight = Math.round(ratioX * bi.getHeight());
            ratioX        = newWidth / (float)bi.getWidth();
            ratioY        = newHeight / (float)bi.getHeight();
            System.out.println("Rescaling ratios: ["+ratioX+", "+ratioY+"]");
            targ = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            targ.getGraphics().drawImage(bi, 0, 0, newWidth, newHeight, 0, 0, bi.getWidth(), bi.getHeight(), null);
            boundingBox.x = (int)(boundingBox.x * ratioX);
            boundingBox.y = (int)(boundingBox.y * ratioY);
            boundingBox.width  = (int)Math.round(Math.ceil(boundingBox.width*ratioX));
            boundingBox.height = (int)Math.round(Math.ceil(boundingBox.height*ratioY));
        } else {
            targ   = bi;
            ratioX = 1;
            ratioY = 1;
        }
        
        
        if (ds2ImageName==null) {
            try {
                ds2ImageName = APIv2.getImageName(targ, "jpg");
            } catch (UnirestException ex) {
                JOptionPane.showMessageDialog(null, "Failed to send the image to the DivaServices. Reason:\n"+ex.getMessage());
                return;
            }
        }
        
        PolyListResult res = null;
        
        try {
            res = APIv2.extractPolygons(ds2ImageName, 6, 0.0007, 0.12, 3, boundingBox);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to request polygons. Reason:\n"+ex.getMessage());
            return;
        }
        
        List<Polygon> polys = null;
        
        try {
            polys = res.getResult();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to receive polygons. Reason:\n"+ex.getMessage());
            return;
        }
        
        for (Polygon p : polys) {
            for (int i=0; i<p.xpoints.length; i++) {
                p.xpoints[i] = Math.round(p.xpoints[i] / ratioX);
                p.ypoints[i] = Math.round(p.ypoints[i] / ratioY);
            }
            selectedZone.addTextSegZone(new TeiAreaPoly(p));
        }
        
        repaint();

        logger.debug("The selected zone has " + selectedZone.getTextSegZones().size() + " text segments");
    }
}
