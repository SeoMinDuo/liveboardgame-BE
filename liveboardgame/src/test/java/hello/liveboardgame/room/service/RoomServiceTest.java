package hello.liveboardgame.room.service;

import hello.liveboardgame.gameInfo.domain.GameInfo;
import hello.liveboardgame.gameInfo.repository.GameInfoRepository;
import hello.liveboardgame.gameInfo.service.GameInfoService;
import hello.liveboardgame.room.domain.Room;
import hello.liveboardgame.room.dto.GameOutcomeDto;
import hello.liveboardgame.room.dto.GameResultStatus;
import hello.liveboardgame.room.repository.RoomManager;
import hello.liveboardgame.stomp.dto.GameInfoDto;
import hello.liveboardgame.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RoomServiceTest {

//    @MockBean
//    RoomManager roomManagerMock;
//
//    @MockBean
//    GameUserManager gameUserManagerMock;


    @Autowired
    RoomManager roomManager;

    @Autowired
    RoomService roomService;

    @Autowired
    GameInfoService gameInfoService;

    @Autowired
    GameInfoRepository gameInfoRepository;


    @BeforeEach()
    void init() {
        roomManager.init();
    }

    @Test
//    @Disabled
    void getRoomId() {
        //when
        Long roomId1 = roomService.getRoomId();
        roomService.enterRoom(roomId1, new User());
        Long roomId2 = roomService.getRoomId();
        roomService.enterRoom(roomId2, new User());
        Long roomId3 = roomService.getRoomId();
        roomService.enterRoom(roomId3, new User());
        Long roomId4 = roomService.getRoomId();
        roomService.enterRoom(roomId4, new User());
        //then
        assertThat(roomId1).isEqualTo(1L);
        assertThat(roomId2).isEqualTo(1L);
        assertThat(roomId3).isEqualTo(2L);
        assertThat(roomId4).isEqualTo(2L);
    }

    @Test
    void enterRoom() {
        for (Long i = 1L; i <= 100L; i++) {
            //when
            roomService.enterRoom(i, new User());
            //then
            assertThat(roomManager.getWaitingRoomsCount()).isEqualTo(1);
            //when
            roomService.enterRoom(i, new User());
            //then
            assertThat(roomManager.getFullRoomsCount()).isEqualTo(i.intValue());
        }

    }

    @Test
    void exitRoom() {
        //given
        roomService.enterRoom(1L, new User("234", "u1", 1L));
        assertThat(roomManager.getWaitingRoomsCount()).isEqualTo(1);
        roomService.enterRoom(1L, new User("123", "u2", 1L));
        assertThat(roomManager.getFullRoomsCount()).isEqualTo(1);
        //when
        String sessionId = roomManager.getRoom(1L).getUsers().get(0).getSessionId();
        assertThat(roomManager.getRoom(1L).getUsers().size()).isEqualTo(2);
        roomService.exitRoom("123");
        //then
        assertThat(roomManager.getRoom(1L).getUsers().size()).isEqualTo(0);
        roomCntValidTest(100,0,0);
    }

    @Test
    void logRoomStatus() {
    }

    @Test
    void selectRandomStartingPlayer() {
        //given
        roomService.enterRoom(1L, new User("a", "u1", 1L));
        roomService.enterRoom(1L, new User("b", "u2", 1L));
        Room room = roomManager.getRoom(1L);
        //when
        String name = roomService.selectRandomStartingPlayer(1L);
        //then
        String[] sArr = {"u1", "u2"};
        assertThat(sArr).contains(name);
        assertThat(sArr).contains(room.getBlueUser().getName());
        System.out.println("room.getBlueUser().getName() = " + room.getBlueUser().getName());
        assertThat(sArr).contains(room.getRedUser().getName());
        System.out.println("room.getRedUser().getName() = " + room.getRedUser().getName());
    }

    @Test
    @DisplayName("무승부 테스트")
    void getGameResult_V1() {
        //given
        //room입장
        roomService.enterRoomForBlueRedControl(1L, new User("a", "u1", 1L), 1);
        roomService.enterRoomForBlueRedControl(1L, new User("b", "u2", 1L), 2);
        //게임 시나리오 저장
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 2, "u1"));//1
        gameInfoService.saveGameInfo(1L, new GameInfoDto(1, 6, "u2"));//2
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 1, "u1"));//3
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 6, "u2"));//4
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 1, "u1"));//5
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 6, "u2"));//6
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 1, "u1"));//7
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 6, "u2"));//8
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 2, "u1"));//9
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 6, "u2"));//10
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 3, "u1"));//11
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 6, "u2"));//12
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 3, "u1"));//13
        gameInfoService.saveGameInfo(1L, new GameInfoDto(7, 6, "u2"));//14
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 3, "u1"));//15
        //when 게임 시작
        GameOutcomeDto pass1 = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u2"));
        //then
        assertThat(pass1.getUserName()).isEqualTo("u2");
        assertThat(pass1.getStatue()).isEqualTo(GameResultStatus.NO_ACTION);
        //when
        GameOutcomeDto gameResult = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u1"));
        //then
        assertThat(gameResult.getUserName()).isEqualTo(null);
        assertThat(gameResult.getStatue()).isEqualTo(GameResultStatus.DRAW);
    }

    @Test
    @DisplayName("선공 영토승리 테스트")
    void getGameResult_V2() {
        //given
        //room입장
        roomService.enterRoomForBlueRedControl(1L, new User("a", "u1", 1L), 1);
        roomService.enterRoomForBlueRedControl(1L, new User("b", "u2", 1L), 2);
        //게임 시나리오 저장
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 2, "u1"));//1
        gameInfoService.saveGameInfo(1L, new GameInfoDto(1, 6, "u2"));//2
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 1, "u1"));//3
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 6, "u2"));//4
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 1, "u1"));//5
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 6, "u2"));//6
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 1, "u1"));//7
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 6, "u2"));//8
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 1, "u1"));//9
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 6, "u2"));//10
        gameInfoService.saveGameInfo(1L, new GameInfoDto(1, 2, "u1"));//11
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 6, "u2"));//12
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 3, "u1"));//13
        gameInfoService.saveGameInfo(1L, new GameInfoDto(7, 6, "u2"));//14
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 3, "u1"));//15
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 7, "u2"));//16
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 3, "u1"));//17
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 7, "u2"));//18
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 3, "u1"));//19
        //when 게임 시작
        GameOutcomeDto pass1 = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u2"));
        //then
        assertThat(pass1.getUserName()).isEqualTo("u2");
        assertThat(pass1.getStatue()).isEqualTo(GameResultStatus.NO_ACTION);
        //when
        GameOutcomeDto gameResult = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u1"));
        //then
        assertThat(gameResult.getUserName()).isEqualTo("u1");
        assertThat(gameResult.getStatue()).isEqualTo(GameResultStatus.CONQUERED_TERRITORY);
    }

    @Test
    @DisplayName("후공 영토승리 테스트")
    void getGameResult_V3() {
        //given
        //room입장
        roomService.enterRoomForBlueRedControl(1L, new User("a", "u1", 1L), 1);
        roomService.enterRoomForBlueRedControl(1L, new User("b", "u2", 1L), 2);
        //게임 시나리오 저장
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 2, "u1"));//1
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 5, "u2"));//2
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 1, "u1"));//3
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 5, "u2"));//4
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 1, "u1"));//5
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 6, "u2"));//6
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 1, "u1"));//7
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 7, "u2"));//8
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 1, "u1"));//9
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 7, "u2"));//10
        gameInfoService.saveGameInfo(1L, new GameInfoDto(1, 2, "u1"));//11
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 6, "u2"));//12
        gameInfoService.saveGameInfo(1L, new GameInfoDto(2, 3, "u1"));//13
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 8, "u2"));//14
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 3, "u1"));//15
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 8, "u2"));//16
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 3, "u1"));//17
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 8, "u2"));//18
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 3, "u1"));//19
        //when 게임 시작
        GameOutcomeDto pass1 = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u2"));
        //then
        assertThat(pass1.getUserName()).isEqualTo("u2");
        assertThat(pass1.getStatue()).isEqualTo(GameResultStatus.NO_ACTION);
        //when
        GameOutcomeDto gameResult = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u1"));
        //then
        assertThat(gameResult.getUserName()).isEqualTo("u2");
        assertThat(gameResult.getStatue()).isEqualTo(GameResultStatus.CONQUERED_TERRITORY);
    }

    @Test
    @DisplayName("둘다 패스패스인 경우 후공 승리 테스트")
    void getGameResult_V4() {
        //given
        //room입장
        roomService.enterRoomForBlueRedControl(1L, new User("a", "u1", 1L), 1);
        roomService.enterRoomForBlueRedControl(1L, new User("b", "u2", 1L), 2);
        //when 게임 시작
        GameOutcomeDto pass1 = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u1"));
        //then
        assertThat(pass1.getUserName()).isEqualTo("u1");
        assertThat(pass1.getStatue()).isEqualTo(GameResultStatus.NO_ACTION);
        //when
        GameOutcomeDto gameResult = roomService.getGameResult(1L, new GameInfoDto(-1, -1, "u2"));
        //then
        assertThat(gameResult.getUserName()).isEqualTo("u2");
        assertThat(gameResult.getStatue()).isEqualTo(GameResultStatus.CONQUERED_TERRITORY);
    }

    @Test
    @DisplayName("선공 성파괴 승리 테스트")
    void getGameResult_V5() {
        //given
        //room입장
        roomService.enterRoomForBlueRedControl(1L, new User("a", "u1", 1L), 1);
        roomService.enterRoomForBlueRedControl(1L, new User("b", "u2", 1L), 2);
        //게임 시나리오 저장
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 2, "u1"));//1
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 2, "u2"));//2
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 1, "u1"));//3
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 2, "u2"));//4
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 1, "u1"));//5
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 4, "u2"));//6
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 2, "u1"));//7
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 4, "u2"));//8
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 3, "u1"));//9
        gameInfoService.saveGameInfo(1L, new GameInfoDto(7, 4, "u2"));//10
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 3, "u1"));//11
        //when 게임 시작
        GameOutcomeDto gameResult = roomService.getGameResult(1L, new GameInfoDto(5, 3, "u1"));
        //then
        assertThat(gameResult.getUserName()).isEqualTo("u1");
        assertThat(gameResult.getStatue()).isEqualTo(GameResultStatus.DESTROYED_FORTRESS);
    }

    @Test
    @DisplayName("후공 성파괴 승리 테스트")
    void getGameResult_V6() {
        //given
        //room입장
        roomService.enterRoomForBlueRedControl(1L, new User("a", "u1", 1L), 1);
        roomService.enterRoomForBlueRedControl(1L, new User("b", "u2", 1L), 2);
        //게임 시나리오 저장
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 2, "u1"));//1
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 1, "u2"));//2
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 3, "u1"));//3
        gameInfoService.saveGameInfo(1L, new GameInfoDto(7, 2, "u2"));//4
        gameInfoService.saveGameInfo(1L, new GameInfoDto(4, 3, "u1"));//5
        gameInfoService.saveGameInfo(1L, new GameInfoDto(6, 3, "u2"));//6
        gameInfoService.saveGameInfo(1L, new GameInfoDto(3, 3, "u1"));//7
        gameInfoService.saveGameInfo(1L, new GameInfoDto(5, 2, "u2"));//8
        //when 게임 시작
        GameOutcomeDto gameResult = roomService.getGameResult(1L, new GameInfoDto(5, 2, "u2"));
        //then
        assertThat(gameResult.getUserName()).isEqualTo("u2");
        assertThat(gameResult.getStatue()).isEqualTo(GameResultStatus.DESTROYED_FORTRESS);
    }

    //영토싸움을 이긴 케이스 GameInfo
    private List<GameInfo> getGameInfoListTest(long roodId) {
        List<GameInfo> gameinfoList = new ArrayList<>();
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 1, "u1"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 2, "u2"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 1, "u1"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 2, "u2"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 1, "u1"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 2, "u2"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 1, "u1"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 2, "u2"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 1, "u1"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 2, "u2"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 1, "u1"));
        gameinfoList.add(new GameInfo("a", 1L, 0, 0, 2, "u2"));
        return gameinfoList;
    }

    void roomCntValidTest(int availableRoomCnt, int watingRoomsCnt, int fullRoomsCnt) {
        assertThat(roomManager.getAvailableRoomsCount()).isEqualTo(availableRoomCnt);
        assertThat(roomManager.getWaitingRoomsCount()).isEqualTo(watingRoomsCnt);
        assertThat(roomManager.getFullRoomsCount()).isEqualTo(fullRoomsCnt);
    }
}