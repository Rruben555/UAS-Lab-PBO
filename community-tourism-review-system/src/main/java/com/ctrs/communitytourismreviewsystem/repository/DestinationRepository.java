package com.ctrs.communitytourismreviewsystem.repository;

import com.ctrs.communitytourismreviewsystem.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
}