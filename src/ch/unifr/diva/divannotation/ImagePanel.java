/* ****************************************************************************************************************** *
 * Copyright HisDoc 2.0 Project                                                                                       *
 *                                                                                                                    *
 * Copyright (c) University of Fribourg, 2015                                                                         *
 *                                                                                                                    *
 * @author: Angelika Garz                                                                                             *
 *          angelika.garz@unifr.ch                                                                                    *
 *          http://diuf.unifr.ch/main/diva/home/people/angelika-garz                                                  *
 * ****************************************************************************************************************** */

package ch.unifr.diva.divannotation;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Mathias Seuret
 */
public class ImagePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(ImagePanel.class);

    private double zoom = 1;
    private BufferedImage image = null;
    private int offsetX = 0;
    private int offsetY = 0;

    private ImagePanelOwner owner;

    public ImagePanel(ImagePanelOwner owner) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        this.owner = owner;
    }

    /**
     * @return the horizontal offset
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * @return the vertical offset
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Displays the panel
     *
     * @param g the {@link Graphics}
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getImage() == null) {
            return;
        }
//        logger.trace("<" + getWidth() + "><" + g.getClip().getBounds().width + ">");
        //g.drawImage(image, 0, 0, getWidth(), getHeight(), offsetX, offsetY, offsetX+getWidth()/zoom, offsetY+getHeight()/zoom, Color.black, null);
        paintToGraphics(g, true);
    }

    /**
     * zoom bug fixed
     *
     * @param g the {@link Graphics}
     * @param useZoomAndOffset true if use zoom and offset
     */
    private void paintToGraphics(Graphics g, boolean useZoomAndOffset) {
        g.drawImage(
                getImage(),
                0,
                0,
                g.getClip().getBounds().width,
                g.getClip().getBounds().height,
                offsetX,
                offsetY,
                (int) (offsetX + g.getClip().getBounds().width / zoom),
                (int) (offsetY + g.getClip().getBounds().height / zoom),
                Color.black,
                null
        );
        owner.paintPanel((Graphics2D) g);
    }

    /**
     * Sets the offset of the panel
     *
     * @param ox x offset
     * @param oy y offset
     */
    public void setOffset(int ox, int oy) {
        logger.trace("Setting offset: " + ox + "," + oy);
        offsetX = ox;
        offsetY = oy;
    }

    /**
     * @return the zoom value
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Sets the zoom of the panel, takes a non-null
     * integer as parameter
     *
     * @param zoom the zoom value
     */
    public void setZoom(double zoom) {
        if (zoom <= 0) {
            return;
        }
        //int centerX = offsetX + (int) (getWidth() / this.zoom / 2);
        //int centerY = offsetY + (int) (getHeight() / this.zoom / 2);
        this.zoom = zoom;
        //setOffset(
        //        centerX - (int) (getWidth() / this.zoom / 2),
        //        centerY - (int) (getHeight() / this.zoom / 2)
        //         );
    }

    /**
     * Turns a relative and zoomed coordinate into absolute
     * coordinate
     *
     * @param x the relative and zoomed coordinate
     * @return the real X coordinate
     */
    public int getRealX(int x) {
        return (int) (x / zoom + offsetX);
    }

    /**
     * Turns a relative and zoomed coordinate into absolute
     * coordinate
     *
     * @param y the relative and zoomed coordinate
     * @return the real Y coordinated
     */
    public int getRealY(int y) {
        return (int) (y / zoom + offsetY);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param x the panel coordinate
     * @return the pixel X coordinate
     */
    public int getPanelX(int x) {
        return (int) ((x - offsetX) * zoom);
    }

    /**
     * Returns the position of a coordinate in the panel
     *
     * @param y the panel coordinate
     * @return the pixel Y coordinate
     */
    public int getPanelY(int y) {
        return (int) ((y - offsetY) * zoom);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image of the panel
     *
     * @param i the buffered image
     */
    public void setImage(BufferedImage i) {
        image = i;
    }

}