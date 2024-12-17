package org.foxesworld.aibf.reader;

import org.foxesworld.aibf.AnimationConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AIBFReaderGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AIBFReaderGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Font font = new Font("Arial", Font.PLAIN, 14);
            UIManager.put("Label.font", font);
            UIManager.put("Button.font", font);
            UIManager.put("TextArea.font", font);
            UIManager.put("TextField.font", font);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("AIBF Reader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel fileLabel = new JLabel("Select AIBF File:");
        JTextField filePathField = new JTextField();
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> {
            String selectedPath = selectFile("AIBF Files", "aibf");
            if (selectedPath != null) {
                filePathField.setText(selectedPath);
            }
        });

        JTextArea outputTextArea = new JTextArea(5, 40);
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        JButton readButton = new JButton("Read AIBF");
        readButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (filePath == null || filePath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("File path selected: " + filePath);  // Debugging line

            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                JOptionPane.showMessageDialog(frame, "File not found or invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for correct file extension
            if (!filePath.endsWith(".aibf")) {
                JOptionPane.showMessageDialog(frame, "Invalid file format. Please select a .aibf file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                AIBFReader aibfReader = new AIBFReader();
                Map<String, byte[]> sections = aibfReader.readAIBF(filePath);
                System.out.println(sections);

                byte[] metaData = sections.get("META");
                if (metaData != null) {
                    AnimationConfig.Meta meta = aibfReader.readMetaSection(metaData);
                    String output = "Animation: " + meta.getAnimationName() + "\n" +
                            "Author: " + meta.getAuthor() + "\n" +
                            "FPS: " + meta.getFps();
                    outputTextArea.setText(output);
                } else {
                    outputTextArea.setText("META section not found.");
                }
            } catch (IOException ex) {
                //JOptionPane.showMessageDialog(frame, "Error reading file: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(fileLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        frame.add(filePathField, gbc);

        gbc.gridx++;
        frame.add(browseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        frame.add(readButton, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(scrollPane, gbc);

        frame.setVisible(true);
    }

    private static String selectFile(String description, String extension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(description, extension));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());  // Debugging line
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
