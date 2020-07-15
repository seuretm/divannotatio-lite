/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.unifr.diva.divannotation.scribbling;

import ch.unifr.diva.divaservices.DivaServicesCommunicator;
import ch.unifr.diva.divaservices.returntypes.DivaServicesResponse;
import ch.unifr.tei.facsimile.surfacegrp.surface.zone.TeiTextSegZone;
import ch.unifr.tei.facsimile.surfacegrp.surface.zone.TeiTextZone;
import ch.unifr.tei.facsimile.surfacegrp.surface.zone.TeiZoneType;
import ch.unifr.tei.utils.TeiAreaPoly;
import ch.unifr.tei.utils.TeiAreaRect;
import ch.unifr.tei.utils.TeiAreaUnlocalized;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ch.unifr.diva.divannotation.GUI;
import ch.unifr.diva.divannotation.TeiScrollableImagePanel;
import ch.unifr.diva.divannotation.Zoomable;
import static ch.unifr.diva.divannotation.scribbling.ScribblingPanel.mixRGB;
import ch.unifr.diva.divannotation.textzone.AreaCreationPanel;
import static ch.unifr.diva.divannotation.textzone.AreaCreationPanel.getOverlappingZone;
import org.apache.log4j.Logger;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.convolution.FGaussianConvolve;

/**
 *
 * @author Mathias Seuret
 */
public class ScribblingTab extends JPanel implements Zoomable {
    private static final Logger logger = Logger.getLogger(GUI.class);
    protected GUI gui;
    
    protected String graphicPath = "";
    protected BufferedImage baseImage = null;
    protected byte[][] baseBin = null;
    protected int[][] annotations = null;
    protected BufferedImage polyImage = null;
    
    protected JButton btnClear = new JButton("Clear scribblings");
    protected JButton btnText = new JButton("Create new text line");
    protected JButton btnComment = new JButton("Create new comment line");
    
    protected JSlider opacitySlider = new JSlider(0, 100, 100);
    
    protected ScribblingPanel scribblingPanel;
    
