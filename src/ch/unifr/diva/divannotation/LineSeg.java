/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation;

import org.apache.log4j.Logger;
import org.jdom2.JDOMException;

import java.io.IOException;
import org.apache.log4j.varia.NullAppender;

/**
 * @author ms
 */
public class LineSeg {

    /**
     * This is a UI-setting. When selecting a Point of a polygon one should be at least that close.
     */
    public static final double MAX_DISTANCE_TO_POINT_FOR_POINT_SELECTION = 30.0;
    private static final Logger logger = Logger.getLogger(LineSeg.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JDOMException {
        logger.info("Starting annotation tool GUI");
        new GUI();
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new NullAppender());
    }

}

