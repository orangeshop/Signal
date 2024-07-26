package com.ssafy.signal.Match.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LocationDto {
    private long location_id;

    private double latitude;
    private double longitude;

    private long user_id;
    public LocationEntity asLocationEntity()
    {
        return new LocationEntity(
                latitude,
                longitude,
                user_id
        );
    }
}
