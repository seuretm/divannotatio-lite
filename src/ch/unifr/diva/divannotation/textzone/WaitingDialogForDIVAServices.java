/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.textzone;

import ch.unifr.diva.divannotation.TeiScrollableImagePanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * @author liwicki
 */
abstract class WaitingDialogForDIVAServices {

    private static final Logger logger = Logger.getLogger(WaitingDialogForDIVAServices.class);

    WaitingDialogForDIVAServices(TeiScrollableImagePanel panel, String message) {
        logger.trace(Thread.currentThread().getStackTrace()[1].getMethodName());

        final JDialog loading = new JDialog(panel.getGui());
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(new JLabel(message), BorderLayout.CENTER);
        loading.setUndecorated(true);
        loading.getContentPane().add(p1);
        loading.pack();
        loading.setLocationRelativeTo(panel.getGui());
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loading.setModal(true);
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                method();
                return null;
            }

            @Override
            protected void done() {
                loading.dispose();
                super.done(); //To change body of generated methods, choose Tools | Templates.
            }
        };
        worker.execute();
        loading.setVisible(true);
        try {
            worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public abstract String method();

}
