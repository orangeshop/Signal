package com.ssafy.signal;

import com.ssafy.signal.member.controller.MemberController;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .userId(1L)
                .loginId("testuser")
                .password("password123")
                .name("Test User")
                .build();
    }

    @Test
    @WithMockUser
    void testGetMemberById() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(member);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login_id").value("testuser"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void testCreateMember() throws Exception {
        when(memberService.saveMember(any(Member.class))).thenReturn(member);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"login_id\": \"testuser\", \"password\": \"password123\", \"name\": \"Test User\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login_id").value("testuser"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @WithMockUser
    void testUpdateMember() throws Exception {
        when(memberService.updateMember(any(Member.class))).thenReturn(member);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"user_id\": 1, \"login_id\": \"testuser\", \"password\": \"newpassword123\", \"name\": \"Updated Test User\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login_id").value("testuser"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @WithMockUser
    void testDeleteMember() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete successful"));
    }
}
