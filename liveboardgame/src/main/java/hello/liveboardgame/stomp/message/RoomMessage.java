package hello.liveboardgame.stomp.message;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoomMessage {
    private String id;
    private String msg;
    private String roomId;
}
