package com.ssafy.signal.chat.controller;

import com.ssafy.signal.chat.domain.ChatRoomDto;
import com.ssafy.signal.chat.domain.ChatRoomEntity;
import com.ssafy.signal.chat.domain.MessageDto;
import com.ssafy.signal.chat.repository.ChatRoomRepository;
import com.ssafy.signal.chat.service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void send(MessageDto message) throws Exception {
        message = chatService.saveMessage(message);
        chatService.updateLastMessage(message);
        chatService.sendMsgNotification(message);
        messagingTemplate.convertAndSend("/topic/" + message.getChat_id(), message);
    }

    @PostMapping("/chat-room/create")
    public ChatRoomDto createChatRoom(@RequestBody ChatRoomDto chatRoomDto) throws Exception {
        return chatService.createChatRoom(chatRoomDto);
    }

    @GetMapping("/chat-room")
    public List<ChatRoomDto> getAllChatRooms(@RequestParam("user_id") long user_id) throws Exception {
        return chatService.getAllChatRooms(user_id);
    }

    @DeleteMapping("chat-room")
    public void deleteChatRoom(@RequestParam("chat_id") long chat_id) throws Exception {
        chatService.deleteChatRoom(chat_id);
    }

    @GetMapping("/chat-room/messages")
    public List<MessageDto> getAllMessage(@RequestParam("chat_id") long chat_id) throws Exception {
        return chatService.getAllMessages(chat_id);
    }

    @PatchMapping("/message/read")
    public void letMessageRead(@RequestParam("chat_id") long chat_id,@RequestParam("user_id") long user_id) throws Exception {
        chatService.LetMessageRead(chat_id,user_id);
    }
}
