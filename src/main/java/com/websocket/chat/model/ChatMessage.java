package com.websocket.chat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChatMessage implements Serializable {

    public ChatMessage() {

    }

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, int read) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.read = read;
    }

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK, AUTO
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private int read=1; // 읽음여부 (0:읽음, 1:읽지 않음)
}
