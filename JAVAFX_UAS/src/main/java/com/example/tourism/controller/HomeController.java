package com.example.tourism.controller;

import com.example.tourism.model.dto.DestinationDTO;
import com.example.tourism.service.IDestinationService;
import com.example.tourism.service.impl.DestinationServiceImpl;
import com.example.tourism.util.SceneManager;
import com.example.tourism.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class HomeController implements Initializable {

    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> ratingCombo;
    @FXML private Button searchButton;
    @FXML private GridPane destinationGrid;
    @FXML private Label userLabel;
    @FXML private Button adminDashboardBtn;

    private final IDestinationService destinationService = new DestinationServiceImpl();
    private List<DestinationDTO> allDestinations;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBoxes();
        setupNavbar();
        loadAllDestinations();
    }

    private void setupNavbar() {
        if (SessionManager.getInstance().isLoggedIn()) {
            String username = SessionManager.getInstance().getCurrentUser().getUsername();
            if (userLabel != null) userLabel.setText("Hello, " + username);
        }
        if (adminDashboardBtn != null) {
            adminDashboardBtn.setVisible(SessionManager.getInstance().isAdmin());
            adminDashboardBtn.setManaged(SessionManager.getInstance().isAdmin());
        }
    }

    private void setupComboBoxes() {
        if (categoryCombo != null) {
            categoryCombo.setItems(FXCollections.observableArrayList(
                    "All Categories", "Beach", "Mountain", "Nature",
                    "Heritage", "Culinary", "Photography"
            ));
            categoryCombo.setValue("All Categories");
        }
        if (ratingCombo != null) {
            ratingCombo.setItems(FXCollections.observableArrayList(
                    "All Ratings", "5 Stars", "4+ Stars", "3+ Stars"
            ));
            ratingCombo.setValue("All Ratings");
        }
    }

    private void loadAllDestinations() {
        new Thread(() -> {
            try {
                allDestinations = destinationService.getAllDestinations();
                Platform.runLater(() -> displayDestinations(allDestinations));
                log.info("Loaded {} destinations", allDestinations.size());
            } catch (Exception e) {
                log.error("Error loading destinations", e);
                Platform.runLater(() -> showError("Gagal memuat destinasi: " + e.getMessage()));
            }
        }).start();
    }

    private void displayDestinations(List<DestinationDTO> destinations) {
        if (destinationGrid == null) return;
        destinationGrid.getChildren().clear();

        if (destinations == null || destinations.isEmpty()) {
            Label noData = new Label("Tidak ada destinasi ditemukan");
            noData.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14;");
            destinationGrid.add(noData, 0, 0);
            return;
        }

        int column = 0;
        int row = 0;
        for (DestinationDTO destination : destinations) {
            VBox card = createDestinationCard(destination);
            GridPane.setFillWidth(card, true);
            destinationGrid.add(card, column, row);
            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createDestinationCard(DestinationDTO destination) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-border-color: #e5e7eb; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 16; " +
                        "-fx-background-radius: 16; " +
                        "-fx-background-color: white; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 4);"
        );
        // FIX #1: Tinggi tetap untuk semua card agar grid tidak berantakan
        card.setPrefHeight(400);
        card.setMinHeight(400);
        card.setMaxHeight(400);
        card.setMaxWidth(Double.MAX_VALUE);

        // ── GAMBAR ──────────────────────────────────────────────────────────
        if (destination.getImages() != null && !destination.getImages().isEmpty()) {
            String imageUrl = "http://localhost:8080" + destination.getImages().get(0);
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(imageUrl, 400, 200, true, true, true);
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);
                // Lebar gambar mengikuti lebar card, dikurangi total lebar border (1px kiri + 1px kanan)
                // agar gambar tidak melebihi (overflow) tepi card yang membulat.
                imageView.fitWidthProperty().bind(card.widthProperty().subtract(2));

                // Clip rounded di sudut atas
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
                clip.widthProperty().bind(imageView.fitWidthProperty());
                clip.setHeight(200);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                imageView.setClip(clip);

                card.getChildren().add(imageView);
            } catch (Exception e) {
                card.getChildren().add(createImagePlaceholder(card));
            }
        } else {
            card.getChildren().add(createImagePlaceholder(card));
        }

        // ── KONTEN INFORMASI ─────────────────────────────────────────────────
        // FIX #2: Padding bottom lebih besar agar deskripsi tidak mepet ke tepi bawah
        VBox content = new VBox();
        content.setPadding(new Insets(14, 16, 20, 16));
        content.setSpacing(6);
        VBox.setVgrow(content, Priority.ALWAYS);

        // Badge kategori
        if (destination.getCategory() != null && !destination.getCategory().isBlank()) {
            Label categoryBadge = new Label(destination.getCategory().toUpperCase());
            categoryBadge.setStyle(
                    "-fx-background-color: #e6faf5; " +
                            "-fx-text-fill: #00b38b; " +
                            "-fx-font-size: 10; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 3 10; " +
                            "-fx-background-radius: 999;"
            );
            content.getChildren().add(categoryBadge);
        }

        // Nama destinasi
        Label nameLabel = new Label(destination.getName() != null ? destination.getName() : "Tanpa Nama");
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #111827;");
        nameLabel.setWrapText(false);
        // FIX #3: Nama tidak boleh overflow melampaui lebar card
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        content.getChildren().add(nameLabel);

        // Lokasi
        if (destination.getLocation() != null && !destination.getLocation().isBlank()) {
            HBox locationBox = new HBox(4);
            locationBox.setAlignment(Pos.CENTER_LEFT);

            Label pinIcon = new Label("📍");
            pinIcon.setStyle("-fx-font-size: 11;");

            Label locationLabel = new Label(destination.getLocation());
            locationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");
            locationLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
            locationLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(locationLabel, Priority.ALWAYS);

            locationBox.getChildren().addAll(pinIcon, locationLabel);
            content.getChildren().add(locationBox);
        }

        // FIX #4: Deskripsi dibatasi tingginya, tidak wrap tak terbatas
        if (destination.getDescription() != null && !destination.getDescription().isBlank()) {
            String desc = destination.getDescription();
            if (desc.length() > 75) desc = desc.substring(0, 75) + "...";

            Label descLabel = new Label(desc);
            descLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #94a3b8;");
            descLabel.setWrapText(true);
            descLabel.setMaxHeight(38); // batasi maks 2 baris
            content.getChildren().add(descLabel);
        }

        // Spacer mendorong rating ke paling bawah content
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        content.getChildren().add(spacer);

        // Baris bawah: rating ⭐ + jumlah review
        HBox bottomRow = new HBox(6);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        double rating = destination.getAverageRating() != null ? destination.getAverageRating() : 0.0;
        Label ratingLabel = new Label(String.format("⭐ %.1f", rating));
        ratingLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #f59e0b;");

        int totalReviews = destination.getTotalReviews() != null ? destination.getTotalReviews() : 0;
        Label reviewLabel = new Label("(" + totalReviews + " reviews)");
        reviewLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #94a3b8;");

        bottomRow.getChildren().addAll(ratingLabel, reviewLabel);
        content.getChildren().add(bottomRow);

        card.setOnMouseClicked(e -> SceneManager.navigateToDestinationDetail(destination.getId()));
        card.getChildren().add(content);
        return card;
    }

    private VBox createImagePlaceholder(VBox card) {
        VBox placeholder = new VBox();
        placeholder.setStyle("-fx-background-color: linear-gradient(135deg, #89f7fe, #66a6ff);");
        placeholder.setPrefHeight(200);
        placeholder.setMinHeight(200);
        placeholder.setMaxHeight(200);
        placeholder.setMaxWidth(Double.MAX_VALUE);
        placeholder.setAlignment(Pos.CENTER);

        Label icon = new Label("🏔️");
        icon.setStyle("-fx-font-size: 40;");
        placeholder.getChildren().add(icon);

        return placeholder;
    }

    @FXML
    private void onSearchClick() {
        if (allDestinations == null) return;

        String searchTerm = searchTextField != null ? searchTextField.getText().toLowerCase() : "";
        String selectedCategory = categoryCombo != null ? categoryCombo.getValue() : "All Categories";
        String selectedRating = ratingCombo != null ? ratingCombo.getValue() : "All Ratings";

        List<DestinationDTO> filtered = allDestinations.stream()
                .filter(d -> {
                    if (!searchTerm.isEmpty()) {
                        boolean matchName = d.getName() != null && d.getName().toLowerCase().contains(searchTerm);
                        boolean matchLoc = d.getLocation() != null && d.getLocation().toLowerCase().contains(searchTerm);
                        boolean matchDesc = d.getDescription() != null && d.getDescription().toLowerCase().contains(searchTerm);
                        if (!matchName && !matchLoc && !matchDesc) return false;
                    }
                    if (!"All Categories".equals(selectedCategory)) {
                        if (!selectedCategory.equals(d.getCategory())) return false;
                    }
                    if (!"All Ratings".equals(selectedRating)) {
                        double minRating = 3.0;
                        if ("5 Stars".equals(selectedRating)) minRating = 5.0;
                        else if ("4+ Stars".equals(selectedRating)) minRating = 4.0;
                        double destRating = d.getAverageRating() != null ? d.getAverageRating() : 0.0;
                        if (destRating < minRating) return false;
                    }
                    return true;
                })
                .toList();

        displayDestinations(filtered);
    }

    @FXML
    private void onLogoutClick() {
        SessionManager.getInstance().logout();
        SceneManager.loadScene("login");
    }

    @FXML
    private void onAdminDashboardClick() {
        SceneManager.loadScene("admin-dashboard");
    }

    @FXML
    private void onWriteReviewClick() {
        log.info("Write Review clicked");
    }

    @FXML
    private void onExploreClick() {
        if (searchTextField != null) searchTextField.clear();
        if (categoryCombo != null) categoryCombo.setValue("All Categories");
        if (ratingCombo != null) ratingCombo.setValue("All Ratings");
        displayDestinations(allDestinations);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}