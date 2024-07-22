package com.ssafy.signal.chat.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {
    private final Map<Long, Set<Long>> roomMembers = new ConcurrentHashMap<>();

    public void addUserToRoom(Long userId, Long chatId) {
        roomMembers.computeIfAbsent(chatId, k -> new HashSet<>()).add(userId);
    }

    public void removeUserFromRoom(Long userId, Long chatId) {
        Set<Long> users = roomMembers.get(userId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                roomMembers.remove(chatId);
            }
        }
    }

    public boolean isUserInRoom(Long userId, Long chatId) {
        return roomMembers.getOrDefault(chatId, new HashSet<>()).contains(userId);
    }
}
