package com.ssafy.signal.Match.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "location")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long location_id;

    @Column
    private double latitude;

    @Column
    public double longitude;

    public LocationEntity(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationDto asLocationDto() {
        return new LocationDto(
                location_id,
                latitude,
                longitude
        );
    }
}
