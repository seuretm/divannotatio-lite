/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divaservices.returntypes;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * @author Marcel Würsch
 *         marcel.wuersch@unifr.ch
 *         http://diuf.unifr.ch/main/diva/home/people/marcel-w%C3%BCrsch
 *         Created on: 08.10.2015.
 */
public abstract class IHighlighter<T> implements Iterable<T> {

    private static final Logger logger = Logger.getLogger(IHighlighter.class);

    private List<T> data;

    IHighlighter(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }
}
