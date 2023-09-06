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
    private String userName;

    public GameInfo(String gameId, Long roomId, Integer x, Integer y, Integer ord, String userName) {
        this.gameId = gameId;
        this.roomId = roomId;
        this.x = x;
        this.y = y;
        this.ord = ord;
        this.userName = userName;
    }

    public GameInfo() {
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "id=" + id +
                ", gameId='" + gameId + '\'' +
                ", roomId=" + roomId +
                ", x=" + x +
                ", y=" + y +
                ", ord=" + ord +
                ", userName='" + userName + '\'' +
                '}';
    }
}
