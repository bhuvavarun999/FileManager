/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cecsfilemanager;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PopUpDemo {
    private File selectedFile;

    public PopUpDemo(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public void showRenameCopyDialog(InternalFrame internalFrameObj,String action) {
        // Implement the dialog for Rename or Copy actions here
        if (action.equals("Rename")) {
            // Show a dialog to get the new name for the file/folder
            String newName = JOptionPane.showInputDialog(null, "Enter new name:", "Rename", JOptionPane.PLAIN_MESSAGE);

            // Perform the rename action here using the newName
            if (newName != null && !newName.isEmpty()) {
                File newFile = new File(selectedFile.getParentFile(), newName);
                boolean renamed = selectedFile.renameTo(newFile);
                if (renamed) {
//                     Update the file list in the internal frame
//                    InternalFrame selectedInternalFrame = (InternalFrame) selectedFrame;
                     internalFrameObj.refresh();
                    System.out.println("File/Folder renamed successfully.");
                } else {
                    System.out.println("Failed to rename the File/Folder.");
                }
            }
        } else if (action.equals("Copying")) {
                // Show a dialog to select the destination folder
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int choice = fileChooser.showDialog(null, "Copy");
    if (choice == JFileChooser.APPROVE_OPTION) {
        File destinationFolder = fileChooser.getSelectedFile();

        // Get the selected file
        File selectedFile = internalFrameObj.getFileList().getSelectedValue();

        // Construct the destination file path
        File destinationFile = new File(destinationFolder, selectedFile.getName());

        try {
            // Perform the copy action using Java NIO
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            
            // Print success message
            System.out.println("File copied to: " + destinationFile.getAbsolutePath());
            
            // Refresh the internal frame
            internalFrameObj.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error copying file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
        }
    }

    public void showDeleteFileDialog(InternalFrame internalFrameObj) {
        // Show a confirmation dialog to confirm the delete action
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // Perform the delete action here
            boolean deleted = selectedFile.delete();
            if (deleted) {
                // Update the file list in the internal frame
                 // Refresh the internal frame
            internalFrameObj.refresh();;
                System.out.println("File/Folder deleted successfully.");
            } else {
                System.out.println("Failed to delete the File/Folder.");
            }
        }
    }
}

