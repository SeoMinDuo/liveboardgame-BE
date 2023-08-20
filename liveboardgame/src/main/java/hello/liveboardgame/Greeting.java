package hello.liveboardgame;

public class Greeting {
    private String gameState;
    private String startUser;

    public Greeting() {
    }

    public Greeting(String gameState, String startUser) {
        this.gameState = gameState;
        this.startUser = startUser;
    }
}
