package hello.liveboardgame.room.repository;

import hello.liveboardgame.room.domain.Room;
import hello.liveboardgame.user.domain.User;
import hello.liveboardgame.user.repository.GameUserManager;
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
    private final GameUserManager gameUserManager;

    private static final Map<Long, Room> availableRooms = new HashMap<>();
    private static final Map<Long, Room> waitingRooms = new HashMap<>();
    private static final Map<Long, Room> fullRooms = new HashMap<>();


    @PostConstruct
    public void init() {
        log.info("init() 실행");
        repository.resetAll();
        availableRooms.clear();
        waitingRooms.clear();
        fullRooms.clear();
        List<Room> roomList = repository.findAll();
        for (Room room : roomList) {
            availableRooms.put(room.getId(), room);
        }
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
    public boolean isContainsWaitingRooms(Long roomId) {
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
        if (isContainsWaitingRooms(roomId)) {
            Room findRoom = waitingRooms.get(roomId);
            Integer roomUserSize = insertRoomUser(findRoom, user);

            if (roomUserSize == 2) {
                waitingRooms.remove(roomId);
                fullRooms.put(findRoom.getId(), findRoom);
            }
        }
    }

    public void enterAvailableRoom(Long roomId, User user) {

        if (isContainsAvailableRooms(roomId)) {
            Room findRoom = availableRooms.get(roomId);
            Integer roomUserSize = insertRoomUser(findRoom, user);

            if (roomUserSize == 1) {
                availableRooms.remove(roomId);
                waitingRooms.put(findRoom.getId(), findRoom);
            }
        }
    }

    /**
     * room과 GameUserManger에 user 추가
     * @param room
     * @param user
     * @return room에 들어있는 user의 수
     */
    private Integer insertRoomUser(Room room, User user) {
        //room정보 세팅
        if (room.getGameId() == null) {
            room.setGameId(UUID.randomUUID().toString());
            room.setIsUsed(Boolean.TRUE);
            room.setOrd(0);
        }
        //인게임 user collection에 추가
        gameUserManager.save(user);
        List<User> roomUsers = room.getUsers();
        roomUsers.add(user);

        return roomUsers.size();
    }

    public void exitFullRoom(Long roomId) {
        if (isContainsFullRooms(roomId)) {
            Room room = fullRooms.get(roomId);
            deleteRoomUser(room);

            fullRooms.remove(room.getId());
            availableRooms.put(room.getId(), room);
        }
    }

    public void exitWaitingRoom(Long roomId) {
        if (isContainsWaitingRooms(roomId)) {
            Room room = waitingRooms.get(roomId);
            deleteRoomUser(room);

            waitingRooms.remove(room.getId());
            availableRooms.put(room.getId(), room);
        }
    }

    private void deleteRoomUser(Room room) {
        //room정보 세팅
        if (room.getGameId() != null) {
            room.setGameId(null);
            room.setIsUsed(Boolean.FALSE);
            room.setOrd(0);
        }
        for (User user : room.getUsers()) {
            gameUserManager.delete(user.getSessionId());
        }
        room.getUsers().clear();
    }

    public Integer getRoomUserCount(Long roomId) {
        Room findRoom = repository.findById(roomId);
        return findRoom.getUsers().size();
    }

    public Room getRoom(Long roomId) {
        return repository.findById(roomId);
    }
}
