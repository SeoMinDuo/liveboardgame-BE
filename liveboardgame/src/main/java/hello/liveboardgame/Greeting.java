package hello.liveboardgame;

import lombok.Getter;

@Getter
public class Greeting {
    private String gameServer;

    public Greeting() {
    }

    public Greeting(String gameServer) {
        this.gameServer = gameServer;
    }

//    public String getGameServer() {
//        return gameServer;
//    }
}
