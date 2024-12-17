package org.foxesworld.aibf.creator;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AIBFCreatorGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AIBFCreatorGUI::createAndShowGUI);
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

        JFrame frame = new JFrame("AIBF Creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel jsonLabel = new JLabel("Select JSON Configuration:");
        JTextField jsonPathField = new JTextField();
        JButton jsonBrowseButton = new JButton("Browse...");
        jsonBrowseButton.addActionListener(e -> {
            String selectedPath = selectFile("JSON Files", "json");
            if (selectedPath != null) {
                jsonPathField.setText(selectedPath);
            }
        });

        JLabel outputLabel = new JLabel("Specify output path for AIBF file:");
        JTextField outputPathField = new JTextField();
        JButton outputBrowseButton = new JButton("Browse...");
        outputBrowseButton.addActionListener(e -> {
            String selectedPath = selectFile("AIBF Files", "aibf");
            if (selectedPath != null) {
                outputPathField.setText(selectedPath);
            }
        });

        JButton createButton = new JButton("Create AIBF");
        JTextArea logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        createButton.addActionListener(e -> {
            String jsonPath = jsonPathField.getText();
            String outputPath = outputPathField.getText();

            if (jsonPath.isEmpty() || outputPath.isEmpty()) {
                logArea.append("Error: Please specify all paths.\n");
                return;
            }

            try {
                AIBFCreator creator = new AIBFCreator();
                creator.createAIBF(jsonPath, outputPath);
                logArea.append("AIBF file successfully created: " + outputPath + "\n");
            } catch (Exception ex) {
                logArea.append("Error creating AIBF: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(jsonLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        frame.add(jsonPathField, gbc);

        gbc.gridx++;
        frame.add(jsonBrowseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        frame.add(outputLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        frame.add(outputPathField, gbc);

        gbc.gridx++;
        frame.add(outputBrowseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        frame.add(createButton, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(logScrollPane, gbc);

        frame.setVisible(true);
    }

    /**
     * Method to select a file using JFileChooser.
     *
     * @param description File description (e.g., "JSON Files").
     * @param extension   File extension (e.g., "json").
     * @return Path to the selected file or null if no file was selected.
     */
    private static String selectFile(String description, String extension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(description, extension));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
