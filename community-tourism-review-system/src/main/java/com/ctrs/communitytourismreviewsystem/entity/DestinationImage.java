package com.ctrs.communitytourismreviewsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationImage extends BaseEntity {

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;
}