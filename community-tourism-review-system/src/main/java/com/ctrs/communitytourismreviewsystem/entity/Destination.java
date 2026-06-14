package com.ctrs.communitytourismreviewsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double averageRating;

    @Builder.Default
    @OneToMany(
            mappedBy = "destination",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DestinationImage> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "destination",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Review> reviews = new ArrayList<>();
}