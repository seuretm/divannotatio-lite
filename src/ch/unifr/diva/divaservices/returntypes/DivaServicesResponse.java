/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divaservices.returntypes;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Class encapsulating an image and output information from DivaServices
 *
 * @author Marcel Würsch
 *         marcel.wuersch@unifr.ch
 *         http://diuf.unifr.ch/main/diva/home/people/marcel-w%C3%BCrsch
 *         Created on: 08.10.2015.
 */
public class DivaServicesResponse<T> {

    private static final Logger logger = Logger.getLogger(DivaServicesResponse.class);

    /**
     * extracted image
     */
    private BufferedImage image;
    /**
     * extracted outputs
     */
    private Map<String, Object> output;
    /**
     * extracted highlighters
     */
    private IHighlighter<T> highlighter;

    /**
     * @param image       the result image
     * @param output      the contents of "output"
     * @param highlighter the extracted highlighter information
     */
    public DivaServicesResponse(BufferedImage image, Map<String, Object> output, IHighlighter<T> highlighter) {
        this.image = image;
        this.output = output;
        this.highlighter = highlighter;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public IHighlighter<T> getHighlighter() {
        return highlighter;
    }
}
