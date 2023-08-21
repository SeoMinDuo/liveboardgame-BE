package hello.liveboardgame.gameInfo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameInfo {
    private Long id;
    private String gameId;
    private Long roomId;
    private Integer x;
    private Integer y;
    private Integer ord;

    public GameInfo(String gameId, Long roomId, Integer x, Integer y, Integer ord) {
        this.gameId = gameId;
        this.roomId = roomId;
        this.x = x;
        this.y = y;
        this.ord = ord;
    }

    public GameInfo() {
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "gameId='" + gameId + '\'' +
                ", roomId=" + roomId +
                ", x=" + x +
                ", y=" + y +
                ", ord=" + ord +
                '}';
    }
}
