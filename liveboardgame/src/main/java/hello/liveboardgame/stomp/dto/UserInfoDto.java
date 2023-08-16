package hello.liveboardgame.stomp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserInfoDto {
    private String name;

    public UserInfoDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UserInfoDto() {
    }
}
