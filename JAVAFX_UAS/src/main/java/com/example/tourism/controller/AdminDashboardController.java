package com.example.tourism.controller;

import com.example.tourism.model.dto.ImageDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import com.example.tourism.model.dto.DestinationDTO;
import com.example.tourism.service.IDestinationService;
import com.example.tourism.service.impl.DestinationServiceImpl;
import com.example.tourism.util.SceneManager;
import com.example.tourism.util.SessionManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
public class AdminDashboardController implements Initializable {

    @FXML private Label adminUsernameLabel;

    @FXML private TableView<DestinationDTO> destinationTable;
    @FXML private TableColumn<DestinationDTO, Long> destIdCol;
    @FXML private TableColumn<DestinationDTO, String> destNameCol;
    @FXML private TableColumn<DestinationDTO, String> destCategoryCol;
    @FXML private TableColumn<DestinationDTO, String> destLocationCol;
    @FXML private TableColumn<DestinationDTO, Double> destRatingCol;
    @FXML private TableColumn<DestinationDTO, Integer> destReviewsCol;
    @FXML private TableColumn<DestinationDTO, Void> destActionCol;

    @FXML private Label totalDestLabel;
    @FXML private Label avgRatingLabel;
    @FXML private ListView<String> topDestinationsList;

    @FXML private Label destTotalLabel;
    @FXML private Label destAvgRatingLabel;
    @FXML private Label destTotalReviewsLabel;

    private static final double TABLE_ROW_HEIGHT = 48;
    private static final double TABLE_HEADER_HEIGHT = 44;
    private static final int TABLE_MAX_VISIBLE_ROWS = 8;

