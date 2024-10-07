import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FurnitureManager extends JFrame {
    private final ArrayList<Furniture> furnitureList;
    private final DefaultListModel<String> listModel;
    private final JList<String> furnitureJList;
    private final JTextField nameField;
    private final JTextField typeField;
    private final JTextField quantityField;
    private final JLabel countLabel;
    private final JLabel totalQuantityLabel;

    public FurnitureManager() {
        furnitureList = new ArrayList<>();
        listModel = new DefaultListModel<>();
        furnitureJList = new JList<>(listModel);

        setTitle("Furniture Manager");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Furniture Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Furniture Type:"), gbc);
        typeField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        quantityField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton addButton = new JButton("Add Furniture");
        addButton.addActionListener(new AddFurnitureAction());
        inputPanel.add(addButton, gbc);

        gbc.gridx = 1;
        JButton updateButton = new JButton("Update Furniture");
        updateButton.addActionListener(new UpdateFurnitureAction());
        inputPanel.add(updateButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JButton removeButton = new JButton("Remove Furniture");
        removeButton.addActionListener(new RemoveFurnitureAction());
        inputPanel.add(removeButton, gbc);

        gbc.gridx = 1;
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(e -> clearFields());
        inputPanel.add(clearButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveAction());
        inputPanel.add(saveButton, gbc);

        gbc.gridx = 1;
        JButton sortButton = new JButton("Sort by Name");
        sortButton.addActionListener(new SortAction());
        inputPanel.add(sortButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        countLabel = new JLabel("Total Unique Furniture: 0");
        inputPanel.add(countLabel, gbc);

        gbc.gridy = 7;
        totalQuantityLabel = new JLabel("Total Quantity: 0");
        inputPanel.add(totalQuantityLabel, gbc);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(furnitureJList), BorderLayout.CENTER);
    }

    private void clearFields() {
        nameField.setText("");
        typeField.setText("");
        quantityField.setText("");
        furnitureJList.clearSelection();
    }

    private class AddFurnitureAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            String quantityStr = quantityField.getText().trim();

            if (!name.isEmpty() && !type.isEmpty() && !quantityStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity > 0) {
                        boolean found = false;
                        for (Furniture furniture : furnitureList) {
                            if (furniture.getName().equals(name) && furniture.getType().equals(type)) {
                                furniture.setQuantity(furniture.getQuantity() + quantity);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Furniture furniture = new Furniture(name, type, quantity);
                            furnitureList.add(furniture);
                            listModel.addElement(furniture.toString());
                        }
                        clearFields();
                        updateFurnitureCount();
                        updateTotalQuantity();
                    } else {
                        showError("Quantity must be a positive integer.");
                    }
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid number for quantity.");
                }
            } else {
                showError("Please enter name, type, and quantity.");
            }
        }
    }

    private class UpdateFurnitureAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = furnitureJList.getSelectedIndex();
            if (selectedIndex != -1) {
                String name = nameField.getText().trim();
                String type = typeField.getText().trim();
                String quantityStr = quantityField.getText().trim();

                if (!name.isEmpty() && !type.isEmpty() && !quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0) {
                            furnitureList.set(selectedIndex, new Furniture(name, type, quantity));
                            listModel.set(selectedIndex, furnitureList.get(selectedIndex).toString());
                            clearFields();
                            updateTotalQuantity();
                        } else {
                            showError("Quantity must be a positive integer.");
                        }
                    } catch (NumberFormatException ex) {
                        showError("Please enter a valid number for quantity.");
                    }
                } else {
                    showError("Please enter name, type, and quantity.");
                }
            } else {
                showError("Please select a furniture item to update.");
            }
        }
    }

    private class RemoveFurnitureAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = furnitureJList.getSelectedIndex();
            if (selectedIndex != -1) {
                furnitureList.remove(selectedIndex);
                listModel.remove(selectedIndex);
                updateFurnitureCount();
                updateTotalQuantity();
            } else {
                showError("Please select a furniture item to remove.");
            }
        }
    }

    private class SaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("furniture.txt"))) {
                for (Furniture furniture : furnitureList) {
                    writer.println("Name: " + furniture.getName() + ", Type: " + furniture.getType() + ", Quantity: " + furniture.getQuantity());
                }
                JOptionPane.showMessageDialog(FurnitureManager.this, "Furniture saved successfully to furniture.txt.");
            } catch (IOException ex) {
                showError("Error saving furniture: " + ex.getMessage());
            }
        }
    }

    private class SortAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Collections.sort(furnitureList, Comparator.comparing(Furniture::getName));
            updateFurnitureListDisplay();
        }
    }

    private void updateFurnitureListDisplay() {
        listModel.clear();
        for (Furniture furniture : furnitureList) {
            listModel.addElement(furniture.toString());
        }
    }

    private void updateFurnitureCount() {
        countLabel.setText("Total Unique Furniture: " + furnitureList.size());
    }

    private void updateTotalQuantity() {
        int totalQuantity = furnitureList.stream().mapToInt(Furniture::getQuantity).sum();
        totalQuantityLabel.setText("Total Quantity: " + totalQuantity);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(FurnitureManager.this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class Furniture implements Serializable {
        private final String name;
        private final String type;
        private int quantity;

        public Furniture(String name, String type, int quantity) {
            this.name = name;
            this.type = type;
            this.quantity = quantity;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return name + " (" + type + ") - Quantity: " + quantity;
        }
    }

    public static void main(String[] args) {
        FurnitureManager manager = new FurnitureManager();
        manager.setVisible(true);
    }
}
