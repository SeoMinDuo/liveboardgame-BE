package hello.liveboardgame.user.repository;

import hello.liveboardgame.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GameUserManager {
    private static final Map<String, User> inGameUsers = new HashMap<>();

    public void save(User user) {
        inGameUsers.put(user.getSessionId(), user);
    }

    public User findBySessionId(String sessionId) {
        return inGameUsers.get(sessionId);
    }

    public User delete(String sessionId) {
        return inGameUsers.remove(sessionId);
    }

}
