/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divaservices.returntypes;

import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;

/**
 * @author Marcel Würsch
 *         marcel.wuersch@unifr.ch
 *         http://diuf.unifr.ch/main/diva/home/people/marcel-w%C3%BCrsch
 *         Created on: 08.10.2015.
 */
public class PointHighlighter extends IHighlighter<Point> {

    private static final Logger logger = Logger.getLogger(PointHighlighter.class);

    public PointHighlighter(List<Point> data) {
        super(data);
    }
}
