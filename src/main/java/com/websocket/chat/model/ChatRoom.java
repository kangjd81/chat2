package com.websocket.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private String name;
    private List<String> users;
    private List<ChatMessage> chatMessages;
    private long notReadCount; // 않읽은 메시지

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        chatRoom.users = new ArrayList<>();

        return chatRoom;
    }

    public int getMessagesUnRead(String name){
        return chatMessages.stream()
                .filter(chatMessage -> !chatMessage.getSender().equals(name))
                .mapToInt(ChatMessage::getRead).sum();
    }
}
