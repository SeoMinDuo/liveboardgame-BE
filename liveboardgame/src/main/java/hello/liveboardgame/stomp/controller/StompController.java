package hello.liveboardgame.stomp.controller;

import hello.liveboardgame.Greeting;
import hello.liveboardgame.gameInfo.service.GameInfoService;
import hello.liveboardgame.stomp.dto.GameInfoDto;
import hello.liveboardgame.user.domain.User;
import hello.liveboardgame.room.service.RoomService;
import hello.liveboardgame.stomp.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StompController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final RoomService roomService;
    private final GameInfoService gameInfoService;

//    @MessageMapping("/sendMessage")
////    @SendTo("/topic/messages")
//    public void gameRoomResponseController(/*RoomMessage*/@Payload Map<String, String> message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
////        log.info("id={}", message.getId());
////        log.info("msg={}", message.getMsg());
////        log.info("roomId={}", message.getRoomId());
//        log.info("message.text = {}", message.get("text"));
//        Greeting greeting = new Greeting("일단 text받긴함");
//        //연결된 소켓의 세션정보를 얻음
//        log.info("sessionID={}", headerAccessor.getSessionId());
////        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
//        messagingTemplate.convertAndSend("/topic/" + /*message.getRoomId()*/"1", greeting);
//    }

    /**
     * 클라이언트 인게임 입장처리
     * @param roomId
     * @param userInfo
     * @param headerAccessor
     * @return
     */

//    @MessageMapping("/sendMessage")
////    @SendTo("/topic/messages")
//    public void gameRoomResponseController(/*RoomMessage*/@Payload Map<String, String> message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
////        log.info("id={}", message.getId());
////        log.info("msg={}", message.getMsg());
////        log.info("roomId={}", message.getRoomId());
//        log.info("message.text = {}", message.get("text"));
//        Greeting greeting = new Greeting("일단 text받긴함");
//        //연결된 소켓의 세션정보를 얻음
//        log.info("sessionID={}", headerAccessor.getSessionId());
////        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
//        messagingTemplate.convertAndSend("/topic/" + /*message.getRoomId()*/"1", greeting);
//    }

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

    /**
     * 클라이언트가 보낸 좌표값을 저장하고 Broadcast
     * @param roomId
     * @param gameInfoDto
     * @return
     */
    @MessageMapping("/gameboard/{roomId}")
    @SendTo("/topic/gameboard/{roomId}")
    public GameInfoDto CoordinateUpdateController(@DestinationVariable Long roomId, GameInfoDto gameInfoDto) {
        log.info("CoordinateUpdateController gameInfoDto={}", gameInfoDto);
        gameInfoService.saveGameInfo(roomId, gameInfoDto);

        String user1 = "user1";
        String user2 = "user2";
        String name = gameInfoDto.equals(user1)? user1 : user2;
        new GameInfoDto(gameInfoDto.getX(), gameInfoDto.getY(), name);


        return gameInfoDto;
    }

    /**
     * 클라이언트가 보낸 좌표값을 저장하고 Broadcast
     * @param roomId
     * @param gameInfoDto
     * @return
     */
    @MessageMapping("/gameboard/{roomId}")
    @SendTo("/topic/gameboard/{roomId}")
    public GameInfoDto CoordinateUpdateController(@DestinationVariable Long roomId, GameInfoDto gameInfoDto) {
        log.info("CoordinateUpdateController gameInfoDto={}", gameInfoDto);
        gameInfoService.saveGameInfo(roomId, gameInfoDto);

        String user1 = "user1";
        String user2 = "user2";
        String name = gameInfoDto.equals(user1)? user1 : user2;
        new GameInfoDto(gameInfoDto.getX(), gameInfoDto.getY(), name);


        return gameInfoDto;
    }

}