    public ScribblingTab(GUI gui) {
        
        this.gui = gui;
        
        this.setLayout(new BorderLayout());
        
        JPanel leftMenu = new JPanel();
        this.add(leftMenu, BorderLayout.WEST);
        leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.Y_AXIS));
        
        leftMenu.add(btnClear);
        leftMenu.add(new JLabel(" "));
        leftMenu.add(btnText);
        leftMenu.add(btnComment);
        
        leftMenu.add(new JLabel(" "));
        leftMenu.add(new JLabel("Scribbling opacity"));
        leftMenu.add(opacitySlider);
        
        
        
        scribblingPanel = new ScribblingPanel(gui);
        add(scribblingPanel, BorderLayout.CENTER);
        
        
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                try {
                    formFocusGained(evt);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(ScribblingTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                // Nothing to do
            }
        });
        
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                generatePolyImg();
                repaint();
            }
        });
        
        btnText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                generatePolygon(AreaCreationPanel.ZONE_TYPES.TEXTSEG_ZONE);
            }
            
        });
        
        btnComment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                generatePolygon(AreaCreationPanel.ZONE_TYPES.COMMENTSEG_ZONE);
            }
        });
        
        opacitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                scribblingPanel.setOpacity(opacitySlider.getValue());
            }
        });
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                opacitySlider.setValue(100);
            }
        });
    }
    
    public void formFocusGained(FocusEvent evt) throws IOException {
        logger.debug("ScribblingTab: gained focus");
        
        gui.updateSharedPanelItems();
        
        logger.debug("gui.getImage() = "+gui.getImage());
        if (baseImage != gui.getImage()) {
            baseImage = gui.getImage();
            generateBinary();
            generatePolyImg();
        }
        
        scribblingPanel.setBaseImage(baseImage);
    }

    private void generateBinary() throws IOException {
        logger.debug("ScribblingTab: generating binary image");
        BufferedImage bi = binarize(gui.getImage());
        baseBin = new byte[bi.getWidth()][bi.getHeight()];
        annotations = new int[bi.getWidth()][bi.getHeight()];
        
        int nb = 0;
        for (int x=0; x<bi.getWidth(); x++) {
            for (int y=0; y<bi.getHeight(); y++) {
                int rgb = bi.getRGB(x, y) & 0xFFFFFF;
                if ((rgb&0xFFFFFF) != 0) {
                    baseBin[x][y] = 1;
                    nb++;
                } else {
                    baseBin[x][y] = 0;
                }
            }
        }
        
        scribblingPanel.setBinaryMask(baseBin);
        logger.debug("ScribblingTab: binary image generated, "+nb+" foreground pixels");
    }
    
    private void generatePolyImg() {
        logger.debug("ScribblingTab: starting regenrating poly image");
        if (polyImage==null || polyImage.getWidth()!=baseImage.getWidth() || polyImage.getHeight()!=baseImage.getHeight()) {
            polyImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        polyImage.getGraphics().drawImage(baseImage, 0, 0, null);
        logger.debug("ScribblingTab: poly image created");
        this.scribblingPanel.setImage(polyImage);
    }
    
    public static BufferedImage binarize(BufferedImage ori) {
        float th = 0.1f;
        float g1 = 15f;
        float g2 = 1.5f;
        
        
        FImage fbi = ImageUtilities.createFImage(ori);
        FImage img = fbi.process(new FGaussianConvolve(g1)).subtract(fbi.process(new FGaussianConvolve(g2)));
        img = img.threshold(th);
        BufferedImage bin = ImageUtilities.createBufferedImage(img);
        BufferedImage bi = new BufferedImage(ori.getWidth(), ori.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < ori.getWidth(); x++) {
            for (int y = 0; y < ori.getHeight(); y++) {
                if ((bin.getRGB(x, y)&0xFF)!=0) {
                    bi.setRGB(x, y, 0x000000);
                } else {
                    bi.setRGB(x, y, 0xFFFFFF);
                }
            }
        }
        return bi;
    }
    
    private void generatePolygon(AreaCreationPanel.ZONE_TYPES type) {
        logger.debug("Starting to generate a new zone; type: "+type);
        
        // Looking for where the selected area is
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;
        int[][] binary = scribblingPanel.getBinaryData();
        for (int x=0; x<binary.length; x++) {
            for (int y=0; y<binary[x].length; y++) {
                int rgb = binary[x][y];
                if (rgb!=ScribblingPanel.DRAW_COLOR && rgb!=ScribblingPanel.FILL_COLOR) {
                    continue;
                }
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        
        if (minX==Integer.MAX_VALUE) {
            return;
        }
        
        // the extraction method requires a border
        minX--;
        minY--;
        maxX++;
        maxY++;
        
        // We can extract a small binary image out of this
        BufferedImage bin = new BufferedImage(
                maxX-minX+1,
                maxY-minY+1,
                BufferedImage.TYPE_INT_RGB
        );
        for (int x=minX; x<=maxX; x++) {
            for (int y=minY; y<=maxY; y++) {
                int rgb = 0;
                if (x>=0 && y>=0 && x<binary.length && y<binary[x].length) {
                    rgb = binary[x][y];
                }
                if (rgb!=ScribblingPanel.DRAW_COLOR && rgb!=ScribblingPanel.FILL_COLOR) {
                    bin.setRGB(x-minX, y-minY, 0x000000);
                } else {
                    bin.setRGB(x-minX, y-minY, 0xFFFFFF);
                }
            }
        }
        
        // Contacting the DIVA Services
        DivaServicesCommunicator communicator = new DivaServicesCommunicator("http://divaservices.unifr.ch/api/v1");
        DivaServicesResponse<Polygon> result = communicator.runPolygonExtraction(bin, 7);
        List<Polygon> polygons = result.getHighlighter().getData();
        
        // We cropped, so the points need to be shifted
        for (Polygon p : polygons) {
            offsetPoints(p, minX, minY);
        }
        
        // Decorations can be added directly
        if (type==AreaCreationPanel.ZONE_TYPES.DECO_ZONE) {
            for (Polygon p : polygons) {
                gui.page.addDecorationZone(new TeiAreaPoly(p));
            }
        }
        
        // text lines can be put together into a text block
        if (type==AreaCreationPanel.ZONE_TYPES.TEXTSEG_ZONE) {
            
            for (Polygon p : polygons) {
                TeiTextZone z = getOverlappingZone(gui, new TeiAreaPoly(p), TeiZoneType.MAIN_TEXT);
                Object o = z.addTextSegZone(new TeiAreaPoly(p));
                logger.debug("Adding text seg zone: "+o);
            }
        }
        
        // comment lines can be put together into a text block
        if (type==AreaCreationPanel.ZONE_TYPES.COMMENTSEG_ZONE) {
            for (Polygon p : polygons) {
                TeiTextZone z = getOverlappingZone(gui, new TeiAreaPoly(p), TeiZoneType.COMMENT_TEXT);
                TeiTextSegZone sz = z.addTextSegZone(new TeiAreaPoly(p));
                logger.debug("Adding comment seg zone: "+sz);
            }
        }
        
        // finally, we change the color of what was extracted
        BufferedImage bi = scribblingPanel.getImage();
        for (int x=0; x<bi.getWidth(); x++) {
            for (int y=0; y<bi.getHeight(); y++) {
                int rgb = bi.getRGB(x, y) & 0xFFFFFF;
                if (rgb==ScribblingPanel.DRAW_COLOR || rgb==ScribblingPanel.FILL_COLOR) {
                    bi.setRGB(x, y, mixRGB(ScribblingPanel.REPLACE_COLOR, baseImage.getRGB(x,y), opacitySlider.getValue()));
                }
            }
        }
        repaint();
    }
    
    private static void offsetPoints(Polygon p, int ox, int oy) {
        for (int n=0; n<p.npoints; n++) {
            p.xpoints[n] += ox;
            p.ypoints[n] += oy;
        }
    }

    @Override
    public double getZoom() {
        return scribblingPanel.getZoom();
    }

    @Override
    public void setZoom(double z) {
        scribblingPanel.setZoom(z);
    }
}
