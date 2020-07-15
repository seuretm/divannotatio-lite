/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */
package ch.unifr.diva.divannotation;

import ch.unifr.tei.TeiHisDoc;
import ch.unifr.tei.facsimile.surfacegrp.TeiSurfaceGrpFolio;
import ch.unifr.tei.facsimile.surfacegrp.TeiSurfaceGrpPart;
import ch.unifr.tei.facsimile.surfacegrp.surface.TeiGraphic;
import ch.unifr.tei.facsimile.surfacegrp.surface.TeiSurface;
import ch.unifr.tei.teiheader.filedesc.editionstmt.respstmt.TeiRespStmt;
import ch.unifr.diva.divannotation.foliospages.FoliosAndPages;
import ch.unifr.diva.divannotation.opensave.OpenMenu;
import ch.unifr.diva.divannotation.parts.Parts;
import ch.unifr.diva.divannotation.textzone.AreaCreationPanel;
import ch.unifr.diva.divannotation.textzone.HandNotePanel;
import ch.unifr.diva.divannotation.textzone.TranscriptionPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import ch.unifr.diva.divannotation.export.ExportData;
import ch.unifr.diva.divannotation.scribbling.ScribblingPanel;
import ch.unifr.diva.divannotation.scribbling.ScribblingTab;

/**
 * @author Mathias Seuret
 */
public class GUI extends JFrame {

    private static final Logger logger = Logger.getLogger(GUI.class);

    public final JTabbedPane tabbedPane;
    public BufferedImage bi;
    public File currentDirectory = new File(".");
    public File teiFile = new File("");
    public TeiSurfaceGrpPart part = null;
    public TeiSurfaceGrpFolio folio = null;
    public TeiSurface page = null;
    public TeiGraphic graphic = null;
    private String currentImageURL = "";
    private ButtonGroup zoneGroup = new ButtonGroup();
    private TeiRespStmt humanResp;
    // needed: selected zone
    private TeiHisDoc tei;
    private double zoom = 1;
    private int vOffset = 0;
    private int hOffset = 0;
    private JButton zoomIn;
    private JButton zoomOut;
    private JSlider zoomSlider;
    private JRadioButton zoneAll;
    private JRadioButton zoneText;
    private JRadioButton zoneTextSeg;

    int sliderPreviousValue = 100;
    int sliderCurrentValue = 100;

    //protected ScrollableImagePanel ScrollableImagePanel;
    public GUI() {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());

        this.bi = null;
        this.tei = null;

        // this.ScrollableImagePanel= null;
        // Scaling to system zoom
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        int width = getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
        if (width > 2000) {
            setDefaultSize(24);
        }
        this.setSize(800, 600);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        content.add(tabbedPane, BorderLayout.CENTER);
        // content.add(save, BorderLayout.SOUTH);
        JPanel sharedBtns = new JPanel();
        content.add(sharedBtns, BorderLayout.SOUTH);
        JButton save = new JButton("Save TEI file");
        zoomIn = new JButton("Zoom +");
        zoomOut = new JButton("Zoom -");
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 10, 200, 50);
        zoneAll = new JRadioButton("all");
        zoneText = new JRadioButton("text");
        zoneTextSeg = new JRadioButton("text lines");
        sharedBtns.add(save);
        sharedBtns.add(zoomIn);
        sharedBtns.add(zoomOut);
        sharedBtns.add(zoomSlider);
        zoneGroup.add(zoneAll);
        zoneGroup.add(zoneText);
        zoneGroup.add(zoneTextSeg);
        sharedBtns.add(zoneAll);
        sharedBtns.add(zoneText);
        sharedBtns.add(zoneTextSeg);

        this.add(content);
        //this.add(sharedBtns);

        tabbedPane.addTab("Open", new OpenMenu(this));
        tabbedPane.addTab("Pages", new Parts(this));
        tabbedPane.addTab("Text area creation", new AreaCreationPanel(this));
        tabbedPane.addTab("Transcription", new TranscriptionPanel(this));
        tabbedPane.addTab("Export OCR training data", new ExportData(this));
        //tabbedPane.addTab("Folio and pages management", new FoliosAndPages(this));
        //tabbedPane.addTab("Pen-based interaction", new ScribblingTab(this));
        //tabbedPane.addTab("Hand notes management", new HandNotePanel(this));
        //tabbedPane.addTab("Transcription", new TranscriptionPanel(this));
        //tabbedPane.addTab("Export data", new ExportData(this));

