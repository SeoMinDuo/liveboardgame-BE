package hello.liveboardgame.room.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {

    private Long id;
    private String name;
    private Long roomId;

    public User(String name, Long roomId) {
        this.name = name;
        this.roomId = roomId;
    }

    public User() {
    }
}
