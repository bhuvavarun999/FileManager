package com.mycompany.cecsfilemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

 

 

public class App extends JFrame {
    JPanel panel, topPanel;
    JMenuBar menuBar;
    JMenu fileMenu, treeMenu, windowMenu, helpMenu;
    JMenuItem renameItem, copyItem, deleteItem, runItem, exitItem;
    JMenuItem expandItem, collapseItem;
    JMenuItem newItem, cascadeItem;
    JMenuItem helpItem, aboutItem;
    JToolBar toolBar;
    JComboBox combobox;
    JButton detailBtn, simpleBtn;
    JLabel statusBar;
    JDesktopPane desktopPane;
    public static int internalFrameCount = 0;
    private String currentDrive;
    private File currentDirectory;

    public App(){

        panel = new JPanel();
        topPanel = new JPanel();
        menuBar = new JMenuBar();
        statusBar = new JLabel("Current Drive");
        desktopPane = new JDesktopPane();    
        currentDirectory = new File(System.getProperty("user.dir"));
        currentDrive = currentDirectory.getAbsolutePath().substring(0, 3);

    }

    private void createNewInternalFrame(String selectedDirectory) {
            internalFrameCount++;
           InternalFrame myinternalframe = new InternalFrame(currentDrive);
            desktopPane.add(myinternalframe);
            myinternalframe.setVisible(true);
            myinternalframe.setLocation(internalFrameCount * 10, internalFrameCount * 10);
    }

    public void go(){
        this.setTitle("CECS 544  Project - Varun Bhuva");
        panel.setLayout(new BorderLayout());
        topPanel.setLayout(new BorderLayout());

        //internalFrame internalFrameObj = new internalFrame(currentDrive);
         String initialDirectory = currentDrive;

        InternalFrame internalFrameObj = new InternalFrame(initialDirectory);
        createMenuBar(internalFrameObj);
        //topPanel.add(menuBar,BorderLayout.NORTH);
        panel.add(topPanel,BorderLayout.NORTH);       

        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createToolBar(internalFrameObj);
        topPanel.add(toolBar, BorderLayout.SOUTH);
        topPanel.setSize(1000,200);

        // Add the desktop pane
        panel.add(desktopPane, BorderLayout.CENTER);            

        // Create an internal frame and add it to the desktop pane
        //internalFrame myinternalframe = new internalFrame();
        desktopPane.add(internalFrameObj);

        internalFrameObj.setVisible(true);

        addStatusBar();
        this.add(statusBar, BorderLayout.SOUTH);

        this.setVisible(true);
        this.setSize(1000,650);
    }


    public void createMenuBar(InternalFrame internalFrameObj){

        fileMenu = new JMenu("File");
        treeMenu = new JMenu("Tree");
        windowMenu = new JMenu("Window");
        helpMenu = new JMenu("Help");

        // File menu items : Add new menu items for all menus
        renameItem = new JMenuItem("Rename");
        copyItem = new JMenuItem("Copy");
        deleteItem = new JMenuItem("Delete");
        runItem = new JMenuItem("Run");
        exitItem = new JMenuItem("Exit");

        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialogBox("Rename", internalFrameObj);
            }
        });

        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialogBox("Copying", internalFrameObj);
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialogBox("Deleting", internalFrameObj);
            }
        });

        runItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialogBox("Executing", internalFrameObj);
            }
        });        

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        fileMenu.add(renameItem);
        fileMenu.add(copyItem);
        fileMenu.add(deleteItem);
        fileMenu.add(runItem);
        fileMenu.add(exitItem);

        expandItem = new JMenuItem("Expand Branch");
        collapseItem = new JMenuItem("Collapse Branch");

        treeMenu.add(expandItem);
        treeMenu.add(collapseItem);
        
        
        expandItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                internalFrameObj.expandOneLevel();   
            }
        });
        
        collapseItem.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        internalFrameObj.collapseOneLevel();
    }
    });
        
        newItem = new JMenuItem("New");
        // Add action listener to the "New" menu item
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(currentDrive);
                //String selectedDirectory = (String) comboBox.getSelectedItem();
                //createNewInternalFrame(selectedDirectory);
                addStatusBar();
            }
        });
        cascadeItem = new JMenuItem("Cascade");
        cascadeItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
                cascadeInternalFrames();
            }
        });

        windowMenu.add(newItem);
        windowMenu.add(cascadeItem);

        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
                showPopupBoxForHelp();
            }
        });
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
                showPopupBoxForAbout();
            }
        });

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);


        menuBar.add(fileMenu);
        menuBar.add(treeMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);      
    }

    private void createToolBar(InternalFrame internalFrameObj) {
        toolBar = new JToolBar();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0)); // Use FlowLayout
        JComboBox<String> comboBox = new JComboBox<>();
        // Get all the available root directories (drives) on the computer
        File[] roots = File.listRoots();
        
      for (File root : roots) {
            comboBox.addItem(root.getAbsolutePath());
        }
      
      for (File root : roots) {
            if (root.getPath().equals(currentDrive)) {
                comboBox.setSelectedItem(currentDrive);
            }
        }
        
        // Add a listener to detect changes in the selected item of the comboBox
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected drive's path from the comboBox
                String selectedDrive = (String) comboBox.getSelectedItem();
                // Update the currentDrive variable with the selected drive's path
                currentDrive = selectedDrive;
                
               
