/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.opensave;

import ch.unifr.tei.TeiHisDoc;
import ch.unifr.tei.teiheader.filedesc.editionstmt.respstmt.TeiRespStmt;
import ch.unifr.diva.divannotation.GUI;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author ms
 */
@SuppressWarnings("FieldCanBeLocal")
public class OpenMenu extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(OpenMenu.class);

    private GUI gui;

    /**
     * These fields are created by NetBeans Form Editor.
     * WARNING: Do NOT modify this code.
     * The content is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Fields">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jtfFilename;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>


    /**
     * Creates new form OpenMenu
     */
    public OpenMenu(GUI gui) {
        this.gui = gui;
        initComponents();
        gui.updateSharedPanelItems();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "Convert2Lambda", "Anonymous2MethodRef"})
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtfFilename = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jtfFilename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfFilenameActionPerformed(evt);
            }
        });

        jButton1.setText("Open");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Create a new file");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("If you have already a file with some data:");

        jLabel2.setText("If you want to create a new (empty) file:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtfFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 587, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(341, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtfFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfFilenameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfFilenameActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(gui.currentDirectory);
        chooser.setSelectedFile(new File("output.xml"));
        FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
                "xml files (*.xml)", "xml"
        );

        chooser.setDialogTitle("Select a TEI-HisDoc file");
        // set selected filter
        chooser.setFileFilter(xmlfilter);
        

        int c = chooser.showSaveDialog(this);
        if (c == 1) {
            return;
        }

        TeiRespStmt resp = TeiRespStmt.person(
                "anonymous@mail.com",
                "anonymous",
                "anonymous",
                "transcriber",
                "Transcription"
        );
        gui.setHumanResp(resp);

        TeiHisDoc tei = new TeiHisDoc(resp);

        gui.setTei(tei);

        gui.unlockTabs(2);

        gui.currentDirectory = chooser.getCurrentDirectory();
        gui.teiFile = chooser.getSelectedFile();
        if (!gui.teiFile.getAbsolutePath().endsWith(".xml")) {
            gui.teiFile = new File(gui.teiFile.getAbsolutePath()+".xml");
        }

        tei.changeDirectory(gui.currentDirectory.getAbsolutePath());
        //Fotini the opened file is visible at the title
        gui.setTitle(gui.teiFile.toString());

        logger.info("The TEI file will be saved to " + gui.teiFile);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(gui.currentDirectory);
        FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
                "xml files (*.xml)", "xml"
        );

        chooser.setDialogTitle("Select a TEI-HisDoc file");
        // set selected filter
        chooser.setFileFilter(xmlfilter);
        
        int c = chooser.showOpenDialog(this);
        if (c == 1) {
            return;
        }

        TeiRespStmt resp = TeiRespStmt.person(
                "anonymous@mail.com",
                "anonymous",
                "anonymous",
                "transcriber",
                "Transcription"
        );
        TeiHisDoc thd = new TeiHisDoc(resp);

        try {
            thd.load(chooser.getSelectedFile().getAbsolutePath());
        } catch (JDOMException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "The selected file does not contain valid XML, it cannot be loaded:\n"+ex.getMessage()
                    , "Loading error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "The selected file could not be read."
                    , "Loading error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        File f = chooser.getSelectedFile();
        String relative = gui.currentDirectory.toURI().relativize(f.toURI()).getPath();
        this.jtfFilename.setText(relative);

        gui.setHumanResp(resp);
        gui.setTei(thd);
        gui.unlockTabs(2);
        gui.currentDirectory = chooser.getCurrentDirectory();
        gui.teiFile = chooser.getSelectedFile();
        thd.changeDirectory(gui.currentDirectory.getAbsolutePath());

        //Fotini the opened file is visible at the title
        gui.setTitle(gui.teiFile.toString());

    }//GEN-LAST:event_jButton1ActionPerformed
    // End of variables declaration                   
}
