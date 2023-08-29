package hello.liveboardgame.gameInfo.service;

import hello.liveboardgame.gameInfo.domain.GameInfo;
import hello.liveboardgame.gameInfo.repository.GameInfoRepository;
import hello.liveboardgame.room.domain.Room;
import hello.liveboardgame.room.repository.RoomManager;
import hello.liveboardgame.stomp.dto.GameInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameInfoService {

    private final GameInfoRepository gameInfoRepository;
    private final RoomManager roomManager;

    public void saveGameInfo(Long roomId, GameInfoDto gameInfoDto) {
        Room room = roomManager.getRoom(roomId);

        String gameId = room.getGameId();
        Integer x = gameInfoDto.getX();
        Integer y = gameInfoDto.getY();
        Integer ord = room.getOrder();
        String userName = gameInfoDto.getName();

        GameInfo gameInfo = new GameInfo(gameId, roomId, x, y, ord, userName);
        log.info("GameInfo 저장완료 {}", gameInfo);
        gameInfoRepository.save(gameInfo);
    }


}
