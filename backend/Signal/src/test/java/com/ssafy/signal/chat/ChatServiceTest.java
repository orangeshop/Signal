package com.ssafy.signal.chat;

import com.ssafy.signal.chat.domain.ChatRoomDto;
import com.ssafy.signal.chat.service.ChatService;
import com.ssafy.signal.match.service.MatchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ChatServiceTest {
    @Autowired
    private ChatService chatService;
    private List<Long> users = new ArrayList<>();
    @BeforeEach
    void setUp() {
        ChatRoomDto chatRoomDto1 = ChatRoomDto
                .builder()
                .from_id(110)
                .to_id(111)
                .build();

        ChatRoomDto chatRoomDto2 = ChatRoomDto
                .builder()
                .from_id(110)
                .to_id(112)
                .build();

        ChatRoomDto chatRoomDto3 = ChatRoomDto
                .builder()
                .from_id(110)
                .to_id(113)
                .build();

        long id = chatService.createChatRoom(chatRoomDto1).getChat_id();
        users.add(id);
        id = chatService.createChatRoom(chatRoomDto2).getChat_id();
        users.add(id);
        id = chatService.createChatRoom(chatRoomDto3).getChat_id();
    }

    @AfterEach
    public void tearDown() {
        for (Long id : users) {
            chatService.deleteChatRoom(id);
        }
    }

    @Test
    @DisplayName("채팅방 목록 조회")
    public void testChat() {
        List<ChatRoomDto> chatRoomDtos = chatService.getAllChatRooms(110);
        assertThat(chatRoomDtos.size()).isEqualTo(3);
    }
}
