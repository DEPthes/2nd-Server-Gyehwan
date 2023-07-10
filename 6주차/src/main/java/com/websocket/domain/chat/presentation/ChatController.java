package com.websocket.domain.chat.presentation;

import com.websocket.domain.chat.ChatRoomRepository;
import com.websocket.domain.chat.RedisPublisher;
import com.websocket.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

//    private final SimpMessageSendingOperations messagingTemplate;

//    @MessageMapping("/chat/message")
//    public void message(ChatMessage message) {
//        if (message.getType().equals(ChatMessage.MessageType.ENTER)) {
//            message.setMessage(message.getSender() + "님이 입장하셨습니다. 👋🏼");
//        }
//        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
//    }
}
