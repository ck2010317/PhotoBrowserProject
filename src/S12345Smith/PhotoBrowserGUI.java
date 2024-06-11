package S12345Smith;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoBrowserGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JButton addButton;
    private JButton removeButton;
    private JButton searchButton;
    private JButton createCollectionButton;
    private JTextField searchField;
    private JTextArea displayArea;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem saveMenuItem;
    private JMenuItem loadMenuItem;

    private List<PhotoCollection> collections;

    public PhotoBrowserGUI() {
        collections = new ArrayList<>();
        collections.add(new PhotoCollection("Default Collection"));

        frame = new JFrame("Photo Browser");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        addButton = new JButton("Add Photo");
        removeButton = new JButton("Remove Photo");
        searchButton = new JButton("Search Photos");
        createCollectionButton = new JButton("Create Collection");
        searchField = new JTextField(20);

        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(createCollectionButton);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddPhotoDialog();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRemovePhotoDialog();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = searchField.getText();
                displayArea.append("Searching for: " + searchQuery + "\n");

                new Thread(() -> {
                    List<Photo> searchResults = new ArrayList<>();
                    for (PhotoCollection collection : collections) {
                        searchResults.addAll(PhotoSearch.searchByTags(collection.getPhotos(), List.of(searchQuery.split(",")), false));
                        searchResults.addAll(PhotoSearch.searchByDescription(collection.getPhotos(), searchQuery));

                        // Example date range search (modify as needed)
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date startDate = dateFormat.parse("2022-01-01");
                            Date endDate = dateFormat.parse("2023-01-01");
                            searchResults.addAll(PhotoSearch.searchByDate(collection.getPhotos(), startDate, endDate));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }

                    SwingUtilities.invokeLater(() -> {
                        displayArea.append("Search Results:\n");
                        for (Photo photo : searchResults) {
                            displayArea.append("Title: " + photo.getTitle() + "\n");
                            displayArea.append("Tags: " + String.join(", ", photo.getTags()) + "\n");
                            displayArea.append("Date: " + photo.getDate() + "\n");
                            displayArea.append("Description: " + photo.getDescription() + "\n");
                            displayArea.append("\n");
                        }
                    });
                }).start();
            }
        });

        createCollectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCollection();
            }
        });

        // Menu bar setup
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save");
        loadMenuItem = new JMenuItem("Load");

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // Menu item action listeners
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFile();
            }
        });
    }

    private void createCollection() {
        String collectionName = JOptionPane.showInputDialog(frame, "Enter the name of the new collection:", "Create Collection", JOptionPane.PLAIN_MESSAGE);
        if (collectionName != null && !collectionName.trim().isEmpty()) {
            collections.add(new PhotoCollection(collectionName));
            displayArea.append("Created Collection: " + collectionName + "\n");
        }
    }

    // Method to show the Add Photo Dialog
    private void showAddPhotoDialog() {
        JTextField titleField = new JTextField(20);
        JTextField tagsField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Tags (comma separated):"));
        panel.add(tagsField);
        panel.add(new JLabel("Date (yyyy-mm-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        JComboBox<String> collectionComboBox = new JComboBox<>();
        for (PhotoCollection collection : collections) {
            collectionComboBox.addItem(collection.getName());
        }
        panel.add(new JLabel("Select Collection:"));
        panel.add(collectionComboBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Photo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String tags = tagsField.getText();
            String dateStr = dateField.getText();
            String description = descriptionArea.getText();
            String selectedCollection = (String) collectionComboBox.getSelectedItem();

            // Convert date string to Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Create a new photo and add it to the selected collection
            Photo photo = new Photo(title, List.of(tags.split(",")), date, description);
            for (PhotoCollection collection : collections) {
                if (collection.getName().equals(selectedCollection)) {
                    collection.addPhoto(photo);
                    displayArea.append("Added Photo: " + title + " to " + selectedCollection + "\n");
                    displayArea.append("Tags: " + tags + "\n");
                    displayArea.append("Date: " + date + "\n");
                    displayArea.append("Description: " + description + "\n");
                    break;
                }
            }
        }
    }

    // Method to show the Remove Photo Dialog
    private void showRemovePhotoDialog() {
        JTextField titleField = new JTextField(20);

        JComboBox<String> collectionComboBox = new JComboBox<>();
        for (PhotoCollection collection : collections) {
            collectionComboBox.addItem(collection.getName());
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Title of Photo to Remove:"));
        panel.add(titleField);
        panel.add(new JLabel("Select Collection:"));
        panel.add(collectionComboBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Remove Photo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String photoTitle = titleField.getText();
            String selectedCollection = (String) collectionComboBox.getSelectedItem();

            // Find and remove the photo from the selected collection
            for (PhotoCollection collection : collections) {
                if (collection.getName().equals(selectedCollection)) {
                    Photo toRemove = null;
                    for (Photo photo : collection.getPhotos()) {
                        if (photo.getTitle().equals(photoTitle)) {
                            toRemove = photo;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        collection.removePhoto(toRemove);
                        displayArea.append("Removed Photo: " + photoTitle + " from " + selectedCollection + "\n");
                        break;
                    }
                }
            }
        }
    }

    // Method to save photo collections to a file
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                FileManager.saveToFile(collections, fileChooser.getSelectedFile().getPath());
                displayArea.append("Photo collections saved to file.\n");
            } catch (IOException e) {
                displayArea.append("Error saving photo collections: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    // Method to load photo collections from a file
    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                collections = FileManager.readFromFile(fileChooser.getSelectedFile().getPath());
                displayArea.append("Photo collections loaded from file.\n");
            } catch (IOException | ClassNotFoundException e) {
                displayArea.append("Error loading photo collections: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PhotoBrowserGUI app = new PhotoBrowserGUI();
            app.show();
        });
    }
}
