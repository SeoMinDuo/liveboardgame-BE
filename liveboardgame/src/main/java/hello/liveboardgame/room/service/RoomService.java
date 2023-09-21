package hello.liveboardgame.room.service;

import hello.liveboardgame.room.dto.GameOutcomeDto;
import hello.liveboardgame.room.domain.Room;
import hello.liveboardgame.stomp.dto.GameInfoDto;
import hello.liveboardgame.user.domain.User;
import hello.liveboardgame.room.repository.RoomManager;
import hello.liveboardgame.user.repository.GameUserManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomManager roomManager;
    private final GameUserManager gameUserManager;

    public Long getRoomId() {
        return roomManager.getRoomId().orElse(-1);
    }

    /**
     * user가 room에 입장
     * @param roomId
     * @param user
     * @return 방에 인원 수
     */
    public Integer enterRoom(Long roomId, User user) {

        if (roomManager.isContainsWaitingRooms(roomId)) {
            log.info("enterRoom() : userName={}이 watingRoom에 입장 roomId={}", user.getName(), roomId);
            roomManager.enterWaitingRoom(roomId, user);

        } else if (roomManager.isContainsAvailableRooms(roomId)) {
            log.info("enterRoom() : userName={}이 availableRoom에 입장 roomId={}", user.getName(), roomId);
            roomManager.enterAvailableRoom(roomId, user);
        } else {
            log.info("enterRoom() : userName={} 남은 방이 없음 roomId={}",user.getName(), roomId);
        }
        return roomManager.getRoomUserCount(roomId);
    }

    public Integer enterRoomForBlueRedControl(Long roomId, User user, int colorNum) {

        if (roomManager.isContainsWaitingRooms(roomId)) {
            log.info("enterRoom() : userName={}이 watingRoom에 입장 roomId={}", user.getName(), roomId);
            roomManager.enterWaitingRoom(roomId, user);

        } else if (roomManager.isContainsAvailableRooms(roomId)) {
            log.info("enterRoom() : userName={}이 availableRoom에 입장 roomId={}", user.getName(), roomId);
            roomManager.enterAvailableRoom(roomId, user);
        } else {
            log.info("enterRoom() : userName={} 남은 방이 없음 roomId={}",user.getName(), roomId);
        }
        if (colorNum == 1) roomManager.getRoom(roomId).setBlueUser(user);
        else if (colorNum == 2) roomManager.getRoom(roomId).setRedUser(user);

        return roomManager.getRoomUserCount(roomId);
    }

    public void exitRoom(String sessionId) {
        User findUser = gameUserManager.findBySessionId(sessionId);
        if (findUser == null) {
            logRoomStatus();
            return;
        }
        Long findUserRoomId = findUser.getRoomId();//user가 속해있는 room의 id

        if (roomManager.isContainsFullRooms(findUserRoomId)) {
            log.info("exitRoom() : fullRoom 퇴장 roomid={}", findUserRoomId);
            roomManager.exitFullRoom(findUserRoomId);
        } else if (roomManager.isContainsWaitingRooms(findUserRoomId)) {
            log.info("exitRoom() : waitingRoom 퇴장 roomid={}", findUserRoomId);
            roomManager.exitWaitingRoom(findUserRoomId);
        } else if (roomManager.isContainsAvailableRooms(findUserRoomId)) {
            log.info("exitRoom() : 해당 Room에는 아무도 없음 roomId={}", findUserRoomId);
        }
    }

    public void logRoomStatus() {
        log.info("[logRoomStatus] available:{} wating:{} full:{}",
                roomManager.getAvailableRoomsCount(),
                roomManager.getWaitingRoomsCount(),
                roomManager.getFullRoomsCount()
        );
    }

    public String selectRandomStartingPlayer(Long roomId) {
        List<User> users = roomManager.getRoom(roomId).getUsers();
        double random = Math.random() * 10;
        System.out.println("random = " + random);
        int randomIndex = (int) (random % 2);
        System.out.println("randomIndex = " + randomIndex);
        return users.get(randomIndex).getName();
    }

    public GameOutcomeDto getGameResult(Long roomId, GameInfoDto gameInfoDto) {
        Room room = roomManager.getRoom(roomId);
        return room.getGameResult(gameInfoDto);
    }
}
