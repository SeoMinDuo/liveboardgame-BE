package hello.liveboardgame.room.domain;

import lombok.*;

//@Getter @Setter
//@AllArgsConstructor
@Data
public class Room {

    private Long id;

    private Boolean isUsed = false;

    public Room(Long id) {
        this.id = id;
    }

}
