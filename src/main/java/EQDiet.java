/**
 * EQDiet Beta 0.9.
 * (C) 2020-2021, EQDiet.
 * Website: https://eqdiet.weebly.com
 * This program comes with NO WARANTY.
 * Project licensed under the MIT license.
 * See our GitHub repositories for more information at https://github.com/EQDiet
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public final class EQDiet extends javax.swing.JFrame {
    // This block creates most of the variables that will be used in the program
    boolean isSomethingWritten;
    byte foodExists;
    short duration, food, restartSession, sessionNumber;
    long finalKcal, kcal, logQuantity, quantity;
    String fileName, text, dish, foodDB, meal, grams, log, installdir, installdir2;
    
    void StartLog() {
        duration = 1;
        try (FileWriter fwS=new FileWriter("Data/FoodLog.txt");
                BufferedWriter fW = new BufferedWriter(fwS)) {
            while(duration > 0) {
                fW.write("-------------------------------------------------------");
                fW.newLine();
                fW.write("      File generated by EQDiet. (C) 2021, EQDiet.      ");
                fW.newLine();
                fW.write("-------------------------------------------------------");
                fW.newLine();
                fW.write("Session " + sessionNumber + ":");
                jTextArea3.setText("-------------------------------------------------------\n"
                        + "      File generated by EQDiet. (C) 2021, EQDiet.      \n"
                        + "-------------------------------------------------------\n"
                        + "Session " + sessionNumber + ":\n");
                duration = 0;
            }
            fW.close();
        } catch (IOException ex) {
            jLabel9.setText("Error saving food log. Please reinstall application.");
        } 
    }
    
    void StartLogNewSession() {
        duration = 1;
        try (FileWriter fwS=new FileWriter("Data/FoodLog.txt", true);
                BufferedWriter fW = new BufferedWriter(fwS)) {
            while(duration > 0) {
                fW.newLine();
                fW.newLine();
                fW.write("Session " + sessionNumber + ":");
                jTextArea3.setText(jTextArea3.getText() + "\n\nSession " + sessionNumber + ":\n");
                duration = 0;
            }
            fW.close();
        } catch (IOException ex) {
            jLabel9.setText("Error saving food log. Please reinstall application.");
        } 
    }
    
    void WriteLog() {
        duration = 1;
        try (FileWriter flS=new FileWriter("Data/FoodLog.txt", true);
            BufferedWriter fS = new BufferedWriter(flS)) {
            while(duration > 0) {
                fS.newLine();
                fS.write(log + ".");
                jTextArea3.setText(jTextArea3.getText() + log + ".\n");
                duration = 0;
            }
            fS.close();
        } catch (IOException ex) {
            jLabel9.setText("Error saving food log");
        } 
    }
    
    void RestartSession() {
        this.duration = 1;
        jLabel5.setText("");
        jTextField1.setText("");
        jTextField2.setText("");
        jButton2.setEnabled(false);
        jTextField1.setEnabled(true);
        jTextField2.setEnabled(true);
        jButton1.setEnabled(true);
        jLabel5.setText("Welcome to EQDiet");
        jLabel9.setText("An open source healthy diet app");
        duration = 0;
        logQuantity = 0;
        restartSession = 0;
        foodExists = 0;
        isSomethingWritten = false;
        StartLogNewSession();
    }
    
    void CheckForUpdates(double currentVersion) {
        try {
            URL url = new URL("https://dl.bintray.com/eqdiet/app/main/version-data/VersionNumber.txt");
            Scanner sc = new Scanner(url.openStream());
            double newVersion = Double.valueOf(sc.nextLine());
            javax.swing.JFrame topFrame = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
            if (newVersion > currentVersion) {
                int result = JOptionPane.showConfirmDialog(topFrame, "Version " + newVersion + " is available. Would you like to install it now?\nThe previous version will be removed.", "Updates available", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    URL verurl = new URL("https://dl.bintray.com/eqdiet/app/main/version-data/Version.txt");
                    Scanner scanner = new Scanner(verurl.openStream());
                    String newVer = scanner.nextLine();
                    onExit();
                    Download("https://dl.bintray.com/eqdiet/app/main/EQDiet" + newVer + ".jar", "EQDiet" + newVer + ".jar");
                    Download("https://dl.bintray.com/eqdiet/app/updater/EQDietUpdater.jar", "EQDietUpdater.jar");
                    jFrame1.dispose();
                    Runtime update = Runtime.getRuntime();
                    String[] commandToExecute = {"Java/bin/javaw", "-jar", installdir + "EQDietUpdater.jar"};
                    update.exec(commandToExecute);
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(topFrame, "You have the latest version installed!", "No updates available", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.net.MalformedURLException ex) {
            Logger.getLogger(EQDiet.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog((javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), "Error while checking for updates", "Fatal error", JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ex) {
            Logger.getLogger(EQDiet.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog((javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), "Error while checking for updates", "Fatal error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    short onStart() {
        /* Checks if data directory exists (useful for portable versions) */
        java.io.File datadirpath = new java.io.File("Data");
        if (!(datadirpath.exists())) {
            datadirpath.mkdir();
        }
        java.io.File file = new java.io.File("Data/EQDiet.dat");
        java.io.File file2 = new java.io.File("Data/FoodLog.txt");
        short val = 1;
        String sessionText = null;
        if (file.exists()) {
            try (FileReader fR = new FileReader(file);
                    BufferedReader in = new BufferedReader(fR)) {
                val = (short) in.read();
                in.readLine();
                if (file2.exists()) {
                    try (FileReader fR2 = new FileReader(file2);
                            BufferedReader in2 = new BufferedReader(fR2)) {
                        StringBuilder sB = new StringBuilder();
                        int c;
                        while ((c = in2.read()) != -1) {
                            sB.append((char) c);
                        }
                        sB.append("\n");
                        sessionText = sB.toString();
                        in2.close();
                    }
                } else {
                    StartLog();
                    sessionText = jTextArea3.getText();
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(EQDiet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            StartLog();
            sessionText = jTextArea3.getText();
        }
        System.out.println("Welcome to EQDiet");
        System.out.println("An open source healthy diet app");
        jTextArea3.setText(sessionText);
        return val;
    }
    
    void onExit() {
        try (FileWriter fW = new FileWriter("Data/EQDiet.dat");
                BufferedWriter out = new BufferedWriter(fW)) {
            if (isSomethingWritten) {
                out.write(sessionNumber + 1);
            } else {
                out.write(sessionNumber);
            }
            out.newLine();
            out.write((int) logQuantity);
            out.close();
            if (isSomethingWritten && restartSession == 0) {
                try (FileWriter fW2 = new FileWriter("Data/FoodLog.txt", true);
                        BufferedWriter fL = new BufferedWriter(fW2)) {
                    fL.newLine();
                    fL.write("- In total you have eaten " + logQuantity + " kilocalories. -");
                    jTextArea3.setText(jTextArea3.getText() + "- In total you have eaten " + logQuantity + " kilocalories. -");
                    logQuantity = 0;
                }
            }
            if (isSomethingWritten) {
                sessionNumber++;
                RestartSession();
            }
        } catch (IOException ex) {
            Logger.getLogger(EQDiet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void Download(String downloadfile, String downloadname) { // Thanks baeldung on www.baeldung.com for publishing this code
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadfile).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(installdir + "/" + downloadname)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Logger.getLogger(EQDiet.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
   /**
    * goToURL code coded by Antony Garcia Gonzalez. Website: http://panamahitek.com
    * @param URL
    */
    void goToURL(String URL) {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    URI uri = new URI(URL);
                    desktop.browse(uri);
                } catch (URISyntaxException | IOException ex) {
                    Logger.getLogger(EQDiet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Creates new form EQDietBeta08
     */
    public EQDiet() {
        sessionNumber = 1; // This may be overwritten
        this.duration = 1;
        installdir = System.getProperty("user.dir") + "/Data/";
        initComponents();
        ImageIcon img = new ImageIcon(getClass().getResource("EQDiet.png"));
        this.setIconImage(img.getImage());
        jFrame1.setIconImage(img.getImage());
        jFrame2.setIconImage(img.getImage());
        jLabel5.setText("Welcome to EQDiet");
        jLabel9.setText("An open source healthy diet app");
        duration = 0;
        logQuantity = 0;
        restartSession = 0;
        jButton2.setEnabled(false);
        sessionNumber = onStart();
        foodExists = 0;
        isSomethingWritten = false;
        java.io.File updaterFile = new java.io.File(installdir + "EQDietUpdater.jar");
        if (updaterFile.exists()) {
            updaterFile.delete();
        }
        
        /* Creates action on window close */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
                System.exit(0);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jFrame1 = new javax.swing.JFrame();
        jLabel6 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jFrame2 = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jFrame1.setTitle("About EQDiet");
        jFrame1.setLocation(new java.awt.Point(44, 82));
        jFrame1.setResizable(false);
        jFrame1.setSize(new java.awt.Dimension(325, 140));
        jFrame1.setType(java.awt.Window.Type.POPUP);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("About EQDiet Desktop App");

        jButton6.setText("Close");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Version 0.9 Beta");

        jButton7.setText("Check for updates");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Documentation");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("(C) 2021, EQDiet");

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame1Layout.createSequentialGroup()
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jFrame1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)))
                .addContainerGap())
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addContainerGap())
        );

        jFrame2.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jFrame2.setTitle("Food registry");
        jFrame2.setLocation(new java.awt.Point(20, 15));
        jFrame2.setResizable(false);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Food registry");

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jButton4.setText("Exit food registry");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(181, 181, 181))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EQDiet");
        setLocation(new java.awt.Point(30, 15));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("What have you eaten?");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Food:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Quantity (g):");

        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton2.setText("End session");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Exit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setText("About");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton9.setText("Food registry");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton5)
                    .addComponent(jButton9))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            dish = jTextField1.getText();
            grams = jTextField2.getText();
            quantity = Long.parseLong(grams);
            try {
                for (int i = 0; i <= EQDiet_db.foodonly.length; i++) {
                    if (EQDiet_db.foodonly[i].equals((dish).toLowerCase()) || (EQDiet_db.foodonly[i].equals((dish.substring(0, dish.length()-1)).toLowerCase()) && dish.endsWith("s"))) {
                        foodDB = EQDiet_db.foodonly[i].toLowerCase();
                        finalKcal = EQDiet_db.kcalonly[i] * quantity / 100;
                        jLabel5.setText("You have eaten " + finalKcal + " kilocalories");
                        jLabel9.setText("Press \"End session\" to add all your foods");
                        log = "You have eaten " + finalKcal + " kilocalories of " + jTextField1.getText().toLowerCase();
                        logQuantity += finalKcal;
                        jButton2.setEnabled(true);
                        WriteLog();
                        jTextField1.setText(null);
                        jTextField2.setText(null);
                        isSomethingWritten = true;
                        break; //Stops the for sentence
                    }
                }
            } catch (ArrayIndexOutOfBoundsException aioobex) {
                jLabel5.setText("Error:");
                jLabel9.setText("The specified food does not actually exist");
            }
        } catch (NumberFormatException nex) {
            jLabel5.setText("Error:");
            jLabel9.setText("The quantity you specified isn't a number");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(restartSession == 1) {
            jButton2.setText("End session");
            sessionNumber++;
            RestartSession();
        } else {
          duration = 1;
          try {
            FileWriter fwL = new FileWriter("Data/FoodLog.txt", true);
            try (BufferedWriter fL = new BufferedWriter(fwL)) {
                while(duration > 0) {
                    fL.newLine();
                    fL.write("- In total you have eaten " + logQuantity + " kilocalories. -");
                    jTextArea3.setText(jTextArea3.getText() + "- In total you have eaten " + logQuantity + " kilocalories. -");
                    duration = 0;
                }
                restartSession = 1;
                jLabel5.setText("The session has been successfully completed");
                jLabel9.setText("In total you have eaten " + logQuantity + " kilocalories.");
                jButton2.setText("Start new session");
                jTextField1.setText("");
                jTextField2.setText("");
                jTextField1.setEnabled(false);
                jTextField2.setEnabled(false);
                jButton1.setEnabled(false);
                fL.close();
            }
          } catch (IOException ex) {
              jLabel9.setText("Error saving food log");
          }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        onExit();
        System.exit(0);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        jFrame1.setSize(339, 180);
        jFrame1.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        jFrame2.setSize(504, 355);
        jFrame2.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jFrame2.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        goToURL("https://docs.eqdiet.lumito.net");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        java.io.File file = new java.io.File("Data/VersionNumber.txt");
        if (file.exists()) {
            try (FileReader fReader = new FileReader(file);
                    BufferedReader reader = new BufferedReader(fReader)) {
                double ver = Double.valueOf(reader.readLine());
                CheckForUpdates(ver);
            } catch (IOException ex) {
                CheckForUpdates(0.9); // This will check updates with the default version.
            }
        } else {
            CheckForUpdates(0.9); // This will check updates with the default version.
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jFrame1.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EQDiet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new EQDiet().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}