package hello.liveboardgame.user.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {

    private Long id;
    private String sessionId;
    private String name;
    private Long roomId;

    public User(String sessionId, String name, Long roomId) {
        this.sessionId = sessionId;
        this.name = name;
        this.roomId = roomId;
    }

    public User() {
    }
}
