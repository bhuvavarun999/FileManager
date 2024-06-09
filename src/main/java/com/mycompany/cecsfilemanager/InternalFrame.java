package com.mycompany.cecsfilemanager;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class InternalFrame extends JInternalFrame {
    private JSplitPane splitPane;
    private JTree directoryTree;
    private JList<File> fileList;

    public InternalFrame(String currentDrive) {
        super(currentDrive, true, true, true, true);
        setSize(800, 500);
        setLocation(30, 30);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        initUI(currentDrive);
    }

    private void initUI(String currentDrive) {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        directoryTree = new JTree(createTreeModel(new FileNode(new File(currentDrive))));
        fileList = new JList<>(new DefaultListModel<>());

        // Set tree selection mode to single selection
        directoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Add selection listener to handle folder selection and update right-side file list
    directoryTree.addTreeSelectionListener(e -> {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) directoryTree.getLastSelectedPathComponent();
        if (node != null) {
            FileNode fileNode = (FileNode) node.getUserObject();
            File selectedFile = fileNode.getFile();
            if (selectedFile.isDirectory()) {
                loadFiles(selectedFile);
            }
        }
    });
    
    
    
    fileList.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                File selectedFile = fileList.getSelectedValue();
                if (selectedFile != null && selectedFile.isFile()) {
                    openFile(selectedFile);
                }
            }
        }
    });
        
    
    

        
        // Add mouse listener to handle folder expansion/collapse and file list update on click
        directoryTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node != null && node.isLeaf()) {
                        FileNode fileNode = (FileNode) node.getUserObject();
                        File selectedFile = fileNode.getFile();
                        loadFiles(selectedFile);
                    } else if (directoryTree.isExpanded(path)) {
                        directoryTree.collapsePath(path);
                    } else {
                        directoryTree.expandPath(path);
                    }
                }
            }
        });
        
        
        // Set custom cell renderer for directoryTree
    directoryTree.setCellRenderer(new DefaultTreeCellRenderer() {
        private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
        private Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof FileNode) {
                    FileNode fileNode = (FileNode) userObject;
                    File file = fileNode.getFile();

                    if (file.isFile()) {
                        setIcon(fileIcon);
                    } else if (file.isDirectory()) {
                        setIcon(folderIcon);
                    }

                    setText(file.getName());
                }
            }

            return component;
        }
    });
        
         
        JScrollPane treeScrollPane = new JScrollPane(directoryTree);
        JScrollPane fileScrollPane = new JScrollPane(fileList);

        splitPane.setLeftComponent(treeScrollPane);
        splitPane.setRightComponent(fileScrollPane);

        // Set the preferred size of the split pane's divider
        splitPane.setDividerSize(5);

        // Set the initial divider location (adjust as needed)
        splitPane.setDividerLocation(250);
    
        setContentPane(splitPane);

        // Load the initial files in the current drive
        loadFiles(new File(currentDrive));
    }

    private DefaultTreeModel createTreeModel(FileNode root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root.getFile().getAbsolutePath());
        createNode(root, rootNode);
        return new DefaultTreeModel(rootNode);
    }

    private void createNode(FileNode fileNode, DefaultMutableTreeNode parent) {
        File file = fileNode.getFile();

        if (file.isDirectory()) {
            if (file.getParentFile() == null) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File child : files) {
                        createNode(new FileNode(child), parent);
                    }
                }
            } else {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileNode);
                parent.add(node);

                File[] files = file.listFiles();
                if (files != null) {
                    for (File child : files) {
                        createNode(new FileNode(child), node);
                    }
                }
            }
        }
    }

    private void loadFiles(File directory) {
        DefaultListModel<File> listModel = (DefaultListModel<File>) fileList.getModel();
        listModel.clear();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    public JList<File> getFileList() {
        return fileList;
    }

    public void openFile(File file) {
        
        System.out.println(file);
        
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Desktop is not supported on this platform.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    


    // Helper class to represent files and directories in the tree
    private class FileNode {
        private File file;

        public FileNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return file.getName();
        }
    }
    
    
   // Method to refresh the frame while preserving node structure
public void refresh() {
    // Store the selected file and its parent directory
    File selectedFile = fileList.getSelectedValue();
    File selectedParentDirectory = selectedFile.getParentFile();

    // Rebuild the entire tree structure
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) directoryTree.getModel().getRoot();
    expandAndSelectNode(selectedParentDirectory, rootNode);

    // Reload the files in the selected directory (if applicable)
    if (selectedParentDirectory != null) {
        loadFiles(selectedParentDirectory);
    }
}

