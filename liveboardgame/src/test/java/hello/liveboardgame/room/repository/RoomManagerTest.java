package hello.liveboardgame.room.repository;

import hello.liveboardgame.room.domain.Room;
import hello.liveboardgame.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RoomManagerTest {

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomManager roomManager;

    @BeforeEach
    void before() {
        init();
    }

    @Test
    void init() {
        //roomManager 생성시 초기화가 되어 있어야함
        roomManager.init();
        roomCntValidTest(100, 0, 0);
    }

    @Test
    void getAvailableRoomsCount() {
        //default
        Integer availableRoomsCount = roomManager.getAvailableRoomsCount();
        assertThat(availableRoomsCount).isEqualTo(100);

        //when
        roomManager.enterAvailableRoom(1L, new User());
        roomManager.enterAvailableRoom(2L, new User());

        //then
        Integer availableRoomsCount2 = roomManager.getAvailableRoomsCount();
        assertThat(availableRoomsCount2).isEqualTo(98);
    }

    @Test
    void getWaitingRoomsCount() {
        Integer waitingRoomsCount1 = roomManager.getWaitingRoomsCount();
        assertThat(waitingRoomsCount1).isEqualTo(0);

        roomManager.enterAvailableRoom(1L, new User());
        Integer waitingRoomsCount2 = roomManager.getWaitingRoomsCount();
        assertThat(waitingRoomsCount2).isEqualTo(1);
    }

    @Test
    void getFullRoomsCount() {
        Integer fullRoomsCount1 = roomManager.getFullRoomsCount();
        assertThat(fullRoomsCount1).isEqualTo(0);

        roomManager.enterAvailableRoom(1L, new User());
        roomManager.enterWaitingRoom(1L, new User());
        Integer fullRoomsCount2 = roomManager.getFullRoomsCount();
        assertThat(fullRoomsCount2).isEqualTo(1);
    }

    @Test
    void getRoomId() {
        //available Rooms에서 roomId를 가져오는 케이스
        long roomId1 = roomManager.getRoomId().getAsLong();
        Assertions.assertThat(roomId1).isEqualTo(1L);
        //waiting rooms에서 roomId를 가져오는 케이스
        roomManager.enterAvailableRoom(roomId1, new User("a", "a", roomId1));
        long roomId2 = roomManager.getRoomId().getAsLong();
        Assertions.assertThat(roomId2).isEqualTo(1L);
        //1L room의 user가 가득차 roomId 2L가 반환되는 케이스
        roomManager.enterWaitingRoom(roomId2, new User("b", "b", roomId2));
        long roomId3 = roomManager.getRoomId().getAsLong();
        Assertions.assertThat(roomId3).isEqualTo(2L);
        //waiting rooms에서 roomId 2를 가져오는 케이스
        roomManager.enterAvailableRoom(roomId3, new User("a", "a", roomId3));
        long roomId4 = roomManager.getRoomId().getAsLong();
        Assertions.assertThat(roomId4).isEqualTo(2L);
        //2L room의 user가 가득차 roomId 3L가 반환되는 케이스
        roomManager.enterWaitingRoom(roomId4, new User("b", "b", roomId4));
        long roomId5 = roomManager.getRoomId().getAsLong();
        Assertions.assertThat(roomId5).isEqualTo(3L);

    }


    void roomCntValidTest(int availableRoomCnt, int watingRoomsCnt, int fullRoomsCnt) {
        assertThat(roomManager.getAvailableRoomsCount()).isEqualTo(availableRoomCnt);
        assertThat(roomManager.getWaitingRoomsCount()).isEqualTo(watingRoomsCnt);
        assertThat(roomManager.getFullRoomsCount()).isEqualTo(fullRoomsCnt);
    }

    @Test
    void isContainsWaitingRooms() {
        boolean containsWaitingRooms = roomManager.isContainsWaitingRooms(1L);
        assertThat(containsWaitingRooms).isEqualTo(false);

        //when
        roomManager.enterAvailableRoom(1L, new User());
        //then
        boolean containsWaitingRooms2 = roomManager.isContainsWaitingRooms(1L);
        assertThat(containsWaitingRooms2).isEqualTo(true);
    }

    @Test
    void isContainsAvailableRooms() {

        //success
        boolean containsAvailableRooms2 = roomManager.isContainsAvailableRooms(1L);
        assertThat(containsAvailableRooms2).isEqualTo(true);
        //fail
        roomManager.enterAvailableRoom(1L, new User());
        roomManager.enterWaitingRoom(1L, new User());
        boolean containsAvailableRooms1 = roomManager.isContainsAvailableRooms(1L);
        assertThat(containsAvailableRooms1).isEqualTo(false);
    }

    @Test
    void isContainsFullRooms() {
        //success
        boolean containsFullRooms1 = roomManager.isContainsFullRooms(1L);
        assertThat(containsFullRooms1).isEqualTo(false);
        //fail
        roomManager.enterAvailableRoom(1L, new User());
        roomManager.enterWaitingRoom(1L, new User());
        boolean containsFullRooms2 = roomManager.isContainsFullRooms(1L);
        assertThat(containsFullRooms2).isEqualTo(true);
    }

    @Test
    void enterWaitingRoom() {
        //when
        roomManager.enterAvailableRoom(1L, new User());
        roomManager.enterWaitingRoom(1L, new User());
        //then
        Integer fullRoomsCount = roomManager.getFullRoomsCount();
        assertThat(fullRoomsCount).isEqualTo(1);
    }

    @Test
    void enterAvailableRoom() {
        //when
        roomManager.enterAvailableRoom(1L, new User());
        //then
        Integer waitingRoomsCount = roomManager.getWaitingRoomsCount();
        assertThat(waitingRoomsCount).isEqualTo(1);
    }

    @Test
    void exitFullRoom() {
        //given
        roomManager.enterAvailableRoom(1L, new User());
        roomManager.enterWaitingRoom(1L, new User());
        Integer fullRoomsCount = roomManager.getFullRoomsCount();
        assertThat(fullRoomsCount).isEqualTo(1);
        //when
        roomManager.exitFullRoom(1L);
        //then
        roomCntValidTest(100, 0, 0);
    }

    @Test
    void exitWaitingRoom() {
        //given
        roomManager.enterAvailableRoom(1L, new User());
        //when
        Integer waitingRoomsCount = roomManager.getWaitingRoomsCount();
        //then
        assertThat(waitingRoomsCount).isEqualTo(1);
    }

    @Test
    void getRoomUserCount() {
        //given
        Room findRoom = roomManager.getRoom(1L);
        //when
        List<User> users = findRoom.getUsers();
        users.add(new User());
        users.add(new User());
        //then
        Integer roomUserCount = roomManager.getRoomUserCount(1L);
        assertThat(roomUserCount).isEqualTo(2);
    }

    @Test
    void getRoom() {
        Room room = roomManager.getRoom(1L);
        assertThat(room).isNotNull();
    }
}

