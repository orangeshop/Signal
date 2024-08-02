package com.ssafy.signal.chat.service;

import com.ssafy.signal.chat.domain.*;
import com.ssafy.signal.chat.repository.ChatRoomRepository;
import com.ssafy.signal.chat.repository.MessageRepository;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) {
        chatRoomDto.setSend_at(DateTime.now().toDate());
        return chatRoomRepository
                .save(chatRoomDto
                        .asChatRoomEntity())
                .asChatRoomDto();
    }

    public List<ChatRoomDto> getAllChatRooms(long user_id) {
        Member userId = Member.builder().userId(user_id).build();

        List<ChatRoomEntity> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId);
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();

        for(ChatRoomEntity chatRoom : chatRooms) {
            long from_id = chatRoom.getFrom_id().getUserId();
            long to_id = chatRoom.getTo_id().getUserId();

            String from_name="",to_name="";
            if(memberRepository.findById(from_id).isPresent()) {
                from_name = memberRepository.findById(from_id).get().getName();
            }

            if(memberRepository.findById(to_id).isPresent()) {
                to_name = memberRepository.findById(to_id).get().getName();
            }

            chatRoomDtos.add(chatRoom.asChatRoomDto(from_name,to_name));
        }
        return chatRoomDtos;
    }

    public ChatRoomDto updateLastMessage(MessageDto messageDto)
    {
        long chat_id = messageDto.getChat_id();
        ChatRoomDto chatRoomDto = chatRoomRepository.findById(chat_id).orElseThrow().asChatRoomDto();
        chatRoomDto.setLast_message(messageDto.getContent());
        chatRoomDto.setSend_at(messageDto.getSend_at());

        if(messageDto.getIs_from_sender() != null)
            chatRoomDto.setSender_type(messageDto.getIs_from_sender() ? SenderType.FROM : SenderType.TO);

        return chatRoomRepository
                .save(chatRoomDto.asChatRoomEntity())
                .asChatRoomDto();
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

    public ChatRoomEntity getChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(() -> new NoSuchElementException("chat not found with id: " + id));
    }

    @Transactional
    public void LetMessageRead(long chat_id)
    {
        List<MessageEntity> messages = messageRepository.findByChatRoomEntity_ChatId(chat_id);
        if(messages == null || messages.isEmpty()) return;

        for(MessageEntity message : messages)
        {
            message.setIs_read(true);
            messageRepository.save(message);
        }
    }
}
