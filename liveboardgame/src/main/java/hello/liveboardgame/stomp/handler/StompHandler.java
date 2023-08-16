package hello.liveboardgame.stomp.handler;

import hello.liveboardgame.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.MessageChannel;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final RoomService roomService;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        switch (accessor.getCommand()) {
            //STOMP CONNECT처리
            case CONNECT -> System.out.println("CONNECT sessionId = " + sessionId);

            //STOMP DISCONNECT처리
            case DISCONNECT -> {
                System.out.println("DISCONNCT sessionId = " + sessionId);
                roomService.exitRoom(sessionId);
            }
        }
    }
}
