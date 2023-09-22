package hello.liveboardgame.stomp.controller;

import hello.liveboardgame.Greeting;
import hello.liveboardgame.gameInfo.service.GameInfoService;
import hello.liveboardgame.room.dto.GameOutcomeDto;
import hello.liveboardgame.stomp.dto.GameInfoDto;
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
    private final GameInfoService gameInfoService;

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
            String selectedPlayerName = roomService.selectRandomStartingPlayer(roomId);
            return new Greeting("start", selectedPlayerName);
        }
        return new Greeting("", "");
    }

    /**
     * 클라이언트가 보낸 좌표값을 저장하고 게임결과 Broadcast
     * @param roomId
     * @param gameInfoDto
     * @return
     */
    @MessageMapping("/gameboard/{roomId}")
    @SendTo("/topic/gameboard/{roomId}")
    public GameInfoDto CoordinateUpdateController(@DestinationVariable Long roomId, GameInfoDto gameInfoDto) {
        log.info("CoordinateUpdateController gameInfoDto={}", gameInfoDto);

        //좌표정보 저장
        gameInfoService.saveGameInfo(roomId, gameInfoDto);
        //게임결과 반환
        GameOutcomeDto gameResult = roomService.getGameResult(roomId, gameInfoDto);
        gameInfoDto.setGameState(gameResult.getStatue().getStateCode());
        return gameInfoDto;
    }
}