//        unlockTabs(1);
        for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
            tabbedPane.setEnabledAt(i, false);
        }
        tabbedPane.setEnabledAt(0, true);
        //tabbedPane.setEnabledAt(6, true);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tabbedPane.getSelectedComponent().dispatchEvent(
                        new FocusEvent(
                                tabbedPane.getSelectedComponent(),
                                FocusEvent.FOCUS_GAINED
                        )
                );
            }
        });

        updateSharedPanelItems();

        save.addActionListener(GUI.this::save);
        zoomIn.addActionListener(GUI.this::zoomInClicked);
        zoomOut.addActionListener(GUI.this::zoomOutClicked);
        zoomSlider.addChangeListener(GUI.this::stateChanged);
        zoneAll.addActionListener(GUI.this::zoneAllClicked);
        zoneText.addActionListener(GUI.this::zoneTextClicked);
        zoneTextSeg.addActionListener(GUI.this::zoneTextSegClicked);

        zoneGroup.setSelected(zoneAll.getModel(), true);

        //Fotini add title - when a file is opened it show the path and the name of the file
        this.setTitle("Divannotation");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        this.setVisible(true);
        logger.trace("GUI is now visible");
    }

    private static void setDefaultSize(int size) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());

        Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
        Object[] keys = keySet.toArray(new Object[keySet.size()]);

        for (Object key : keys) {

            if (key != null && key.toString().toLowerCase().contains("font")) {

                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    font = font.deriveFont((float) size);
                    UIManager.put(key, font);
                }

            }

        }

    }

    private void save(ActionEvent evt) {
        logger.info("Saving the TEI file");
        try {
            tei.write(new FileWriter(teiFile));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        repaint();
    }

    private void zoomInClicked(ActionEvent evt) {
        setZoom(getZoom() * 1.5f);
        if (tabbedPane.getSelectedComponent() instanceof Zoomable) {
            ((Zoomable) tabbedPane.getSelectedComponent()).setZoom(getZoom());
        }
    }

    private void zoomOutClicked(ActionEvent evt) {
        setZoom(getZoom() / 1.5f);
        if (tabbedPane.getSelectedComponent() instanceof Zoomable) {
            ((Zoomable) tabbedPane.getSelectedComponent()).setZoom(getZoom());
        }
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        sliderCurrentValue = source.getValue();

        if (tabbedPane.getSelectedComponent() instanceof Zoomable) {
            ((Zoomable) tabbedPane.getSelectedComponent()).setZoom(((double)sliderCurrentValue) / 100.f);
            sliderPreviousValue = sliderCurrentValue;
        }

    }

    private void zoneAllClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.ALL_ZONES);
        }
    }

    private void zoneTextClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.TEXT_ZONE);
        }
    }

    private void zoneCommentClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.COMMENT_ZONE);
        }
    }

    private void zoneTextSegClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.TEXTSEG_ZONE);
        }
    }

    private void zoneCommentSegClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.COMMENTSEG_ZONE);
        }
    }

    private void zoneDecorationClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.DECO_ZONE);
        }
    }

    private void zoneWordClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.WORD_ZONE);
        }
    }

    private void zoneCharClicked(ActionEvent evt) {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.CHAR_ZONE);
        }
    }

    public void unlockTabs(int nb) {
        if (nb > tabbedPane.getComponentCount()) {
            nb = tabbedPane.getComponentCount();
        }
        for (int i = 0; i < nb; i++) {
            tabbedPane.setEnabledAt(i, true);
        }
        for (int i = nb; i < tabbedPane.getComponentCount()-1; i++) {
            tabbedPane.setEnabledAt(i, false);
        }
        tabbedPane.setEnabledAt(tabbedPane.getComponentCount()-1, nb>1);
    }

    public final void selectZoneType(TeiScrollableImagePanel.ZONE_TYPES t) {
        switch (t) {
            case ALL_ZONES:
                zoneAll.setSelected(true);
                break;
            case TEXT_ZONE:
                zoneText.setSelected(true);
                break;
            case TEXTSEG_ZONE:
                zoneTextSeg.setSelected(true);
                break;
        }
    }
    
    public final void updateSharedPanelItems() {
        if (tabbedPane.getSelectedComponent() instanceof TeiScrollableImagePanel) {
            zoomIn.setVisible(true);
            zoomOut.setVisible(true);
            zoomSlider.setVisible(true);
            TeiScrollableImagePanel panel = (TeiScrollableImagePanel) tabbedPane.getSelectedComponent();
            panel.setOffset(getvOffset(), gethOffset());
            zoneAll.setVisible(true);
            zoneText.setVisible(true);
            zoneTextSeg.setVisible(true);
            if (panel.getSelectableZoneTypes().contains(TeiScrollableImagePanel.ZONE_TYPES.ALL_ZONES)) {
                zoneAll.setEnabled(true);
            } else {
                zoneAll.setEnabled(false);
            }
            if (panel.getSelectableZoneTypes().contains(TeiScrollableImagePanel.ZONE_TYPES.TEXT_ZONE)) {
                zoneText.setEnabled(true);
            } else {
                zoneText.setEnabled(false);
            }
            if (panel.getSelectableZoneTypes().contains(TeiScrollableImagePanel.ZONE_TYPES.TEXTSEG_ZONE)) {
                zoneTextSeg.setEnabled(true);
            } else {
                zoneTextSeg.setEnabled(false);
            }
            if (zoneAll.isSelected()) {
                panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.ALL_ZONES);
            }
            if (zoneText.isSelected()) {
                panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.TEXT_ZONE);
            }
            if (zoneTextSeg.isSelected()) {
                panel.setCurrentZoneType(TeiScrollableImagePanel.ZONE_TYPES.TEXTSEG_ZONE);
            }
            if (null != panel.getCurrentZoneType()) {
                switch (panel.getCurrentZoneType()) {
                    case ALL_ZONES:
                        zoneAll.setSelected(true);
                        break;
                    case TEXT_ZONE:
                        zoneText.setSelected(true);
                        break;
                    case TEXTSEG_ZONE:
                        zoneTextSeg.setSelected(true);
                        break;
                    default:
                        break;
                }
            }
            panel.invalidate();
            panel.repaint();
        } else if (tabbedPane.getSelectedComponent() instanceof Zoomable) {
            zoomIn.setVisible(true);
            zoomOut.setVisible(true);
            zoomSlider.setVisible(true);
        } else {
            zoomIn.setVisible(false);
            zoomOut.setVisible(false);
            zoomSlider.setVisible(false);
            zoneAll.setVisible(false);
            zoneText.setVisible(false);
            zoneTextSeg.setVisible(false);
        }

    }

    public TeiHisDoc getTei() {
        return tei;
    }

    public void setTei(TeiHisDoc tei) {
        this.tei = tei;
    }

    public BufferedImage getImage() throws IOException {
        if (graphic != null && !currentImageURL.equals(graphic.getURL())) {
            bi = graphic.loadImage();
            currentImageURL = graphic.getURL();
        }
        return bi;
    }

    public TeiRespStmt getHumanResp() {
        return humanResp;
    }

    public void setHumanResp(TeiRespStmt resp) {
        humanResp = resp;
    }

    public void setPart(TeiSurfaceGrpPart part) {

    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    private int getvOffset() {
        return vOffset;
    }

    public void setvOffset(int vOffset) {
        this.vOffset = vOffset;
    }

    private int gethOffset() {
        return hOffset;
    }

    public void sethOffset(int hOffset) {
        this.hOffset = hOffset;
    }
}
