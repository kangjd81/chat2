package com.websocket.chat.config.handler;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.ChatRoom;
import com.websocket.chat.repo.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class StartUpInit {
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @PostConstruct
    public void init(){
        // 1. 기존 대화방 및 대화 삭제
        chatRoomRepository.deleteAll();

        // 2. 대화방 2개 개설
        //  - user-admin

        ChatRoom room = ChatRoom.create("room1 (user-admin)");
        List<String> users = new ArrayList<>(Arrays.asList("user","admin"));
        room.setUsers(users);

        List<ChatMessage> chatMessages = new ArrayList<>(Arrays.asList(
            ChatMessage.builder()
                    .type(ChatMessage.MessageType.TALK)
                    .sender("admin")
                    .message("안녕하세요 유저님,,,")
                    .read(0)
                    .build()
            ,
            ChatMessage.builder()
                    .type(ChatMessage.MessageType.TALK)
                    .sender("user")
                    .message("네네~ 무슨 일이세요?")
                    .read(0)
                    .build()
        ));
        room.setChatMessages(chatMessages);
        chatRoomRepository.createChatRoom(room);

        //  - user-guest
        room = ChatRoom.create("room2 (user-guest)");
        users = new ArrayList<>(Arrays.asList("user","guest"));
        room.setUsers(users);

        chatMessages = new ArrayList<>(Arrays.asList(
                ChatMessage.builder()
                        .type(ChatMessage.MessageType.TALK)
                        .sender("guest")
                        .message("user님 ,, 모하세요?")
                        .read(1)
                        .build()
        ));
        room.setChatMessages(chatMessages);
        chatRoomRepository.createChatRoom(room);

    }
}