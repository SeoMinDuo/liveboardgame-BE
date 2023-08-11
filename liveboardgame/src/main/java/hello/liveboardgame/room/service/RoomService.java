package hello.liveboardgame.room.service;

import hello.liveboardgame.room.repository.RoomManager;
import hello.liveboardgame.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.OptionalLong;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomManager roomManager;

    public Long getRoomId() {
        return roomManager.getRoomId().orElse(-1);
    }
}
