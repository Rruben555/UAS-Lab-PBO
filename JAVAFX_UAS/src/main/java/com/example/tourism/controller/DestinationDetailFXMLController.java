package com.example.tourism.controller;

import com.example.tourism.model.dto.DestinationDTO;
import com.example.tourism.model.dto.ReviewDTO;
import com.example.tourism.model.dto.request.ReviewRequest;
import com.example.tourism.service.ApiClient;
import com.example.tourism.service.IDestinationService;
import com.example.tourism.service.IReviewService;
import com.example.tourism.service.impl.DestinationServiceImpl;
import com.example.tourism.service.impl.ReviewServiceImpl;
import com.example.tourism.util.SceneManager;
import com.example.tourism.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.tourism.service.ApiClient.getBaseUrl;

@Slf4j
public class DestinationDetailFXMLController implements Initializable {

    @FXML private Label destinationTitle;
    @FXML private Label destinationRating;
    @FXML private Label destinationLocation;
    @FXML private Label destinationDescription;
    @FXML private Label categoryLabel;
    @FXML private Label bestTimeLabel;
    @FXML private VBox reviewsContainer;
    @FXML private Button writeReviewButton;
    @FXML private Button backButton;
    @FXML private HBox imageContainer;
    @FXML private VBox imagePlaceholder;

    private final IDestinationService destinationService = new DestinationServiceImpl();
    private final IReviewService reviewService = new ReviewServiceImpl();
    private Long destinationId;
    private DestinationDTO currentDestination;

