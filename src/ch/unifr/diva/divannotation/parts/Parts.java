/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.parts;

import ch.unifr.tei.facsimile.surfacegrp.TeiSurfaceGrpPart;
import ch.unifr.diva.divannotation.GUI;
import ch.unifr.tei.facsimile.surfacegrp.TeiSurfaceGrpFolio;
import ch.unifr.tei.facsimile.surfacegrp.surface.TeiGraphic;
import ch.unifr.tei.facsimile.surfacegrp.surface.TeiSurface;
import java.io.File;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * @author ms
 */
@SuppressWarnings("FieldCanBeLocal")
public class Parts extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(Parts.class);

    private GUI gui;
    private DefaultListModel<TeiSurfaceGrpPart> listModel = new DefaultListModel<>();

    private DefaultListModel<String> graphics = new DefaultListModel<>();
    
    /**
     * These fields are created by NetBeans Form Editor.
     * WARNING: Do NOT modify this code.
     * The content is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Fields">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnUpdatePage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> lstPages;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    /**
     * Creates new form Parts
     */
    public Parts(GUI g) {
        gui = g;
        initComponents();
        lstPages.setModel(graphics);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "Convert2Lambda", "Anonymous2MethodRef"})
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstPages = new javax.swing.JList<>();
        btnUpdatePage = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });

        jLabel1.setText("Page insertion menu");

        lstPages.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstPagesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstPages);

        btnUpdatePage.setText("Update");
        btnUpdatePage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePageActionPerformed(evt);
            }
        });

        btnBrowse.setText("Add page");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdatePage)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdatePage)
                    .addComponent(btnBrowse))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        if (gui.getTei().getFacsimile().getParts().isEmpty()) {
            gui.getTei().getFacsimile().addPart();
        }
        gui.part = gui.getTei().getFacsimile().getPart(0);
        gui.folio = null;
        gui.page = null;
        gui.graphic = null;
        updatePageList();
    }//GEN-LAST:event_formFocusGained

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

    }//GEN-LAST:event_formFocusLost

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(gui.currentDirectory);

        int c = chooser.showOpenDialog(this);
        if (c == 1) {
            return;
        }

        File f = chooser.getSelectedFile();
        String relative = gui.currentDirectory.toURI().relativize(f.toURI()).getPath();

        gui.folio = gui.part.addFolio();
        gui.page  = gui.folio.addPage();
        gui.page.addGraphic(relative, "Image of the page");
        updatePageList();
        lstPages.setSelectedIndex(graphics.size()-1);
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void lstPagesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstPagesValueChanged
        select();
    }//GEN-LAST:event_lstPagesValueChanged

    private void btnUpdatePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdatePageActionPerformed
        int pos = lstPages.getSelectedIndex();
        if (pos<0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select first a file to modify in the list.", "No selected element",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(gui.currentDirectory);
        int c = chooser.showOpenDialog(this);
        if (c == 1) {
            return;
        }
        File f = chooser.getSelectedFile();
        String relative = gui.currentDirectory.toURI().relativize(f.toURI()).getPath();
        select();
        gui.graphic.setURL(relative);
        updatePageList();
        lstPages.setSelectedIndex(pos);
        select();
    }//GEN-LAST:event_btnUpdatePageActionPerformed

    private void updatePageList() {
        graphics.clear();
        for (TeiSurfaceGrpFolio folio : gui.part.getFolios()) {
            for (TeiSurface page : folio.getPages()) {
                graphics.addElement(page.getGraphics().get(0).getURL());
            }
        }
    }
    
    private void select() {
        String val = lstPages.getSelectedValue();
        gui.folio = null;
        gui.page = null;
        gui.graphic = null;
        for (TeiSurfaceGrpFolio folio : gui.part.getFolios()) {
            for (TeiSurface page : folio.getPages()) {
                if (page.getGraphics().get(0).getURL().equals(val)) {
                    gui.folio = folio;
                    gui.page = page;
                    gui.graphic = page.getGraphics().get(0);
                }
            }
        }
        if (gui.graphic!=null) {
            System.out.println("Selected "+val);
            gui.unlockTabs(3);
        }
    }
    
}