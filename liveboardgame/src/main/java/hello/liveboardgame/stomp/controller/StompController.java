package hello.liveboardgame.stomp.controller;

import hello.liveboardgame.Greeting;
import hello.liveboardgame.room.service.RoomService;
import hello.liveboardgame.stomp.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StompController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final RoomService roomService;

    @MessageMapping("/sendMessage")
//    @SendTo("/topic/messages")
    public void gameRoomResponseController(/*RoomMessage*/@Payload Map<String, String> message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
//        log.info("id={}", message.getId());
//        log.info("msg={}", message.getMsg());
//        log.info("roomId={}", message.getRoomId());
        log.info("message.text = {}", message.get("text"));
        Greeting greeting = new Greeting("일단 text받긴함");
        //연결된 소켓의 세션정보를 얻음
        log.info("sessionID={}", headerAccessor.getSessionId());
//        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
        messagingTemplate.convertAndSend("/topic/" + /*message.getRoomId()*/"1", greeting);
    }

    @MessageMapping("/enterRoom/{roomId}")
    @SendTo("/topic/{roomId}")
    public Greeting enterRoomController(@DestinationVariable Long roomId, UserInfoDto userInfo) {
        log.info("call enterRoomController() roomId={}", roomId);
        Integer roomUserCnt = roomService.enterRoom(roomId, userInfo);

        if (roomUserCnt == 2) {
            return new Greeting("게임 시작");
        }

//        messagingTemplate.convertAndSend("/topic/" + /*message.getRoomId()*/"1", greeting);
        return new Greeting("");
    }

    @MessageMapping("/exitRoom/{roomId}")
    @SendTo("/topic/{roomId}")
    public Greeting exitRoomController(@DestinationVariable Long roomId, UserInfoDto userInfo) {
        log.info("call exitRoomController() roomId={}", roomId);
        roomService.exitRoom(roomId);
        Greeting greeting = new Greeting("일단 text받긴함");
//        messagingTemplate.convertAndSend("/topic/" + /*message.getRoomId()*/"1", greeting);
        return greeting;
    }



}
