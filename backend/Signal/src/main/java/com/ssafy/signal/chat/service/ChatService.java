package com.ssafy.signal.chat.service;

import com.ssafy.signal.chat.domain.ChatRoomDto;
import com.ssafy.signal.chat.domain.ChatRoomEntity;
import com.ssafy.signal.chat.domain.MessageDto;
import com.ssafy.signal.chat.domain.MessageEntity;
import com.ssafy.signal.chat.repository.ChatRoomRepository;
import com.ssafy.signal.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) {
        return chatRoomRepository
                .save(chatRoomDto.asChatRoomEntity())
                .asChatRoomDto();
    }

    public List<ChatRoomDto> getAllChatRooms(long user_id) {
        return new ArrayList<>(chatRoomRepository.findChatRoomsByUserId(user_id))
                .stream()
                .map(ChatRoomEntity::asChatRoomDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public MessageDto saveMessage(MessageDto messageDto) {
        return messageRepository
                .save(messageDto.asMessageEntity())
                .asMessageDto();
    }

    public List<MessageDto> getAllMessages(long chat_id) {
        return messageRepository
                .findByChatRoomEntity_ChatId(chat_id)
                .stream()
                .map(MessageEntity::asMessageDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