// Helper method to expand and select a node for a given file
private void expandAndSelectNode(File file, DefaultMutableTreeNode parentNode) {
    Enumeration<TreeNode> children = parentNode.children();
    while (children.hasMoreElements()) {
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
        FileNode childFileNode = (FileNode) childNode.getUserObject();
        File childFile = childFileNode.getFile();

        if (childFile.equals(file)) {
            TreePath path = new TreePath(childNode.getPath());
            directoryTree.setSelectionPath(path);
            directoryTree.expandPath(path);
            directoryTree.scrollPathToVisible(path);
            return;
        }

        if (file.getAbsolutePath().startsWith(childFile.getAbsolutePath())) {
            expandAndSelectNode(file, childNode);
        }
    }
}

private FileNode getSelectedFileNode() {
        TreePath selectedPath = directoryTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            if (node != null) {
                return (FileNode) node.getUserObject();
            }
        }
        return null;
    }

public File getSelectedDirectory() {
        FileNode selectedFileNode = getSelectedFileNode();
        if (selectedFileNode != null) {
            return selectedFileNode.getFile();
        }
        return null;
    }


    // Helper method to format file size
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", (float) size / 1024);
        } else {
            return String.format("%.2f MB", (float) size / (1024 * 1024));
        }
    }

    // Method to show file details (dates and sizes) in the right-side file list
  public void showFileDetails(File directory) {
    DefaultListModel<File> listModel = new DefaultListModel<>();

    File[] files = directory.listFiles();
    if (files != null) {
        for (File file : files) {
            listModel.addElement(file);
        }
    }

    fileList.setModel(listModel);

    // Set custom renderer for fileList
    fileList.setCellRenderer(new DefaultListCellRenderer() {
         @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof File) {
            File file = (File) value;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = dateFormat.format(new Date(file.lastModified()));
            String size = formatFileSize(file.length()); // Display size for both files and directories

            label.setText(file.getName()+"     " + "\t" +"     "+date + "\t" +"     "+ size);
        }

        return label;
    }
    });
}
  
   public void showSimpleBtn(File directory) {
    DefaultListModel<File> listModel = new DefaultListModel<>();

    File[] files = directory.listFiles();
    if (files != null) {
        for (File file : files) {
            listModel.addElement(file);
        }
    }

    fileList.setModel(listModel);
//    Set custom renderer for fileList
    fileList.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof File) {
                File file = (File) value;
                label.setText(file.getName());
            }

            return label;
        }
    });
}
   
   public void collapseOneLevel() {
    TreePath selectedPath = directoryTree.getSelectionPath();
    if (selectedPath != null) {
        directoryTree.collapsePath(selectedPath);
    }
}
   
    // Method to expand one level in the left side of selected directory
    public void expandOneLevel() {
        File selectedDirectory = getSelectedDirectory();
        if (selectedDirectory != null) {
            TreePath selectedPath = directoryTree.getSelectionPath();
            if (selectedPath != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                if (selectedNode != null) {
                    // Clear the node's children and create new nodes for one level of children
                    selectedNode.removeAllChildren();
                    File[] files = selectedDirectory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            createNode(new FileNode(file), selectedNode);
                        }
                    }

                    // Update the tree model and expand the selected path
                    ((DefaultTreeModel) directoryTree.getModel()).reload(selectedNode);
                    directoryTree.expandPath(selectedPath);
                }
            }
        }
    }

}