    private final IDestinationService destinationService = new DestinationServiceImpl();
    private List<DestinationDTO> destinations;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (SessionManager.getInstance().getCurrentUser() != null) {
            adminUsernameLabel.setText(SessionManager.getInstance().getCurrentUser().getUsername());
        }
        setupDestinationTable();
        loadDestinations();
    }

    @FXML private void onBackToHome() { SceneManager.loadScene("home"); }
    @FXML private void onLogout() {
        SessionManager.getInstance().logout();
        SceneManager.loadScene("login");
    }
    @FXML private void onAddDestination() { showDestinationDialog(null); }
    @FXML private void onRefreshDestinations() { loadDestinations(); }
    // ==================== TABLE SETUP ====================

    private void setupDestinationTable() {
        destinationTable.setFixedCellSize(TABLE_ROW_HEIGHT);
        destIdCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        destNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        destCategoryCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        destLocationCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocation()));
        destRatingCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getAverageRating()));
        destReviewsCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalReviews()));

        destActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edit");
            private final Button imageBtn  = new Button("📷 Image");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setStyle("-fx-padding: 4 10; -fx-font-size: 11; -fx-border-radius: 4; -fx-background-radius: 4;");
                imageBtn.setStyle("-fx-padding: 4 10; -fx-font-size: 11; -fx-background-color: #00d4a4; -fx-text-fill: #111; -fx-border-radius: 4; -fx-background-radius: 4;");
                deleteBtn.setStyle("-fx-padding: 4 10; -fx-font-size: 11; -fx-background-color: #ef4444; -fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");

                editBtn.setOnAction(e -> showDestinationDialog(getTableView().getItems().get(getIndex())));
                imageBtn.setOnAction(e -> showManageImagesDialog(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> showDeleteDestinationConfirmation(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, editBtn, imageBtn, deleteBtn));
            }
        });
    }

    // ==================== LOAD DATA ====================

    private void loadDestinations() {
        Task<List<DestinationDTO>> task = new Task<>() {
            @Override protected List<DestinationDTO> call() { return destinationService.getAllDestinations(); }
            @Override protected void succeeded() {
                destinations = getValue();
                destinationTable.setItems(FXCollections.observableArrayList(destinations));
                applyTableHeight(destinations.size());
                updateStatistics();
            }
            @Override protected void failed() { showError("Error", "Gagal memuat destinations"); }
        };
        new Thread(task).start();
    }

    /**
     * Memberi tinggi tetap pada tabel berdasarkan jumlah baris data,
     * sehingga tabel tidak menampilkan banyak baris kosong seperti
     * spreadsheet (tampilan "terlalu GUI"). Jika jumlah data melebihi
     * batas maksimum, tabel akan menggunakan scrollbar internalnya sendiri.
     */
    private void applyTableHeight(int rowCount) {
        double visibleRows = rowCount == 0 ? 3 : Math.min(rowCount, TABLE_MAX_VISIBLE_ROWS);
        double height = TABLE_HEADER_HEIGHT + (visibleRows * TABLE_ROW_HEIGHT) + 2;
        destinationTable.setPrefHeight(height);
        destinationTable.setMinHeight(height);
        destinationTable.setMaxHeight(height);
    }

    private void updateStatistics() {
        if (destinations != null) {
            totalDestLabel.setText(String.valueOf(destinations.size()));
            double avg = destinations.stream()
                    .mapToDouble(d -> d.getAverageRating() != null ? d.getAverageRating() : 0)
                    .average().orElse(0);
            avgRatingLabel.setText(String.format("%.1f", avg));

            int totalReviews = destinations.stream()
                    .mapToInt(d -> d.getTotalReviews() != null ? d.getTotalReviews() : 0)
                    .sum();

            if (destTotalLabel != null) destTotalLabel.setText(String.valueOf(destinations.size()));
            if (destAvgRatingLabel != null) destAvgRatingLabel.setText(String.format("%.1f", avg));
            if (destTotalReviewsLabel != null) destTotalReviewsLabel.setText(String.valueOf(totalReviews));

            topDestinationsList.setItems(FXCollections.observableArrayList(
                    destinations.stream()
                            .filter(d -> d.getAverageRating() != null)
                            .sorted((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()))
                            .limit(10)
                            .map(d -> String.format("⭐ %.1f  %s  —  %s",
                                    d.getAverageRating(), d.getName(),
                                    d.getLocation()))
                            .toList()
            ));
        }
    }

    // ==================== DESTINATION DIALOG ====================

    private void showDestinationDialog(DestinationDTO existing) {
        boolean isEdit = existing != null;

        Dialog<DestinationDTO> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Destination" : "Add New Destination");
        dialog.setHeaderText(isEdit ? "Edit: " + existing.getName() : "Create a new destination");

        DialogPane pane = dialog.getDialogPane();
        pane.setPrefWidth(480);

        TextField nameField = new TextField(isEdit ? existing.getName() : "");
        nameField.setPromptText("Destination name");

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(
                "Beach", "Mountain", "Nature", "Heritage", "Culinary", "Photography"));
        categoryCombo.setPromptText("Select category");
        categoryCombo.setPrefWidth(Double.MAX_VALUE);
        if (isEdit) categoryCombo.setValue(existing.getCategory());

        TextField locationField = new TextField(isEdit ? existing.getLocation() : "");
        locationField.setPromptText("Location (e.g. Bali, Indonesia)");

        TextArea descriptionArea = new TextArea(isEdit ? existing.getDescription() : "");
        descriptionArea.setPromptText("Description...");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(3);

        // Image picker (hanya untuk Add, bukan Edit)
        Label selectedImageLabel = new Label("No image selected");
        selectedImageLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        final File[] selectedImageFile = {null};

        Button pickImageBtn = new Button("📷 Choose Image (Optional)");
        pickImageBtn.setStyle("-fx-padding: 8 16; -fx-background-color: #f8fafc; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        pickImageBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Image");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(SceneManager.getPrimaryStage());
            if (file != null) {
                selectedImageFile[0] = file;
                selectedImageLabel.setText("✅ " + file.getName());
                selectedImageLabel.setStyle("-fx-text-fill: #00796b; -fx-font-size: 12;");
            }
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12;");

        VBox form = new VBox(10,
                new Label("Name"), nameField,
                new Label("Category"), categoryCombo,
                new Label("Location"), locationField,
                new Label("Description"), descriptionArea,
                new Label("Image"), pickImageBtn, selectedImageLabel,
                errorLabel
        );
        form.setStyle("-fx-padding: 10;");
        pane.setContent(form);

        ButtonType saveBtn = new ButtonType(isEdit ? "Update" : "Add", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(saveBtn, new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));

        pane.lookupButton(saveBtn).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty()) { errorLabel.setText("Name is required"); event.consume(); }
            else if (categoryCombo.getValue() == null) { errorLabel.setText("Please select a category"); event.consume(); }
            else if (locationField.getText().trim().isEmpty()) { errorLabel.setText("Location is required"); event.consume(); }
        });

        dialog.setResultConverter(btn -> btn == saveBtn ? DestinationDTO.builder()
                                                          .name(nameField.getText().trim())
                                                          .category(categoryCombo.getValue())
                                                          .location(locationField.getText().trim())
                                                          .description(descriptionArea.getText().trim())
                                                          .build() : null);

        dialog.showAndWait().ifPresent(dto -> {
            if (isEdit) {
                updateDestination(existing.getId(), dto);
            } else {
                // Buat destination dulu, lalu upload image jika ada
                createDestinationThenUploadImage(dto, selectedImageFile[0]);
            }
        });
    }

    private void showManageImagesDialog(DestinationDTO destination) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Images — " + destination.getName());
        dialog.setHeaderText("Kelola gambar untuk destinasi ini");
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 10;");

        // Tombol upload gambar baru
        Button uploadBtn = new Button("+ Upload Gambar Baru");
        uploadBtn.setStyle("-fx-padding: 8 16; -fx-background-color: #00d4a4; -fx-text-fill: #111; -fx-font-weight: bold; -fx-border-radius: 6; -fx-background-radius: 6;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");

        // Container untuk daftar gambar
        VBox imageListBox = new VBox(10);
        content.getChildren().addAll(uploadBtn, statusLabel, new Separator(), imageListBox);

        // Load gambar yang sudah ada
        loadImagesForDialog(destination.getId(), imageListBox, statusLabel);

        uploadBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Pilih Gambar");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(SceneManager.getPrimaryStage());
            if (file != null) {
                uploadImageTask(destination.getId(), file, () -> {
                    statusLabel.setText("✅ Gambar berhasil diupload!");
                    loadImagesForDialog(destination.getId(), imageListBox, statusLabel);
                    loadDestinations();
                });
            }
        });

        dialog.getDialogPane().setContent(new ScrollPane(content));
        dialog.showAndWait();
    }

    private void loadImagesForDialog(Long destinationId, VBox imageListBox, Label statusLabel) {
        Task<List<ImageDTO>> task = new Task<>() {
            @Override protected List<ImageDTO> call() {
                return destinationService.getImages(destinationId);
            }
            @Override protected void succeeded() {
                imageListBox.getChildren().clear();
                List<ImageDTO> images = getValue();

                if (images.isEmpty()) {
                    Label empty = new Label("Belum ada gambar untuk destinasi ini.");
                    empty.setStyle("-fx-text-fill: #64748b;");
                    imageListBox.getChildren().add(empty);
                    return;
                }

                for (ImageDTO image : images) {
                    HBox row = new HBox(10);
                    row.setStyle("-fx-alignment: center-left; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8;");

                    // Preview gambar
                    try {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(
                                "http://localhost:8080" + image.getImagePath(), 80, 60, true, true, true);
                        javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(img);
                        iv.setFitWidth(80);
                        iv.setFitHeight(60);
                        row.getChildren().add(iv);
                    } catch (Exception e) {
                        VBox placeholder = new VBox();
                        placeholder.setStyle("-fx-background-color: #f1f5f9;");
                        placeholder.setPrefSize(80, 60);
                        row.getChildren().add(placeholder);
                    }

                    // Path gambar
                    Label pathLabel = new Label(image.getImagePath());
                    pathLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #374151;");
                    HBox.setHgrow(pathLabel, javafx.scene.layout.Priority.ALWAYS);

                    // Tombol hapus
                    Button deleteBtn = new Button("🗑 Hapus");
                    deleteBtn.setStyle("-fx-padding: 6 12; -fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-border-radius: 6; -fx-background-radius: 6;");
                    deleteBtn.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Hapus Gambar");
                        confirm.setContentText("Yakin ingin menghapus gambar ini?");
                        confirm.showAndWait().ifPresent(result -> {
                            if (result == ButtonType.OK) {
                                Task<Boolean> deleteTask = new Task<>() {
                                    @Override protected Boolean call() {
                                        return destinationService.deleteImage(image.getId());
                                    }
                                    @Override protected void succeeded() {
                                        statusLabel.setText("🗑 Gambar berhasil dihapus!");
                                        loadImagesForDialog(destinationId, imageListBox, statusLabel);
                                        loadDestinations();
                                    }
                                };
                                new Thread(deleteTask).start();
                            }
                        });
                    });

                    row.getChildren().addAll(pathLabel, deleteBtn);
                    imageListBox.getChildren().add(row);
                }
            }
        };
        new Thread(task).start();
    }

    // ==================== DESTINATION CRUD ====================

    private void createDestinationThenUploadImage(DestinationDTO dto, File imageFile) {
        Task<DestinationDTO> task = new Task<>() {
            @Override
            protected DestinationDTO call() {
                return destinationService.createDestination(dto);
            }

            @Override
            protected void succeeded() {
                DestinationDTO created = getValue();
                if (created != null) {
                    if (imageFile != null) {
                        // Ada image — upload setelah destination dibuat
                        uploadImageTask(created.getId(), imageFile, () -> {
                            showInfo("Success", "Destination \"" + created.getName() + "\" berhasil ditambahkan dengan image!");
                            loadDestinations();
                        });
                    } else {
                        showInfo("Success", "Destination \"" + created.getName() + "\" berhasil ditambahkan!");
                        loadDestinations();
                    }
                } else {
                    showError("Error", "Gagal menambahkan destination");
                }
            }

            @Override
            protected void failed() {
                showError("Error", "Gagal menambahkan destination: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void updateDestination(Long id, DestinationDTO dto) {
        Task<DestinationDTO> task = new Task<>() {
            @Override protected DestinationDTO call() { return destinationService.updateDestination(id, dto); }
            @Override protected void succeeded() {
                if (getValue() != null) { showInfo("Success", "Destination berhasil diupdate!"); loadDestinations(); }
                else showError("Error", "Gagal mengupdate destination");
            }
            @Override protected void failed() { showError("Error", "Gagal mengupdate destination"); }
        };
        new Thread(task).start();
    }

    private void showDeleteDestinationConfirmation(DestinationDTO destination) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Destination");
        alert.setContentText("Yakin ingin menghapus \"" + destination.getName() + "\"?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                Task<Boolean> task = new Task<>() {
                    @Override protected Boolean call() { return destinationService.deleteDestination(destination.getId()); }
                    @Override protected void succeeded() { showInfo("Success", "Destination berhasil dihapus!"); loadDestinations(); }
                    @Override protected void failed() { showError("Error", "Gagal menghapus destination"); }
                };
                new Thread(task).start();
            }
        });
    }

    // ==================== IMAGE UPLOAD ====================

    private void uploadImageTask(Long destinationId, File file, Runnable onSuccess) {
        Task<String> task = new Task<>() {
            @Override protected String call() { return destinationService.uploadImage(destinationId, file); }
            @Override protected void succeeded() {
                if (getValue() != null) onSuccess.run();
                else showError("Error", "Gagal upload image");
            }
            @Override protected void failed() { showError("Error", "Gagal upload image: " + getException().getMessage()); }
        };
        new Thread(task).start();
    }

    // ==================== HELPERS ====================

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }
}