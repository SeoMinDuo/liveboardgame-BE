package hello.liveboardgame.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameOutcomeDto {
    private String userName;
    private GameResultStatus statue;
}
