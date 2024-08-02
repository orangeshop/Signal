package com.ssafy.signal.match.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@AllArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name="member_type")
    public MemberType memberType;

    public LocationDto asLocationDto() {
        return LocationDto
                .builder()
                .location_id(location_id)
                .latitude(latitude)
                .user_id(userId.getUserId())
                .longitude(longitude)
                .memberType(memberType)
                .build();
    }
}
