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

    public GameOutcomeDto getGameResult(GameInfoDto gameInfoDto) {

        int x = gameInfoDto.getX();
        int y = gameInfoDto.getY();
//        Room room = roomManager.getRoom(roomId);
        String userName = gameInfoDto.getName();
        //라이벌 이름을 찾음
        String rivalName = getRivalName(gameInfoDto);

        //게임에서 pass가 연속해서 2번 나오면 영토의 크기를 비교하여 게임결과 반환
        if (x == -1 || y == -1) {
            passCnt++;
            if (passCnt == 2) {
                log.info("getGameResult : passCnt=2로 들어옴");
                GameOutcomeDto winnerGameOutcome = compareTerritoriesAndReturnWinner(userName, rivalName);
                if (winnerGameOutcome != null) return winnerGameOutcome;
                log.info("getGameResult : {}님 무승부입니다.", gameInfoDto.getName());
                return new GameOutcomeDto(gameInfoDto.getName(), GameResultStatus.DRAW);
            }
            return new GameOutcomeDto(gameInfoDto.getName(), GameResultStatus.NO_ACTION);
        }

        //상대성을 정복할 경우 결과 반환
        passCnt = 0;
        if (isConqueredTerritory(gameInfoDto)) {
            log.info("getGameResult : {}님이 성을 파괴 하였습니다.", gameInfoDto.getName());
            return new GameOutcomeDto((gameInfoDto.getName()), GameResultStatus.DESTROYED_FORTRESS);
        }
        log.info("getGameResult : {}님 아무일도 일어나지 않았습니다.", gameInfoDto.getName());
        return new GameOutcomeDto(gameInfoDto.getName(), GameResultStatus.NO_ACTION);
    }

    private boolean isConqueredTerritory(GameInfoDto gameInfoDto) {
        log.info("isConqueredTerritory : roomId={} name={} 성 파괴테스트 들어옴", id, gameInfoDto.getName());
        //bord초기화
        initBoard();
        //visited초기화
        initVisited();

        //red blue 유저를 찾음
        log.info("isConqueredTerritory : ++++++++++++++++++++++++++red blue 유저를 찾음++++++++++++++++++++");
        BlueRedNamesDto blueRedNamesDto = getBlueRedUserName();
        String blueName = blueRedNamesDto.getBlueUserName();
        String redName = blueRedNamesDto.getRedUserName();
        log.info("isConqueredTerritory : ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        //board배열에 파랑 빨강 좌표 기록
        log.info("isConqueredTerritory : +++++++++++++++++++board배열에 파랑빨강 좌표 기록+++++++++++++++++++" );
        fillBoard(blueName, redName);
        printBoard();
        log.info("isConqueredTerritory : ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //수를 둔 유저가 파란성일 때 빨간성을 파괴했는지 확인
        log.info("isConqueredTerritory : ++++++++++++++++++++++++수를 둔 유저가 성을 파괴했는지 확인 시작+++++++++++++++++++++++++++");
        boolean result = false;
        int x = gameInfoDto.getX();
        int y = gameInfoDto.getY();
        log.info("gameInfoDto.getName()={} ", gameInfoDto.getName());
        if (gameInfoDto.getName().equals(blueName)) {
            log.info("파괴호출 : {}는 blue팀", gameInfoDto.getName());
            result = dfsIsCastleSurround(x, y, 2);
        }
        //수를 둔 유저가 빨간성일 때 파란성을 파괴했는지 확인
        else if (gameInfoDto.getName().equals(redName)) {
            log.info("파괴호출 : {}는 red팀", gameInfoDto.getName());
            result = dfsIsCastleSurround(x, y, 1);
        }
        //visited 로그출력
        System.out.println("+++++++++++visited 출력+++++++++++");
        printVisited();
        System.out.println("+++++++++++board 출력+++++++++++");
        printBoard();
        System.out.println("+++++++++++++++++++++++++++++++++");
        log.info("isConqueredTerritory : ++++++++++++++++++++++++수를 둔 유저가 성을 파괴했는지 확인 끝+++++++++++++++++++++++++++");
        return result;
    }

    private boolean dfsIsCastleSurround(int x, int y, int rival) {

        log.info("dfsIsCastleSurround 시작 x,y={},{}",x,y);
        System.out.println("++++++++++print visited++++++++++");
        printVisited();
        System.out.println("+++++++++++++++++++++++++++++++++");
        System.out.println("++++++++++print board++++++++++");
        printBoard();

        boolean isSurrond = true;
        for (int i = 0; i < 4; i++) {
            int nx = x + positions[i][1];
            int ny = y + positions[i][0];
            //상대측의 성벽이 공격자의 성벽에 닿는 경우 또는 범위를 이탈할 경우 다음 성벽 조사를 위해 continue
            if (!isValidPosition(nx, ny) || (rival == 1 && board[ny][nx] == 2) || visited[ny][nx]) {
                continue;
            } else if (!isValidPosition(nx, ny) || (rival == 2 && board[ny][nx] == 1) || visited[ny][nx]) {
                continue;
            }
            log.info("nx={}ny={}rival={}board={}", nx, ny,rival,board[ny][nx]);

            //빈공간에 접근될 경우 성 파괴로 인정 x
            if (board[ny][nx] == 0) {
                log.info("x={}y={}nx={}ny={}",x,y,nx,ny);
                log.info("빈 공간에 접근하여 성을 파괴한 것으로 인정하지 않음");
                isSurrond = false;
                continue;
            }

            //방문체크
            visited[ny][nx] = true;
            log.info("visited[{}][{}]={}",ny,nx,visited[ny][nx]);

            isSurrond = dfsIsCastleSurround(nx, ny, rival);
            log.info("nx, ny={},{} isSurround={}",nx,ny,isSurrond);
            if (isSurrond) return true;//하나라도 true가 존재하면 정복할 성이 존재하므로 true반환
        }

        log.info("dfsIsCastleSurround 끝");
        return isSurrond;
    }

    private GameOutcomeDto compareTerritoriesAndReturnWinner(String userName, String rivalName) {

        String blueUserName = gameInfoList.get(0).getUserName();
        //영토가 더 큰 유저의 승리를 반환
        int userTerritorySize = getTerritorySize(userName);
        int rivalTerritorySize = getTerritorySize(rivalName);
        String winnerName = null;

        log.info("getGameResult : userTerritorySize={} rivalTerritorySize={}", userTerritorySize, rivalTerritorySize);
        if (userName == blueUserName) {
            if (userTerritorySize > rivalTerritorySize + 3) {
                log.info("getGameResult : {}님이 정복을 성공하였습니다.", userName);
                winnerName = userName;

            } else if (userTerritorySize < rivalTerritorySize + 3) {
                log.info("getGameResult : {}님이 정복을 성공하였습니다.", rivalName);
                winnerName = rivalName;
            }
        } else {
            if (userTerritorySize + 3 > rivalTerritorySize) {
                log.info("getGameResult : {}님이 정복을 성공하였습니다.", userName);
                winnerName = userName;
            } else if (userTerritorySize + 3 < rivalTerritorySize) {
                log.info("getGameResult : {}님이 정복을 성공하였습니다.", rivalName);
                winnerName = rivalName;
            }
        }
        return new GameOutcomeDto(winnerName, GameResultStatus.CONQUERED_TERRITORY);
    }

    private int getTerritorySize(String name) {

        //bord초기화
        initBoard();
        //visited초기화
        initVisited();

        //red blue 유저 이름을 찾음
        BlueRedNamesDto blueRedUserName = getBlueRedUserName();
        String blueName = blueRedUserName.getBlueUserName();
        String redName = blueRedUserName.getRedUserName();

        //블루 레드 좌표정보 각각의 리스트에 저장 및 board배열에 파랑 빨강 좌표 기록
        board[4][4] = -1;
        for (GameInfo gameInfo : gameInfoList) {
            int x = gameInfo.getX();
            int y = gameInfo.getY();
            if (blueName.equals(gameInfo.getUserName())) {
                board[y][x] = 1;
                blueCoordinateList.add(gameInfo);
            } else if (redName.equals(gameInfo.getUserName())) {
                board[y][x] = 2;
                redCoordinatedList.add(gameInfo);
            }
        }
        //board print@@@@@@@@@@@@@@@@@@@@2
        System.out.println("boar print");
        printBoard();

        //영토크기를 찾고 반환
        int result = 0;
        areaSet.clear();
        if (name.equals(blueName)) {

            //파란성 DFS true 체킹
            for (GameInfo blueInfo : blueCoordinateList) {
                int cx = blueInfo.getX();
                int cy = blueInfo.getY();
                dfsCheckArea(cx, cy);
            }
            //빨간성 DFS False 체킹
            for (GameInfo redInfo : redCoordinatedList) {
                int cx = redInfo.getX();
                int cy = redInfo.getY();
                dfsDisCheckArea(cx, cy);
            }
            //True 영역이 4면과 맞닿아 있는지 체크
            //Ture를 지우면서 이동하며 4면과 닿아있으면 0반환 아니라면 개수반환
            for (CoordInfo coordInfo : areaSet) {
                System.out.print("coordInfo.getX() = " + coordInfo.getX());
                System.out.print("coordInfo.getY() = " + coordInfo.getY());
                System.out.println();
                int x = coordInfo.getX();
                int y = coordInfo.getY();
                if (!visited[y][x]) continue;
                result += dfsGetValidArea(x, y);
                System.out.println("result = " + result);

            }

        } else if (name.equals(redName)) {
            //빨간성 DFS true 체킹
            for (GameInfo redInfo : redCoordinatedList) {
                int cx = redInfo.getX();
                int cy = redInfo.getY();
                dfsCheckArea(cx, cy);
            }
            //파란성 DFS False 체킹
            for (GameInfo blueInfo : blueCoordinateList) {
                int cx = blueInfo.getX();
                int cy = blueInfo.getY();
                dfsDisCheckArea(cx, cy);
            }
            //@@@@@@@@@임시 출력
            printVisited();
            //True 영역이 4면과 맞닿아 있는지 체크
            //Ture를 지우면서 이동하며 4면과 닿아있으면 0반환 아니라면 개수반환
            for (CoordInfo coordInfo : areaSet) {
                //checkTouch초기화
                for (int i = 0; i < 4; i++) {
                    checkTouchFourSpace[i] = false;
                }
                System.out.print("coordInfo.getX() = " + coordInfo.getX());
                System.out.print("coordInfo.getY() = " + coordInfo.getY());
                System.out.println();
                int x = coordInfo.getX();
                int y = coordInfo.getY();
                if (!visited[y][x]) continue;
                result += dfsGetValidArea(x, y);
                System.out.println("result = " + result);

            }
        }
        //board print
        System.out.println("boar print");
        printBoard();

        //@@@@@@@@@임시 출력
        printVisited();

        return result;
    }

    private BlueRedNamesDto getBlueRedUserName() {
        String blueName = "";
        String redName = "";
        if (gameInfoList.get(0).getUserName().equals(users.get(0).getName())) {
            blueName = users.get(0).getName();
            redName = users.get(1).getName();
            System.out.println("redName = " + redName + "blueName = " + blueName);
        } else {
            blueName = users.get(1).getName();
            redName = users.get(0).getName();
            System.out.println("redName = " + redName + "blueName = " + blueName);
        }
        return new BlueRedNamesDto(blueName, redName);
    }

    private String getRivalName(GameInfoDto gameInfoDto) {
        for (User user : users) {
            if (!gameInfoDto.getName().equals(user.getName())) {
                return user.getName();
            }
        }
        return null;
    }

    private void initVisited() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                visited[y][x] = false;
            }
        }
        visited[4][4] = true;
    }

    private void initBoard() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                board[y][x] = 0;
            }
        }
    }

    private void printBoard() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                System.out.print(board[y][x] + " ");
            }
            System.out.println();
        }
    }

    private void printVisited() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                System.out.print(visited[y][x] + " ");
            }
            System.out.println();
        }
    }

    private void fillBoard(String blueName, String redName) {
        board[4][4] = -1;
        for (GameInfo gameInfo : gameInfoList) {
            int x = gameInfo.getX();
            int y = gameInfo.getY();
            log.info("x,y={},{} blueName={} redName={} getUserName={}",x,y, blueName, redName, gameInfo.getUserName());
            if (blueName.equals(gameInfo.getUserName())) {
                board[y][x] = 1;
                log.info("boar={}",board[y][x]);
            } else if (redName.equals(gameInfo.getUserName())) {
                board[y][x] = 2;
                log.info("boar={}",board[y][x]);
            }
        }
    }


    private void dfsCheckArea(int x, int y) {
        for (int[] position : positions) {
            int nx = x + position[1];
            int ny = y + position[0];
            //이동 불가능 지역 또는 이미 방문한 지역은 무시
            if (!isValidPosition(nx, ny) || board[ny][nx] != 0 || visited[ny][nx]) continue;

            //방문체크
            visited[ny][nx] = true;
            areaSet.add(new CoordInfo(nx, ny));
            dfsCheckArea(nx, ny);
        }
    }

    private boolean isValidPosition(int x, int y) {
        return 0 <= x && x < 9 && 0 <= y && y < 9;
    }

    private void dfsDisCheckArea(int x, int y) {
        for (int[] position : positions) {
            int nx = x + position[1];
            int ny = y + position[0];
            //이동불가능지역 또는 방문한 적 지역은 무시
            if (!isValidPosition(nx, ny) || board[ny][nx] != 0 || !visited[ny][nx]) continue;

            //방문체크삭제
            visited[ny][nx] = false;
            areaSet.remove(new CoordInfo(nx, ny));
            dfsDisCheckArea(nx, ny);
        }
    }

    private int dfsGetValidArea(int x, int y) {
        int areaSize = 0;
        boolean isValidArea = true;
        //영역이 4면에 맞닿아 있어 영역으로 인정 x
        if (x == 0) checkTouchFourSpace[0] = true;
        if (x == 8) checkTouchFourSpace[1] = true;
        if (y == 0) checkTouchFourSpace[2] = true;
        if (y == 8) checkTouchFourSpace[3] = true;
        int count = 0;
        for (boolean b : checkTouchFourSpace) {
            if (b) count++;
        }
        if (count == 4) {
            isValidArea = false;
        }
        visited[y][x] = false;
        //다음 위치로 이동
        for (int[] position : positions) {
            int nx = x + position[1];
            int ny = y + position[0];

            if (!isValidPosition(nx,ny) || !visited[ny][nx]) continue;
            int v = dfsGetValidArea(nx, ny);
            if (v == 0) {//0이 반환되는 경우 검증실패
                isValidArea = false;
            }
            areaSize += v;
        }
        if (!isValidArea) return 0;
        return areaSize + 1;
    }

    public void initGame() {
        initBoard();
        initVisited();
        blueCoordinateList.clear();
        redCoordinatedList.clear();
        initCheckTouchFourSpace();
        areaSet.clear();
        passCnt = 0;
        gameInfoList.clear();
    }
    private void initCheckTouchFourSpace() {
        for (int i = 0; i < 4; i++) {
            checkTouchFourSpace[i] = false;
        }
    }
}
