package hello.liveboardgame.room.repository;

import hello.liveboardgame.room.domain.Room;
import hello.liveboardgame.room.domain.User;
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
            return OptionalLong.of(rooms.get(0).getId());
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
                return OptionalLong.of(rooms.get(0).getId());
            }
        }
    }

    /**
     * watingRooms에 해당 room이 존재하는지
     * @param roomId
     * @return
     */
    public boolean isContainsWatingRooms(Long roomId) {
        if (waitingRooms.containsKey(roomId)) {
            return true;
        }
        return false;
    }

    public boolean isContainsAvailableRooms(Long roomId) {
        if (availableRooms.containsKey(roomId)) {
            return true;
        }
        return false;
    }

    public boolean isContainsFullRooms(Long roomId) {
        if (fullRooms.containsKey(roomId)) {
            return true;
        }
        return false;
    }

    public void enterWaitingRoom(Long roomId, User user) {
        if (isContainsWatingRooms(roomId)) {
            Room findRoom = waitingRooms.get(roomId);
            List<User> roomUsers = findRoom.getUsers();
            roomUsers.add(user);

            if (roomUsers.size() == 2) {
                waitingRooms.remove(roomId);
                fullRooms.put(findRoom.getId(), findRoom);
            }
        }
    }

    public void enterAvailableRoom(Long roomId, User user) {

        if (isContainsAvailableRooms(roomId)) {
            Room findRoom = availableRooms.get(roomId);
            List<User> roomUsers = findRoom.getUsers();
            roomUsers.add(user);

            if (roomUsers.size() == 1) {
                availableRooms.remove(roomId);
                waitingRooms.put(findRoom.getId(), findRoom);
            }
        }
    }

    public void exitFullRoom(Long roomId) {
        if (isContainsFullRooms(roomId)) {
            Room room = fullRooms.get(roomId);
            room.getUsers().clear();

            fullRooms.remove(room.getId());
            availableRooms.put(room.getId(), room);
        }
    }

    public void exitWaitingRoom(Long roomId) {
        if (isContainsWatingRooms(roomId)) {
            Room room = waitingRooms.get(roomId);
            room.getUsers().clear();

            waitingRooms.remove(room.getId());
            availableRooms.put(room.getId(), room);
        }
    }

    public Integer getRoomUserCount(Long roomId) {
        Room findRoom = repository.findById(roomId);
        return findRoom.getUsers().size();
    }
}
