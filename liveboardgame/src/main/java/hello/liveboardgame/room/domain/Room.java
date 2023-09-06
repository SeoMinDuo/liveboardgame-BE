package hello.liveboardgame.room.domain;

import hello.liveboardgame.gameInfo.domain.GameInfo;
import hello.liveboardgame.room.dto.BlueRedNamesDto;
import hello.liveboardgame.room.dto.CoordInfo;
import hello.liveboardgame.room.dto.GameOutcomeDto;
import hello.liveboardgame.room.dto.GameResultStatus;
import hello.liveboardgame.stomp.dto.GameInfoDto;
import hello.liveboardgame.user.domain.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//@Getter @Setter
//@AllArgsConstructor
@Data
@Slf4j
public class Room {

    private Long id;

    private Boolean isUsed = false;

    private List<User> users = new ArrayList<>();

    private String gameId;

    private Integer ord = 0;

    public Room(Long id) {
        this.id = id;
    }
    public Integer getOrder() {
        ord += 1;
        return ord;
    }

    //게임 플레이 전용 필드
    private int passCnt = 0;
    private List<GameInfo> gameInfoList = new ArrayList<>();
    private int[][] board = new int[10][10];
    private boolean[][] visited = new boolean[10][10];
    //모든 게임 정보로부터 파랑과 빨강을 구분하여 collection에 담음
    private List<GameInfo> blueCoordinateList = new ArrayList<>();
    private List<GameInfo> redCoordinatedList = new ArrayList<>();
    private HashSet<CoordInfo> areaSet = new HashSet<>();//최종으로 추출된 영역의 좌표값 저장 변수
    private int[][] positions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };
    private boolean[] checkTouchFourSpace = {false, false, false, false};//0:좌 1:우 2:상 3:하

    public void initGame() {
        initBoard();
        initVisited();
        blueCoordinateList.clear();
        redCoordinatedList.clear();
        initCheckTouchFourSpace();
        areaSet.clear();
        passCnt = 0;
    }
    private void initCheckTouchFourSpace() {
        for (int i = 0; i < 4; i++) {
            checkTouchFourSpace[i] = false;
        }
    }
}
