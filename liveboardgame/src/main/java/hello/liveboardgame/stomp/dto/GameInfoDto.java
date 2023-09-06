package hello.liveboardgame.stomp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameInfoDto {
    private int x;
    private int y;
    private String name;

    public GameInfoDto(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    @Override
    public String toString() {
        return "GameInfoDto{" +
                "x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                '}';
    }
}
