package hello.liveboardgame.room.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class CoordInfo {
    private final int x;
    private final int y;

    public CoordInfo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordInfo coordInfo = (CoordInfo) o;
        return getX() == coordInfo.getX() && getY() == coordInfo.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
