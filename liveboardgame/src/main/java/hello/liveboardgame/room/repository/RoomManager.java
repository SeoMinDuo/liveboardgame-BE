package hello.liveboardgame.room.repository;

import hello.liveboardgame.room.domain.Room;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@RequiredArgsConstructor
@Service
@Slf4j
public class RoomManager {

    private final RoomRepository repository;

    private static final Map<Long, Room> availableRooms = new HashMap<>();
    private static final Map<Long, Room> waitingRooms = new HashMap<>();
    private static final Map<Long, Room> fullRooms = new HashMap<>();


    @PostConstruct
    public void init() {
        log.info("init() 실행");
        if (availableRooms.isEmpty()) {
            log.info("availableRooms가 비어있음");
            List<Room> roomList = repository.findAll();
            for (Room room : roomList) {
                availableRooms.put(room.getId(), room);
                log.info("available Room add 성공 room={}", room);
            }
            log.info("availableRoom중 하나 room1 = {}", availableRooms.get(1L).getId());
        } else {
            log.info("availableRooms에 room객체가 존재함 room={}", availableRooms.values());
            availableRooms.clear();
            List<Room> roomList = repository.findAll();
            for (Room room : roomList) {
                availableRooms.put(room.getId(), room);
            }
        }
        waitingRooms.clear();
        fullRooms.clear();
    }

    public Integer getAvailableRoomsCount() {
        return availableRooms.values().stream().toList().size();
    }

    public Integer getWaitingRoomsCount() {
        return waitingRooms.values().stream().toList().size();
    }

    public Integer getFullRoomsCount() {
        return fullRooms.values().stream().toList().size();
    }

    /**
     * 사용가능한 방의 아이디를 반환
     * @return 방id
     */
    public OptionalLong getRoomId() {
        //대기중인 게임방이 존재하는 경우
        if (!waitingRooms.isEmpty()) {
            List<Room> rooms = waitingRooms.values().stream().toList();
            log.info("게임 대기방이 존재하는 경우 요청 roomId={}", rooms.get(0).getId());
            Room room = waitingRooms.remove(rooms.get(0).getId());

            fullRooms.put(room.getId(), room);
            return OptionalLong.of(room.getId());
        } else {
            //사용가능한 방이 존재하지 않는 경우
            if (availableRooms.isEmpty()) {
                log.info("사용 가능한 방이 존재하지 않는 경우");
                return OptionalLong.empty();
            }
            //사용중인 방이 없는 경우
            else {
                List<Room> rooms = availableRooms.values().stream().toList();
                log.info("사용중인 방이 없는 경우 roomId={}", rooms.get(0).getId());
                Room room = availableRooms.remove(rooms.get(0).getId());
                waitingRooms.put(room.getId(), room);
                return OptionalLong.of(room.getId());
            }
        }
    }

}
