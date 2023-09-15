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
    public void deleteAllGameInfo() {
        gameInfoRepository.deleteAll();
    }

    public void saveGameInfo(Long roomId, GameInfoDto gameInfoDto) {

        int x = gameInfoDto.getX();
        int y = gameInfoDto.getY();
        if (x == -1 && y == -1) return;

        Room room = roomManager.getRoom(roomId);

        GameInfo gameInfo = generateGameInfo(gameInfoDto, room);
        log.info("GameInfo 저장완료 {}", gameInfo);
        gameInfoRepository.save(gameInfo);
        room.getGameInfoList().add(gameInfo);
    }

    private static GameInfo generateGameInfo(GameInfoDto gameInfoDto, Room room) {
        String gameId = room.getGameId();
        Long roomId = room.getId();
        int x = gameInfoDto.getX();
        int y = gameInfoDto.getY();
        Integer ord = room.getOrder();
        String userName = gameInfoDto.getName();

        return new GameInfo(gameId, roomId, x, y, ord, userName);
    }
}
