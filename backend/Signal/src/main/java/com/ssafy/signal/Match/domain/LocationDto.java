package com.ssafy.signal.Match.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LocationDto {
    public long location_id;

    public double latitude;
    public double longitude;


    public LocationEntity asLocationEntity()
    {
        return new LocationEntity(
                latitude,
                longitude
        );
    }
}
