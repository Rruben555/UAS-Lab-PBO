package com.ctrs.communitytourismreviewsystem.repository;

import com.ctrs.communitytourismreviewsystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDestinationId(Long destinationId);

    Optional<Review> findByUserIdAndDestinationId(
            Long userId,
            Long destinationId
    );

    long countByDestinationId(Long destinationId);
}