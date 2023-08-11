package hello.liveboardgame.room.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

//@Getter @Setter
//@AllArgsConstructor
@Data
public class Room {

    private Long id;

    private Boolean isUsed = false;

    private List<User> users = new ArrayList<>();

    public Room(Long id) {
        this.id = id;
    }

}
