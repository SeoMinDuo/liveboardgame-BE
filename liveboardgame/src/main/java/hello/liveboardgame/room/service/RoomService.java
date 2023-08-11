package hello.liveboardgame.room.service;

import hello.liveboardgame.room.domain.User;
import hello.liveboardgame.room.repository.RoomManager;
import hello.liveboardgame.stomp.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomManager roomManager;

    public Long getRoomId() {
        return roomManager.getRoomId().orElse(-1);
    }

    /**
     * user가 room에 입장
     * @param roomId
     * @param userInfo
     * @return 방에 인원 수
     */
    public Integer enterRoom(Long roomId, UserInfoDto userInfo) {

        if (roomManager.isContainsWatingRooms(roomId)) {
            log.info("enterRoom() : userName={}이 watingRoom에 입장 roomId={}", userInfo.getName(), roomId);
            User user = new User(userInfo.getName(), roomId);
            roomManager.enterWaitingRoom(roomId, user);

        } else if (roomManager.isContainsAvailableRooms(roomId)) {
            log.info("enterRoom() : userName={}이 availableRoom에 입장 roomId={}", userInfo.getName(), roomId);
            User user = new User(userInfo.getName(), roomId);
            roomManager.enterAvailableRoom(roomId, user);

        } else {
            log.info("enterRoom() : userName={} 남은 방이 없음 roomId={}",userInfo.getName(), roomId);
        }
        return roomManager.getRoomUserCount(roomId);
    }

    public void exitRoom(Long roomId) {
        if (roomManager.isContainsFullRooms(roomId)) {
            log.info("exitRoom() : fullRoom 퇴장 roomid={}", roomId);
            roomManager.exitFullRoom(roomId);
        } else if (roomManager.isContainsWatingRooms(roomId)) {
            log.info("exitRoom() : waitingRoom 퇴장 roomid={}", roomId);
            roomManager.exitWaitingRoom(roomId);
        } else if (roomManager.isContainsAvailableRooms(roomId)) {
            log.info("exitRoom() : 해당 Room에는 아무도 없음 roomId={}", roomId);
        }
    }
}
