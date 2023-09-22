package hello.liveboardgame.room.dto;

import lombok.Getter;

@Getter
public enum GameResultStatus {
    CONQUERED_TERRITORY(1), DESTROYED_FORTRESS(2), NO_ACTION(0), DRAW(3);

    private final int stateCode;

    GameResultStatus(int stateCode) {
        this.stateCode = stateCode;
    }
}
