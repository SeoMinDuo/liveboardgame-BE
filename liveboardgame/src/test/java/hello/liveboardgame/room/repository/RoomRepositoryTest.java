package hello.liveboardgame.room.repository;

import hello.liveboardgame.room.domain.Room;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RoomRepositoryTest {

    RoomRepository repository = new RoomRepository();

    @Test
    void findById() {
        //given
        Long id1 = 1L;
        Long id2 = 50L;
        Long id3 = 100L;

        //when
        Room room1 = repository.findById(id1);
        Room room2 = repository.findById(id2);
        Room room3 = repository.findById(id3);

        //then
        assertThat(room1.getId()).isEqualTo(id1);
        assertThat(room2.getId()).isEqualTo(id2);
        assertThat(room3.getId()).isEqualTo(id3);

    }

    @Test
    void findAll() {
        //given

        //when
        List<Room> roomEntities = repository.findAll();

        //then
        assertEquals(roomEntities.size(), 100);
    }
}