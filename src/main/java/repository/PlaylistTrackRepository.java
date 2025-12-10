package repository;
//1
import entity.PlaylistTrackEntity;
import entity.PlaylistEntity;
import entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrackEntity, UUID> {

    // Найти все треки в плейлисте
    List<PlaylistTrackEntity> findByPlaylist(PlaylistEntity playlist);

    // Найти конкретную связь плейлист-трек
    Optional<PlaylistTrackEntity> findByPlaylistAndTrack(PlaylistEntity playlist, TrackEntity track);

    // Удалить трек из плейлиста
    @Modifying
    @Query("DELETE FROM PlaylistTrackEntity pt WHERE pt.playlist = :playlist AND pt.track = :track")
    void deleteByPlaylistAndTrack(@Param("playlist") PlaylistEntity playlist,
                                  @Param("track") TrackEntity track);

    // Получить количество треков в плейлисте
    @Query("SELECT COUNT(pt) FROM PlaylistTrackEntity pt WHERE pt.playlist = :playlist")
    int countByPlaylist(@Param("playlist") PlaylistEntity playlist);
}