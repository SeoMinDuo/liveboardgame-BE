package hello.liveboardgame.room.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RoomManagerTest {

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomManager roomManager;

    @Test
    void init() {
        //roomManager 생성시 초기화가 되어 있어야함
        roomCntValidTest(100, 0, 0);

        //when
        OptionalLong roomId1 = roomManager.getRoomId();
        roomCntValidTest(99, 1, 0);
        OptionalLong roomId2 = roomManager.getRoomId();
        roomCntValidTest(99, 0, 1);
        OptionalLong roomId3 = roomManager.getRoomId();
        roomCntValidTest(98, 1, 1);
        roomManager.init();

        //then
        roomCntValidTest(100, 0, 0);
    }

    @Test
    void getRoomId() {

        OptionalLong roomId1 = roomManager.getRoomId();
        System.out.println(roomId1);
        roomCntValidTest(99, 1, 0);

        OptionalLong roomId2 = roomManager.getRoomId();
        roomCntValidTest(99, 0, 1);

        OptionalLong roomId3 = roomManager.getRoomId();
        roomCntValidTest(98, 1, 1);
    }


    void roomCntValidTest(int availableRoomCnt, int watingRoomsCnt, int fullRoomsCnt) {
        assertThat(roomManager.getAvailableRoomsCount()).isEqualTo(availableRoomCnt);
        assertThat(roomManager.getWaitingRoomsCount()).isEqualTo(watingRoomsCnt);
        assertThat(roomManager.getFullRoomsCount()).isEqualTo(fullRoomsCnt);
    }
}