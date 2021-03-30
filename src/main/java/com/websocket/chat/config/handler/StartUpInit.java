package com.websocket.chat.config.handler;

import com.websocket.chat.model.ChatRoom;
import com.websocket.chat.repo.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartUpInit {
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @PostConstruct
    public void init(){
        // 1. 기존 대화방 및 대화 삭제
        // chatRoomRepository.deleteAll();

        ChatRoom room = ChatRoom.create("room1");


        /*
        2. 대화방 2개 개설
         - user-admin
         - user-guest
        3. 대화 생성 및 저장

         */
    }
}