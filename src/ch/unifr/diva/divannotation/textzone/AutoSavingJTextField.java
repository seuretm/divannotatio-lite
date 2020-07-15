/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.textzone;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * AutoSavingJTextField class of the LineSeg project
 *
 * @author Manuel Bouillon <manuel.bouillon@unifr.ch>
 *         date: 2016.06.23
 *         brief: auto-saving JTextField with an integrated DocumentListener
 * @see javax.swing.event.DocumentListener
 */
class AutoSavingJTextField extends JTextField {

    private static final Logger logger = Logger.getLogger(AutoSavingJTextField.class);

    public AutoSavingJTextField() {

        this.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateLabel(e);
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateLabel(e);
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateLabel(e);
                    }

                    private void updateLabel(DocumentEvent e) {
//                    logger.debug("BLARG");
                        fireActionPerformed();
                    }
                }
        );
    }

}
