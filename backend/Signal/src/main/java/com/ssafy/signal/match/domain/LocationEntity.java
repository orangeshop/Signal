package com.ssafy.signal.match.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "location")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long location_id;

    @OneToOne
    @JoinColumn(name="user_id")
    Member userId;

    @Column
    private double latitude;

    @Column
    public double longitude;

    public LocationEntity(double latitude, double longitude,long user_id)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = Member
                .builder()
                .userId(user_id)
                .build();
    }

    public LocationDto asLocationDto() {
        return new LocationDto(
                location_id,
                latitude,
                longitude,
                userId.getUserId()
        );
    }
}
