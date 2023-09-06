package hello.liveboardgame.gameInfo.repository;

import hello.liveboardgame.gameInfo.domain.GameInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class GameInfoRepository {
    private static Map<Long, GameInfo> store = new HashMap<>();

    public GameInfo findById(Long id) {
        return store.get(id);
    }

    public void save(GameInfo gameInfo) {
        store.put(gameInfo.getId(), gameInfo);
    }
}
