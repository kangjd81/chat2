package com.websocket.chat.repo;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, String> valueOps;

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public List<ChatRoom> findRoomById(String userId) {
        List<ChatRoom> chatRooms = this.findAllRoom();
        return chatRooms.stream().filter(chatRoom -> chatRoom.getUsers().contains(userId)).collect(Collectors.toList());
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }


    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String userId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, userId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String userId) {
        return hashOpsEnterInfo.get(ENTER_INFO, userId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String userId) {
        hashOpsEnterInfo.delete(ENTER_INFO, userId);
    }

    

    public void deleteAll(){
        List<ChatRoom> chatRooms = this.findAllRoom();
        chatRooms.stream().forEach(chatRoom -> hashOpsChatRoom.delete(CHAT_ROOMS, chatRoom.getRoomId()));
    }


    // 채팅방 유저수 조회
   /* public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }*/


    // 특정 채팅방 조회
    public List<ChatMessage> findMessageList(String roomId) {
        ChatRoom chatRoom = hashOpsChatRoom.get(CHAT_ROOMS, roomId);
        List<ChatMessage> chatMessages = chatRoom.getChatMessages();
        Collections.reverse(chatMessages);
        return chatMessages;
    }

    // 대화 저장
    public void saveChatMessage(ChatMessage chatMessage) {
        ChatRoom chatRoom = hashOpsChatRoom.get(CHAT_ROOMS, chatMessage.getRoomId());
        chatRoom.getChatMessages().add(chatMessage);
        createChatRoom(chatRoom);
    }

}
