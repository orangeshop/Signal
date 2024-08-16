package com.ssafy.signal.chat.service;

import com.ssafy.signal.chat.domain.*;
import com.ssafy.signal.chat.repository.ChatRoomRepository;
import com.ssafy.signal.chat.repository.MessageRepository;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.file.service.FileService;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.notification.service.FirebaseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final FirebaseService firebaseService;
    private final FileRepository fileRepository;

    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) {
        Member from = makeMember(chatRoomDto.getFrom_id());
        Member to = makeMember(chatRoomDto.getTo_id());

        ChatRoomEntity chatRoom = chatRoomRepository.findChatRoomsByUserId(from,to);
        chatRoom = chatRoom == null ? chatRoomRepository.findChatRoomsByUserId(to,from) : chatRoom;

        if(chatRoom != null) return chatRoom.asChatRoomDto();

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
            Member from = chatRoom.getFrom_id();
            Member to = chatRoom.getTo_id();

            String from_name = from.getName();
            String to_name = to.getName();

            String from_url = Optional
                    .ofNullable(fileRepository.findAllByUser(from))
                    .orElse(new FileEntity())
                    .getFileUrl();

            String to_url = Optional
                    .ofNullable(fileRepository.findAllByUser(to))
                    .orElse(new FileEntity())
                    .getFileUrl();

            boolean is_from_sender = chatRoom.getFrom_id().getUserId() == user_id;

            int cnt = messageRepository.countUnreadMessageByUserId(is_from_sender,chatRoom);
            chatRoomDtos.add(chatRoom.asChatRoomDto(from_name,to_name,from_url,to_url,cnt));
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
    public void LetMessageRead(long chat_id,long user_id)
    {
        ChatRoomEntity chatRoom = getChatRoomById(chat_id);
        boolean isFrom = chatRoom.getFrom_id().getUserId() == user_id;

        List<MessageEntity> messages = messageRepository.findByChatRoomEntity_ChatId(chat_id);
        if(messages == null || messages.isEmpty()) return;

        for(MessageEntity message : messages)
        {
            if(message.asMessageDto().getIs_from_sender() == isFrom ) continue;
            message.setIs_read(true);
            messageRepository.save(message);
        }
    }

    public void sendMsgNotification(MessageDto messageDto)throws IOException {
        ChatRoomDto chat = chatRoomRepository
                .findById(messageDto.getChat_id())
                .orElseThrow()
                .asChatRoomDto();

        Member from = makeMember(chat.getFrom_id());
        Member to = makeMember(chat.getTo_id());

        boolean isFromSender = messageDto.getIs_from_sender();

        long destUserId = isFromSender ? to.getUserId() : from.getUserId();
        String destUserName = isFromSender ? from.getName() : to.getName();
        destUserName += " " + (isFromSender ? from.getUserId() : to.getUserId());
        firebaseService.sendMessageTo(destUserId, destUserName,messageDto.getContent(),0);
    }

    public void deleteChatRoom(long chat_id){
        chatRoomRepository.deleteById(chat_id);
    }

    private Member makeMember(long user_id){
        return memberRepository
                .findById(user_id)
                .orElseThrow();
    }
}
