package com.ssafy.signal;

import com.jayway.jsonpath.JsonPath;
import com.ssafy.signal.member.principle.UserPrinciple;
import com.ssafy.signal.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private static String accessToken;
    private static String refreshToken;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

//    @Order(1)
//    @Test
//    public void testSignup() throws Exception {
//        String signupRequest = "{ \"loginId\": \"testuser\", \"password\": \"password123!\", \"name\": \"Test User\", \"type\": \"주니어\" }";
//
//        mockMvc.perform(post("/user/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(signupRequest))
//                .andExpect(status().isOk());
//    }

    @DisplayName("로그인")
    @Order(2)
    @Test
    public void testLogin() throws Exception {
        Thread.sleep(5000);

        String loginRequest = "{ \"loginId\": \"testuser\", \"password\": \"password123!\" }";

        String result = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();

        // JSON 파싱하여 accessToken과 refreshToken 저장
        accessToken = JsonPath.read(result, "$.accessToken");
        refreshToken = JsonPath.read(result, "$.refreshToken");
    }

    @DisplayName("마이페이지")
    @Order(3)
    @Test
    @WithMockUser(username = "testuser")
    public void testMyPage() throws Exception {

        UserPrinciple userPrinciple = mock(UserPrinciple.class);
        when(userPrinciple.getLoginId()).thenReturn("testuser");

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrinciple);

        // Set SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/user/mypage")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loginId").value("testuser"));
    }

    @DisplayName("토큰 재발급")
    @Order(4)
    @Test
    public void testRefreshToken() throws Exception {

        String result = mockMvc.perform(post("/user/refresh")
                        .header("RefreshToken", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();

        // 갱신된 토큰 저장
        accessToken = JsonPath.read(result, "$.accessToken");
        refreshToken = JsonPath.read(result, "$.refreshToken");
    }

//    @DisplayName("로그아웃")
//    @Order(5)
//    @Test
//    public void testLogout() throws Exception {
//
//        UserPrinciple userPrinciple = mock(UserPrinciple.class);
//        when(userPrinciple.getLoginId()).thenReturn("testuser");
//
//        // Mock Authentication
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(userPrinciple);
//
//        // Set SecurityContext
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        mockMvc.perform(post("/user/logout")
//                        .header("Authorization", "Bearer " + accessToken)
//                        .header("RefreshToken", "Bearer " + refreshToken))
//                .andExpect(status().isOk());
//    }

    @DisplayName("회원탈퇴")
    @Order(6)
    @Test
    public void testDeleteAccount() throws Exception {

        UserPrinciple userPrinciple = mock(UserPrinciple.class);
        when(userPrinciple.getLoginId()).thenReturn("testuser");

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrinciple);
        when(authentication.isAuthenticated()).thenReturn(true);

        // Set SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/user/drop")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("RefreshToken", "Bearer " + refreshToken))
                .andExpect(status().isOk());
    }
}
