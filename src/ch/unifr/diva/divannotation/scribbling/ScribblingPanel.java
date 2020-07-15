/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.unifr.diva.divannotation.scribbling;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;
import ch.unifr.diva.divannotation.GUI;
import ch.unifr.diva.divannotation.ScrollableImagePanel;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Mathias Seuret
 */
public class ScribblingPanel extends ScrollableImagePanel {
    private static final Logger logger = Logger.getLogger(GUI.class);
    
    protected BufferedImage baseImage = null;
    
    protected int prevX = -1;
    protected int prevY = -1;
    
    protected byte[][] mask;
    protected int[][] scribbling = null;
    
    protected int mouseButton = 0;
    
    protected static final int FILL_COLOR = 0xFFFFFF;
    protected static final int DRAW_COLOR = 0xFF0000;
    protected static final int ERASE_COLOR = 0x000000;
    protected static final int REPLACE_COLOR = 0xDCC8B4;
    protected static final int NO_COLOR = 0xC0FFEE;
    
    protected int opacity = 100;
    
    public ScribblingPanel(GUI gui) {
        super(gui);
    }

    @Override
    public void mouseDraggedEvent(MouseEvent me) {
        if (scribbling==null) {
            return;
        }
        
        int x = getRealX(me.getX());
        int y = getRealY(me.getY());
        
        int color = DRAW_COLOR; // default
        if (mouseButton==3 || me.isControlDown()) { // right / ctrl
            color = ERASE_COLOR;
        }
        
        uglyLine(getImage(), scribbling, prevX, prevY, x, y, 3, color, opacity);
        
        prevX = x;
        prevY = y;
        repaint();
    }

    @Override
    public void mouseClickedEvent(MouseEvent me) {
        // Nothing to do
    }

    @Override
    public void mousePressedEvent(MouseEvent me) {
        if (!me.isShiftDown()) {
            prevX = getRealX(me.getX());
            prevY = getRealY(me.getY());
        }
        mouseButton = me.getButton();
        
        if (me.isShiftDown()) {
            mouseDraggedEvent(me);
        }
    }

    @Override
    public void mouseReleasedEvent(MouseEvent me) {
        if (mouseButton==3) {
            unfill();
        }
        floodFill();
        repaint();
    }

    @Override
    public void paintPanel(Graphics2D g) {
        // Nothing to do yet
    }
    
    public void setBinaryMask(byte[][] b) {
        mask = b;
    }
    
    public int[][] getBinaryData() {
        return scribbling;
    }
    
    public void setBaseImage(BufferedImage bi) {
        baseImage = bi;
        scribbling = new int[bi.getWidth()][bi.getHeight()];
        for (int x=0; x<scribbling.length; x++) {
            for (int y=0; y<scribbling[x].length; y++) {
                scribbling[x][y] = NO_COLOR;
            }
        }
    }
    
    public void setImage(BufferedImage bi) {
        super.setImage(bi);
        scribbling = new int[bi.getWidth()][bi.getHeight()];
        for (int x=0; x<scribbling.length; x++) {
            for (int y=0; y<scribbling[x].length; y++) {
                scribbling[x][y] = NO_COLOR;
            }
        }
    }
    
    private void floodFill() {
        logger.debug("Starting flood fill");
        
        int nb = 0;
        BufferedImage bi = getImage();
        for (int ox=0; ox<bi.getWidth(); ox++) {
            for (int oy=0; oy<bi.getHeight(); oy++) {
                //int rgb = bi.getRGB(ox, oy) & 0xFFFFFF; // old
                int rgb = scribbling[ox][oy];
                if (rgb==0xFF0000) {
                    floodFill(ox, oy);
                }
                if (mask[ox][oy]==1) {
                    nb++;
                }
            }
        }
        repaint();
        logger.debug(nb+" mask");
        logger.debug("Floodfill done");
    }
    
    private void floodFill(int x, int y) {
        BufferedImage bi = getImage();
        
        Stack<Integer> stack = new Stack<>();
        stack.push(x);
        stack.push(y);
        
        while (!stack.isEmpty()) {
            y = stack.pop();
            x = stack.pop();
            if (x<0 || y<0 || x>=bi.getWidth() || y>=bi.getHeight()) {
                continue;
            }
            //int rgb = bi.getRGB(x, y) & 0xFFFFFF; // old
            int rgb = scribbling[x][y];
            //System.out.println(Integer.toHexString(rgb));
            
            if ((mask[x][y]==1 && rgb!=DRAW_COLOR) || rgb==FILL_COLOR || rgb==ERASE_COLOR) {
                continue;
            }
            
            if (rgb!=DRAW_COLOR) {
                bi.setRGB(x, y, FILL_COLOR);
                scribbling[x][y] = FILL_COLOR;
            }
            
            for (int dx=-1; dx<=1; dx++) {
                for (int dy=-1; dy<=1; dy++) {
                    if (dx==0 && dy==0) {
                        continue;
                    }
                    //rgb = bi.getRGB(x+dx, y+dy) & 0xFFFFFF;
                    rgb = scribbling[x+dx][y+dy];
                    if (rgb==DRAW_COLOR) {
                        continue;
                    }
                    stack.push(x+dx);
                    stack.push(y+dy);
                }
            }
        }
    }
    
    private void unfill() {
        logger.debug("Unfilling foreground pixels");
        BufferedImage bi = getImage();
        for (int ox=0; ox<bi.getWidth(); ox++) {
            for (int oy=0; oy<bi.getHeight(); oy++) {
                int rgb = bi.getRGB(ox, oy) & 0xFFFFFF;
                if (rgb==FILL_COLOR) {
                    bi.setRGB(ox, oy, baseImage.getRGB(ox, oy));
                    scribbling[ox][oy] = NO_COLOR;
                }
            }
        }
    }
    
    public static void uglyLine(BufferedImage bi, int[][] binary, int sx, int sy, int ex, int ey, int size, int color, int opacity) {
        float x = sx;
        float y = sy;
        int dx = ex - sx;
        int dy = ey - sy;

        float ol = (float) Math.sqrt(dx * dx + dy * dy);
        float ox = dx * size / (2 * ol);
        float oy = dy * size / (2 * ol);

        int n = (int) (ol / Math.sqrt(ox * ox + oy * oy));

        do {
            combineSqr(bi, binary, (int) x, (int) y, size, color, opacity);
            x += ox;
            y += oy;
        } while (--n > 0);

    }

    private static void combineSqr(BufferedImage bi, int[][] binary, int cx, int cy, int size, int color, int opacity) {
        for (int px = cx - size / 2; px <= cx + size / 2; px++) {
            for (int py = cy - size / 2; py <= cy + size / 2; py++) {
                if (binary[px][py]==color) {
                    continue;
                }
                try {
                    bi.setRGB(px, py, mixRGB(color, bi.getRGB(px,py), opacity));
                    binary[px][py] = color;
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }
        // please forgive me for this
    }

    public void setOpacity(int value) {
        opacity = value;
    }
    
    public static int mixRGB(int rgb1, int rgb2, int opacity) {
        int res = 0;
        for (int ch=0; ch<3; ch++) {
            int a = (rgb1>>(8*ch)) & 0xFF;
            int b = (rgb2>>(8*ch)) & 0xFF;
            int c = ((opacity*a + (100-opacity)*b) / 100) & 0xFF;
            res |= c << (8*ch);
        }
        return res;
    }
}
