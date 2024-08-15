package com.ssafy.signal.match;

import com.ssafy.signal.match.domain.LocationDto;
import com.ssafy.signal.match.domain.MatchListResponse;
import com.ssafy.signal.match.domain.MemberType;
import com.ssafy.signal.match.service.MatchService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MatchServiceTest {
    @Autowired private MatchService matchService;
    private List<Long> users = new ArrayList<>();
    @BeforeEach
    public void setUp() throws Exception {
        LocationDto u1 = LocationDto
                .builder()
                .user_id(110)
                .latitude(100.0)
                .longitude(100.0)
                .memberType(MemberType.모두)
                .build();

        LocationDto u2 = LocationDto
                .builder()
                .user_id(111)
                .latitude(100.0)
                .longitude(100.0)
                .memberType(MemberType.시니어)
                .build();

        LocationDto u3 = LocationDto
                .builder()
                .user_id(112)
                .latitude(100.0)
                .longitude(100.0)
                .memberType(MemberType.시니어)
                .build();

        LocationDto u4 = LocationDto
                .builder()
                .user_id(113)
                .latitude(100.0)
                .longitude(100.0)
                .memberType(MemberType.시니어)
                .build();

        long id = matchService.saveLocation(u1).getLocation_id();
        users.add(id);
        id = matchService.saveLocation(u2).getLocation_id();
        users.add(id);
        id = matchService.saveLocation(u3).getLocation_id();
        users.add(id);

        id = matchService.saveLocation(u4).getLocation_id();
        users.add(id);
    }

    @AfterEach
    public void tearDown() throws Exception {
        for(Long id : users){
            matchService.deleteLocation(id);
        }
    }

    @Test
    @DisplayName("모두 매칭을 원했을 때")
    public void allmatchTest(){
        List<MatchListResponse> result = matchService.getMatchUser(users.get(0));
        for(MatchListResponse matchListResponse : result){
            System.out.println(matchListResponse);
        }

        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("시니어 매칭을 원했을 때")
    public void seniorMatchTest(){
        List<MatchListResponse> result = matchService.getMatchUser(users.get(1));
        for(MatchListResponse matchListResponse : result){
            System.out.println(matchListResponse);
        }

        assertThat(result.size()).isEqualTo(1);
    }

}
