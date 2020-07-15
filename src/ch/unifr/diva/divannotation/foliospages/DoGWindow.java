/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.unifr.diva.divannotation.foliospages;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.convolution.FGaussianConvolve;

/**
 *
 * @author ms
 */
public class DoGWindow extends javax.swing.JDialog {
    protected JFrame parent = null;
    public BufferedImage result = null;
    protected BufferedImage input;
    protected int prevX;
    protected int prevY;
    
    /**
     * Creates new form DoGWindow
     */
    public DoGWindow(BufferedImage input, JFrame parent) {
        setModal(true);
        initComponents();
        this.parent = parent;
        this.parent.setEnabled(false);
        this.input = input;
        
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                parent.setEnabled(true);
            }
        });
        
        setVisible(true);
        
        new Thread(new Runnable() {
          public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie){}
            randomizePreviewPosition();
          }
        }).start();
        
    }
    
    private void randomizePreviewPosition() {
        Random rnd = new Random();
        prevX = rnd.nextInt(input.getWidth() - lblImage.getWidth());
        prevY = rnd.nextInt(input.getHeight() - lblImage.getHeight());
        System.out.println(prevX+" "+prevY);
        preview();
    }
    
    private void preview() {
        BufferedImage bi = new BufferedImage(lblImage.getWidth(), lblImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawImage(input, 0, 0, bi.getWidth(), bi.getHeight(), prevX, prevY, prevX+bi.getWidth(), prevY+bi.getHeight(), null);
        bi = binarize(bi, g1(), g2(), th());
        lblImage.setIcon(new ImageIcon(bi));
        repaint();
    }
    
    private float g1() {
        try {
            float res = Float.parseFloat(txtG1.getText());
            return res;
        } catch (Exception e) {
            txtG1.setText("15.0");
            return 15.0f;
        }
    }
    
    private float g2() {
        try {
            float res = Float.parseFloat(txtG2.getText());
            return res;
        } catch (Exception e) {
            txtG1.setText("1.5");
            return 1.5f;
        }
    }
    
    private float th() {
        try {
            float res = Float.parseFloat(txtTh.getText());
            return res;
        } catch (Exception e) {
            txtG1.setText("0.1");
            return 0.1f;
        }
    }
    
    public static BufferedImage binarize(BufferedImage ori, float g1, float g2, float th) {
        
        
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        txtG1 = new javax.swing.JTextField();
        txtG2 = new javax.swing.JTextField();
        txtTh = new javax.swing.JTextField();
        lblImage = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnPreview = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Difference of Gaussians binarization");

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        txtG1.setText("15.0");

        txtG2.setText("1.5");

        txtTh.setText("0.1");

        lblImage.setToolTipText("Click to preview another part of the image");
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });

        jLabel1.setText("Gaussian 1 (increase to keep larger elements)");

        jLabel2.setText("Gaussian 2 (decrease to keep smaller elements)");

        jLabel3.setText("Acceptance threshold");

        btnPreview.setText("Update preview image");
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnPreview)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtG1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                            .addComponent(txtG2)
                            .addComponent(txtTh))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 91, Short.MAX_VALUE))
                    .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtG1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtG2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk)
                    .addComponent(btnPreview))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
        parent.setEnabled(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        randomizePreviewPosition();
    }//GEN-LAST:event_lblImageMouseClicked

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        preview();
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        result = binarize(input, g1(), g2(), th());
        setVisible(false);
        parent.setEnabled(true);
    }//GEN-LAST:event_btnOkActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnPreview;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblImage;
    private javax.swing.JTextField txtG1;
    private javax.swing.JTextField txtG2;
    private javax.swing.JTextField txtTh;
    // End of variables declaration//GEN-END:variables
}