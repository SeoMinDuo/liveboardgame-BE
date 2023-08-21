package hello.liveboardgame.stomp.controller;

import hello.liveboardgame.Greeting;
import hello.liveboardgame.user.domain.User;
import hello.liveboardgame.room.service.RoomService;
import hello.liveboardgame.stomp.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StompController {
    private final RoomService roomService;

    /**
     * 클라이언트 인게임 입장처리
     * @param roomId
     * @param userInfo
     * @param headerAccessor
     * @return
     */
    @MessageMapping("/enterRoom/{roomId}")
    @SendTo("/topic/{roomId}")
    public Greeting enterRoomController(@DestinationVariable Long roomId, UserInfoDto userInfo, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        User user = new User(sessionId, userInfo.getName(), roomId);

        Integer roomUserCnt = roomService.enterRoom(roomId, user);

        log.info("call enterRoomController() roomId={}", roomId);
        log.info("enterRoomController : userInfo={}", userInfo.getName());
        log.info("enterRoomController : sessionID={}", sessionId);

        if (roomUserCnt == 2) {
            roomService.logRoomStatus();
            return new Greeting("start", userInfo.getName());
        }

//        messagingTemplate.convertAndSend("/topic/" + /*message.getRoomId()*/"1", greeting);
        return new Greeting("", "");
    }
}
