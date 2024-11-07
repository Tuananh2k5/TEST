/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import javax.swing.Timer;

/**
 *
 * @author Admin
 */
public class Splash extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public Splash() {
        initComponents();
        startbutton.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        LoadingBar = new javax.swing.JLabel();
        bar = new javax.swing.JProgressBar();
        LoadingValue = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        startbutton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(102, 102, 0));
        jTextField1.setFont(new java.awt.Font("Georgia", 2, 36)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(204, 255, 102));
        jTextField1.setText("The Knowledge Library");
        jTextField1.setDisabledTextColor(new java.awt.Color(204, 255, 102));
        jTextField1.setFocusable(false);
        jTextField1.setRequestFocusEnabled(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, -1, 50));

        LoadingBar.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        getContentPane().add(LoadingBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 160, 20));

        bar.setBackground(new java.awt.Color(255, 255, 255));
        bar.setForeground(new java.awt.Color(255, 0, 0));
        getContentPane().add(bar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 670, 20));

        LoadingValue.setBackground(new java.awt.Color(255, 255, 255));
        LoadingValue.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        getContentPane().add(LoadingValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 400, 50, 30));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/Loading_1_-removebg-preview.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, 290, 190));

        startbutton.setBackground(new java.awt.Color(255, 255, 102));
        startbutton.setFont(new java.awt.Font("Georgia", 1, 36)); // NOI18N
        startbutton.setForeground(new java.awt.Color(51, 51, 51));
        startbutton.setText("Start");
        startbutton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startbuttonMouseClicked(evt);
            }
        });
        startbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startbuttonActionPerformed(evt);
            }
        });
        getContentPane().add(startbutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 330, 140, 50));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/Splash.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 670, 460));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void startbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startbuttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startbuttonActionPerformed

    private void startbuttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startbuttonMouseClicked
        // TODO add your handling code here:
        new Login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_startbuttonMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            Splash sp = new Splash();
            sp.setVisible(true);
            
            // Create a timer that updates the progress bar
            Timer timer = new Timer(100, null);
            timer.addActionListener(e -> {
                int progress = sp.bar.getValue();

                if (progress < 100) { // Change to "< 100" to allow reaching 100
                    switch (progress) {
                        case 10 -> sp.LoadingBar.setText("Turning On Modules...");
                        case 20 -> sp.LoadingBar.setText("Loading Modules...");
                        case 50 -> sp.LoadingBar.setText("Connecting to Database...");
                        case 70 -> sp.LoadingBar.setText("Connection Successful");
                        case 80 -> sp.LoadingBar.setText("Launching Application...");
                    }

                    sp.bar.setValue(++progress); // Increment the progress
                    sp.LoadingValue.setText(progress + "%");
                } else {
                    timer.stop(); // Stop the timer
                    sp.LoadingBar.setText("Ready for Launch");
                    sp.LoadingValue.setText("");
                    sp.startbutton.setVisible(true);
                }
            });

            sp.bar.setValue(0); // Initialize the progress bar to 0
            timer.start(); // Start the timer
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LoadingBar;
    private javax.swing.JLabel LoadingValue;
    private javax.swing.JProgressBar bar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton startbutton;
    // End of variables declaration//GEN-END:variables
}