    // === DITAMBAHKAN: simpan review milik user yang sedang login ===
    private ReviewDTO currentUserReview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // destinationId will be set externally via setDestinationId()
    }

    public void setDestinationId(Long id) {
        this.destinationId = id;
        loadDestinationData();
    }

    private void loadDestinationData() {
        new Thread(() -> {
            try {
                currentDestination = destinationService.getDestinationById(destinationId);
                if (currentDestination != null) {
                    Platform.runLater(() -> {
                        displayDestinationInfo(currentDestination);
                        loadReviews();
                    });
                    log.info("Destination loaded: {}", currentDestination.getName());
                } else {
                    Platform.runLater(() -> showError("Destinasi tidak ditemukan"));
                }
            } catch (Exception e) {
                log.error("Error loading destination: {}", destinationId, e);
                Platform.runLater(() -> showError("Gagal memuat destinasi: " + e.getMessage()));
            }
        }).start();
    }

    private void displayDestinationInfo(DestinationDTO destination) {
        if (destinationTitle != null)
            destinationTitle.setText(destination.getName());

        if (destinationRating != null) {
            double rating = destination.getAverageRating() != null ? destination.getAverageRating() : 0.0;
            long reviews = destination.getTotalReviews() != null ? destination.getTotalReviews() : 0;
            int fullStars = (int) Math.round(rating);
            String stars = "★".repeat(fullStars) + "☆".repeat(5 - fullStars);
            destinationRating.setText(String.format("%s %.1f • %d Reviews", stars, rating, reviews));
        }

        if (destinationLocation != null)
            destinationLocation.setText(destination.getLocation() != null ? destination.getLocation() : "");

        if (destinationDescription != null) {
            destinationDescription.setText(destination.getDescription() != null ? destination.getDescription() : "");
        }

        if (categoryLabel != null)
            categoryLabel.setText(destination.getCategory() != null ? destination.getCategory() : "");

        if (bestTimeLabel != null)
            bestTimeLabel.setText("Year-round");

        loadImages(destination);
    }

    // === DIUBAH: loadReviews sekarang deteksi review milik user yang login ===
    private void loadReviews() {
        new Thread(() -> {
            try {
                List<ReviewDTO> reviews = reviewService.getReviewsByDestination(destinationId);

                String loggedInUser = SessionManager.getInstance().isLoggedIn()
                        ? SessionManager.getInstance().getUsername()
                        : null;

                ReviewDTO myReview = null;
                if (loggedInUser != null) {
                    for (ReviewDTO r : reviews) {
                        if (loggedInUser.equals(r.getUsername())) {
                            myReview = r;
                            break;
                        }
                    }
                }

                final ReviewDTO finalMyReview = myReview;
                Platform.runLater(() -> {
                    currentUserReview = finalMyReview;
                    updateWriteReviewButton();
                    displayReviews(reviews);
                });
                log.info("Loaded {} reviews", reviews.size());
            } catch (Exception e) {
                log.error("Error loading reviews for destination: {}", destinationId, e);
            }
        }).start();
    }

    // === DITAMBAHKAN: ubah label tombol sesuai apakah user sudah review ===
    private void updateWriteReviewButton() {
        if (writeReviewButton == null) return;
        if (currentUserReview != null) {
            writeReviewButton.setText("Edit My Review");
        } else {
            writeReviewButton.setText("Write a Review");
        }
    }

    // === DIUBAH: displayReviews sekarang terima info isOwner & isAdmin ===
    private void displayReviews(List<ReviewDTO> reviews) {
        if (reviewsContainer == null) return;
        reviewsContainer.getChildren().clear();

        if (reviews == null || reviews.isEmpty()) {
            Label noReviews = new Label("Belum ada review. Jadilah yang pertama!");
            noReviews.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14;");
            reviewsContainer.getChildren().add(noReviews);
            return;
        }

        boolean isAdmin = SessionManager.getInstance().isAdmin();
        String loggedInUser = SessionManager.getInstance().isLoggedIn()
                ? SessionManager.getInstance().getUsername() : null;

        for (ReviewDTO review : reviews) {
            boolean isOwner = loggedInUser != null && loggedInUser.equals(review.getUsername());
            reviewsContainer.getChildren().add(createReviewItem(review, isOwner, isAdmin));
        }
    }

    // === DIUBAH: createReviewItem sekarang tampilkan badge + tombol edit/hapus ===
    private VBox createReviewItem(ReviewDTO review, boolean isOwner, boolean isAdmin) {
        VBox reviewBox = new VBox();
        reviewBox.setStyle(
                "-fx-border-color: #e5e7eb; -fx-border-width: 1; " +
                        "-fx-border-radius: 16; -fx-padding: 24; -fx-spacing: 12;"
        );

        // Header: username + badge + spacer + bintang
        HBox headerBox = new HBox();
        headerBox.setSpacing(8);
        headerBox.setStyle("-fx-alignment: center-left;");

        Label usernameLabel = new Label(review.getUsername() != null ? review.getUsername() : "Anonymous");
        usernameLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14;");
        headerBox.getChildren().add(usernameLabel);

        // Badge "Your Review" jika milik sendiri
        if (isOwner) {
            Label badge = new Label("Your Review");
            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; " +
                    "-fx-font-size: 11; -fx-font-weight: 600; " +
                    "-fx-padding: 2 8; -fx-border-radius: 999; -fx-background-radius: 999;");
            headerBox.getChildren().add(badge);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int rating = review.getRating() != null ? review.getRating() : 0;
        String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
        Label ratingLabel = new Label(stars + " " + rating);
        ratingLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 13;");

        headerBox.getChildren().addAll(spacer, ratingLabel);

        // Komentar
        Label commentLabel = new Label(review.getComment() != null ? review.getComment() : "");
        commentLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 13;");
        commentLabel.setWrapText(true);

        reviewBox.getChildren().addAll(headerBox, commentLabel);

        // Tombol Edit (owner) dan Hapus (owner/admin)
        if (isOwner || isAdmin) {
            HBox actionBox = new HBox();
            actionBox.setSpacing(8);
            actionBox.setStyle("-fx-alignment: center-right;");

            if (isOwner) {
                Button editBtn = new Button("✏ Edit");
                editBtn.setStyle("-fx-padding: 6 16; -fx-border-color: #94a3b8; -fx-border-width: 1; " +
                        "-fx-border-radius: 999; -fx-background-radius: 999; " +
                        "-fx-background-color: white; -fx-font-size: 12; -fx-cursor: hand;");
                editBtn.setOnAction(e -> showReviewDialog(review));
                actionBox.getChildren().add(editBtn);
            }

            Button deleteBtn = new Button("🗑 Hapus");
            deleteBtn.setStyle("-fx-padding: 6 16; -fx-background-color: #fee2e2; -fx-text-fill: #dc2626; " +
                    "-fx-border-radius: 999; -fx-background-radius: 999; " +
                    "-fx-font-size: 12; -fx-cursor: hand; -fx-border-color: transparent;");
            deleteBtn.setOnAction(e -> confirmDeleteReview(review.getId()));
            actionBox.getChildren().add(deleteBtn);

            reviewBox.getChildren().add(actionBox);
        }

        return reviewBox;
    }

    // === DIUBAH: onWriteReviewClick sekarang kirim currentUserReview ke dialog ===
    @FXML
    private void onWriteReviewClick() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            SceneManager.loadScene("login");
            return;
        }
        showReviewDialog(currentUserReview);
    }

    // === DIUBAH: showReviewDialog sekarang bisa untuk create maupun edit ===
    private void showReviewDialog(ReviewDTO existing) {
        boolean isEdit = (existing != null);

        Dialog<ReviewRequest> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Review" : "Tulis Review");
        dialog.setHeaderText(isEdit ? "Perbarui review kamu" : "Bagikan pengalaman kamu di destinasi ini");

        VBox content = new VBox();
        content.setSpacing(15);
        content.setPadding(new Insets(20));

        Label ratingLabel = new Label("Rating (1-5)");
        ratingLabel.setStyle("-fx-font-weight: bold;");
        Slider ratingSlider = new Slider(1, 5, isEdit ? existing.getRating() : 3);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setBlockIncrement(1);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setSnapToTicks(true);
        Label ratingValue = new Label((int) ratingSlider.getValue() + " ⭐");
        ratingValue.setStyle("-fx-font-size: 14; -fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        ratingSlider.valueProperty().addListener((obs, o, n) ->
                ratingValue.setText(n.intValue() + " ⭐")
        );

        Label commentLabel = new Label("Komentar");
        commentLabel.setStyle("-fx-font-weight: bold;");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Ceritakan pengalaman kamu...");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(5);
        if (isEdit && existing.getComment() != null) {
            commentArea.setText(existing.getComment());
        }

        content.getChildren().addAll(ratingLabel, ratingSlider, ratingValue, commentLabel, commentArea);
        dialog.getDialogPane().setContent(content);

        ButtonType submitBtn = new ButtonType(
                isEdit ? "Simpan Perubahan" : "Submit Review",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelBtn = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtn, cancelBtn);

        dialog.setResultConverter(bt -> {
            if (bt == submitBtn) {
                return ReviewRequest.builder()
                        .rating((int) ratingSlider.getValue())
                        .comment(commentArea.getText())
                        .build();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(request -> {
            if (request.getComment() == null || request.getComment().trim().isEmpty()) {
                showError("Komentar tidak boleh kosong");
                return;
            }
            submitReview(request);
        });
    }

    private void submitReview(ReviewRequest request) {
        new Thread(() -> {
            ReviewDTO result = reviewService.createReview(request, destinationId);
            Platform.runLater(() -> {
                if (result != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Berhasil");
                    alert.setHeaderText(null);
                    alert.setContentText("Review berhasil disimpan! Terima kasih 🙏");
                    alert.showAndWait();
                    loadReviews();
                } else {
                    showError("Gagal menyimpan review. Silakan coba lagi.");
                }
            });
        }).start();
    }

    private void loadImages(DestinationDTO destination) {
        if (imageContainer == null) return;
        imageContainer.getChildren().clear();

        List<String> images = destination.getImages();
        if (images == null || images.isEmpty()) {
            // Tampilkan placeholder gradient
            VBox placeholder = new VBox();
            placeholder.setStyle("-fx-background-color: linear-gradient(135deg, #89f7fe, #66a6ff); -fx-background-radius: 24;");
            placeholder.setPrefHeight(400);
            placeholder.setPrefWidth(800);
            imageContainer.getChildren().add(placeholder);
            return;
        }

        // Tampilkan gambar pertama sebagai cover utama
        String firstImage =  getBaseUrl() + images.get(0);
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(firstImage, true);
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);
            imageView.setFitWidth(800);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-background-radius: 24;");
            imageContainer.getChildren().add(imageView);
        } catch (Exception e) {
            log.warn("Gagal load image: {}", firstImage);
            VBox placeholder = new VBox();
            placeholder.setStyle("-fx-background-color: linear-gradient(135deg, #89f7fe, #66a6ff); -fx-background-radius: 24;");
            placeholder.setPrefHeight(400);
            imageContainer.getChildren().add(placeholder);
        }
    }

    // === DITAMBAHKAN: konfirmasi sebelum hapus ===
    private void confirmDeleteReview(Long reviewId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Review");
        confirm.setHeaderText(null);
        confirm.setContentText("Yakin ingin menghapus review ini?");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) deleteReview(reviewId);
        });
    }

    // === DITAMBAHKAN: eksekusi hapus review ke API ===
    private void deleteReview(Long reviewId) {
        new Thread(() -> {
            boolean success = reviewService.deleteReview(reviewId);
            Platform.runLater(() -> {
                if (success) {
                    loadReviews();
                } else {
                    showError("Gagal menghapus review.");
                }
            });
        }).start();
    }

    @FXML
    private void onBackClick() {
        SceneManager.loadScene("home");
    }

    @FXML
    private void onLoadMoreClick() {
        loadReviews();
    }

    @FXML
    private void onAddWishlistClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Fitur wishlist akan segera tersedia!");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}