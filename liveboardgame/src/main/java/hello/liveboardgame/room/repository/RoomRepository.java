package hello.liveboardgame.room.repository;

import hello.liveboardgame.room.domain.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class RoomRepository {

    private static final Map<Long, Room> store = new HashMap<>();

    private static long remainRoomCnt = 100L;

    //방의 개수를 100개로 제한
    static {
        for (long i = 1; i <= 100; i++) {
            Room room = new Room(i);
            store.put(room.getId(), room);
        }
    }

    public Room findById(Long id) {
        return store.get(id);
    }

    public List<Room> findAll() {
        return new ArrayList<>(store.values());
    }

    public void resetAll() {
        store.clear();
        for (long i = 1; i <= 100; i++) {
            Room room = new Room(i);
            store.put(room.getId(), room);
        }
    }
}
