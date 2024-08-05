package com.ssafy.signal.match.domain;

import com.ssafy.signal.member.domain.Member;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MatchListResponse {
    Member user;
    LocationDto location;

    private String url;

    private double dist;
    private int quadrant;

    public static MatchListResponse asMatchResponse(
            Member user,LocationDto location, double reference_lat,
            double reference_lon, String url)
    {
        int quadrant = getQuadrant(location, reference_lat, reference_lon);

        double target_lat = location.getLatitude();
        double target_lon = location.getLongitude();

        return MatchListResponse
                .builder()
                .user(user)
                .location(location)
                .url(url)
                .dist(getDistance(target_lat, target_lon, reference_lat, reference_lon))
                .quadrant(quadrant)
                .build();
    }

    private static final int EARTH_RADIUS = 6371;

    private static int getQuadrant(LocationDto location, double reference_lat, double reference_lon) {

        int quadrant = 0;
        double target_lat = location.getLatitude();
        double target_lon = location.getLongitude();

        if(target_lat >= reference_lat &&  target_lon >= reference_lon)
            quadrant = 1;
        else if(target_lat >= reference_lat && target_lon <= reference_lon)
            quadrant = 2;
        else if(target_lat < reference_lat && target_lon <= reference_lon)
            quadrant = 3;
        else if(target_lat < reference_lat && target_lon >= reference_lon)
            quadrant = 4;

        return quadrant;
    }

    private static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2)* Math.sin(dLat/2)+ Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2))* Math.sin(dLon/2)* Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }
}
