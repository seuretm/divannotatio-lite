/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

/**
 * @author ms
 */
public abstract class ScrollableImagePanel extends JPanel implements ImagePanelOwner, Zoomable {

    protected static final String ACTION_PERFORMED = "ACTION_PERFORMED";
    static final String ACTION_NOTHING = "ACTION_NOTHING";
    private static final Logger logger = Logger.getLogger(ScrollableImagePanel.class);
    protected ImagePanel panel = new ImagePanel(this);
    protected JScrollBar hScroll = new JScrollBar(JScrollBar.HORIZONTAL);
    protected JScrollBar vScroll = new JScrollBar(JScrollBar.VERTICAL);
    protected JPanel topPanel = new JPanel();
    protected JPanel leftPanel = new JPanel();
    protected JPanel bottomPanel = new JPanel();
    protected GUI gui;
    private JPanel centerPanel = new JPanel();
    private int previousMouseX;
    private int previousMouseY;

    protected ScrollableImagePanel(GUI gui) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());

        this.gui = gui;
        vScroll.setVisible(true);
        vScroll.addAdjustmentListener(ae -> {
            panel.setOffset(hScroll.getValue(), vScroll.getValue());
            repaint();
        });
        hScroll.setVisible(true);
        hScroll.addAdjustmentListener(ae -> {
            panel.setOffset(hScroll.getValue(), vScroll.getValue());
            repaint();
        });
        initActionMappings();
        panel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                mouseDraggedEvent(me);
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                previousMouseX = me.getX();
                previousMouseY = me.getY();
            }
        });
        panel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClickedEvent(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedEvent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseReleasedEvent(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        this.setLayout(new BorderLayout());
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(panel, BorderLayout.CENTER);
        centerPanel.add(hScroll, BorderLayout.SOUTH);
        centerPanel.add(vScroll, BorderLayout.EAST);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);
        this.add(leftPanel, BorderLayout.WEST);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.revalidate();

    }

    public GUI getGui() {
        return gui;
    }

    void initActionMappings() {
        initActionMapping(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "scrollUp", this::scrollUp);
        initActionMapping(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "scrollDown", this::scrollDown);
        initActionMapping(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "scrollRight", this::scrollRight);
        initActionMapping(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "scrollLeft", this::scrollLeft);
    }

    void initActionMapping(KeyStroke actionK,
                           String action,
                           Function<ActionEvent, String> f) {
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.apply(e);
            }
        };
        addActionMapping(actionK, action, a);
    }

    private void addActionMapping(KeyStroke actionK, String action, Action scrollAction) {
        panel.getInputMap().put(actionK, action);
        panel.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(actionK, action);
        panel.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(actionK, action);
        panel.getActionMap().put(action, scrollAction);
    }

    private String scrollUp(ActionEvent e) {
        if (panel.getHeight() / panel.getZoom() > 0) {
            vScroll.setValue(Integer.max(vScroll.getValue() - 10, vScroll.getMinimum()));
        }
        return ACTION_PERFORMED;
    }

    private String scrollDown(ActionEvent e) {
        if (panel.getHeight() / panel.getZoom() > 0) {
            vScroll.setValue(Integer.min(vScroll.getValue() + 10, vScroll.getMaximum()));
        }
        return ACTION_PERFORMED;
    }

    private String scrollRight(ActionEvent e) {
        if (panel.getWidth() / panel.getZoom() > 0) {
            hScroll.setValue(Integer.min(hScroll.getValue() + 10, hScroll.getMaximum()));
        }
        return ACTION_PERFORMED;
    }

    private String scrollLeft(ActionEvent e) {
        if (panel.getWidth() / panel.getZoom() > 0) {
            hScroll.setValue(Integer.max(hScroll.getValue() - 10, hScroll.getMinimum()));
        }
        return ACTION_PERFORMED;
    }

    private void updateScrollBars() {
        if (panel.getImage() == null) {
            return;
        }
        hScroll.setMaximum((panel.getImage().getWidth()));
        hScroll.setVisibleAmount((int) (panel.getWidth() / panel.getZoom()));

        vScroll.setMaximum((panel.getImage().getHeight()));
        vScroll.setVisibleAmount((int) (panel.getHeight() / panel.getZoom()));
    }

    int getOffsetX() {
        return panel.getOffsetX();
    }

    /**
     * @return the vertical offset
     */
    int getOffsetY() {
        return panel.getOffsetY();
    }

    /**
     * Sets the offset of the panel
     *
     * @param ox the x offset
     * @param oy the y offset
     */
    public void setOffset(int ox, int oy) {
        panel.setOffset(ox, oy);
    }

    /**
     * @return the zoom value
     */
    public double getZoom() {
//        return gui.zoom;
        return panel.getZoom();
    }

    /**
     * Sets the zoom of the panel, takes a non-null integer as parameter
     *
     * @param zoom the zoom value
     */
    public void setZoom(double zoom) {
        gui.setZoom(zoom);
        panel.setZoom(zoom);
        updateScrollBars();
    }

    /**
     * Turns a relative and zoomed coordinate into absolute coordinate
     *
     * @param x the relative coordinate
     * @return the real X coordinate
     */
    protected int getRealX(int x) {
        return panel.getRealX(x);
    }

    /**
     * Turns a relative and zoomed coordinate into absolute coordinate
     *
     * @param y the relative coordinate
     * @return the real Y coordinated
     */
    protected int getRealY(int y) {
        return panel.getRealY(y);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param x the panel coordinate
     * @return the pixel X coordinate
     */
    int getPanelX(int x) {
        return panel.getPanelX(x);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param y the panel coordinate
     * @return the pixel Y coordinate
     */
    int getPanelY(int y) {
        return panel.getPanelY(y);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return panel.getImage();
    }

    /**
     * Sets the image of the panel
     *
     * @param i the buffered image
     */
    protected void setImage(BufferedImage i) {
        panel.setImage(i);
        updateScrollBars();
    }

    protected ImagePanel getPanel() {
        return panel;
    }

    protected abstract void mouseDraggedEvent(MouseEvent me);

    protected abstract void mouseClickedEvent(MouseEvent me);

    protected abstract void mousePressedEvent(MouseEvent me);

    protected abstract void mouseReleasedEvent(MouseEvent me);
}
