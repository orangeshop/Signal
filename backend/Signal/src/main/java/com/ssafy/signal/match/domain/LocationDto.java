package com.ssafy.signal.match.domain;

import com.ssafy.signal.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LocationDto {
    private long location_id;

    private double latitude;
    private double longitude;

    private long user_id;

    MemberType memberType;

    public LocationEntity asLocationEntity()
    {
        return LocationEntity
                .builder()
                .latitude(latitude)
                .longitude(longitude)
                .userId(Member.builder().userId(user_id).build())
                .memberType(memberType)
                .build();
    }
}