//                InternalFrame internalFrameObj = new InternalFrame(currentDrive);
//                
//                desktopPane.add(internalFrameObj);
//                internalFrameObj.setVisible(true);
//                internalFrameObj.setLocation(internalFrameCount * 10, internalFrameCount * 10);
                // Update the status bar to show information for the selected drive
                addStatusBar();
            }
        });

 

        Dimension comboBoxSize = new Dimension(150, comboBox.getPreferredSize().height); // Adjust the width as desired
        comboBox.setPreferredSize(comboBoxSize);

        panel.add(comboBox); // Add the JComboBox to the panel

        JButton detailBtn = new JButton("Details");
        detailBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                setFileListRenderer(true);



                internalFrameObj.showFileDetails(internalFrameObj.getSelectedDirectory());
//                internalFrameObj.setFileListRenderer(true);
            }
        });


        panel.add(detailBtn);

        JButton simpleBtn = new JButton("Simple");
        simpleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                setFileListRenderer(false);
                internalFrameObj.showSimpleBtn(internalFrameObj.getSelectedDirectory());
            }
        });
        panel.add(simpleBtn);

 

        toolBar.add(panel); // Add the panel to the JToolBar      

    }

    private void addStatusBar() {
        statusBar.setBorder(BorderFactory.createEtchedBorder()); 
        // Get the drive information
        File drive = new File(currentDrive);
        long totalSpace = drive.getTotalSpace();
        long freeSpace = drive.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

 

        // Convert bytes to GB for better readability
        double totalSpaceGB = totalSpace / (1024.0 * 1024 * 1024);
        double freeSpaceGB = freeSpace / (1024.0 * 1024 * 1024);
        double usedSpaceGB = usedSpace / (1024.0 * 1024 * 1024);

 

        // Display the information in the status bar
        statusBar.setText("Current Drive: " + currentDrive 
            + "     Free Space: " + String.format("%.2f GB", freeSpaceGB) +
                "     Used Space: " + String.format("%.2f GB", usedSpaceGB)
            + "    Total Space: " + String.format("%.2f GB", totalSpaceGB));

    }

    private void cascadeInternalFrames() {
        JInternalFrame[] frames = desktopPane.getAllFrames();        
        int offsetX = 30;
        int offsetY = 30;
        int yOffsetIncrement = 30;
        int n = frames.length - 1;
        int j  = 0;
        for (int i = n; i >= 0; i--) {
            frames[j++].setLocation(offsetY * i, offsetX * i);
        }   
    }

    private static void showPopupBoxForAbout() {
        JOptionPane.showMessageDialog(null, "CECS 277 File Manager", "ABOUT", JOptionPane.INFORMATION_MESSAGE);
    }
     private static void showPopupBoxForHelp() {
        JOptionPane.showMessageDialog(null, "Contact admin for help!", "HELP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDialogBox(String act, InternalFrame internalFrameObj) {
        JInternalFrame selectedFrame = desktopPane.getSelectedFrame();

        if (selectedFrame instanceof JInternalFrame) {
            InternalFrame selectedInternalFrame = (InternalFrame) selectedFrame;
            JList<File> fileList = selectedInternalFrame.getFileList();

 

            File selectedFile = fileList.getSelectedValue();

            PopUpDemo popclickobj =  new PopUpDemo(selectedFile);

            if(act.equals("Rename") || act.equals("Copying")){         
                popclickobj.showRenameCopyDialog(selectedInternalFrame,act);
            }
            else if(act.equals("Executing")){
                internalFrameObj.openFile(selectedFile);
            }
            else{
                popclickobj.showDeleteFileDialog(selectedInternalFrame);
            }
        }
    }
}