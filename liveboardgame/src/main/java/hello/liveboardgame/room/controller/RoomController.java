package hello.liveboardgame.room.controller;

import hello.liveboardgame.room.dto.RoomIdDto;
import hello.liveboardgame.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    private final RoomService roomService;


    @GetMapping("/roomId")
    public RoomIdDto getRoomIdController(String xxxID) {
        log.info("Get:/roomId 요청");
        log.info("xxxID={}", xxxID);
        return new RoomIdDto(roomService.getRoomId());
    }
}
