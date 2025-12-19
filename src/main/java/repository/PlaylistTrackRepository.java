package repository;

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

    List<PlaylistTrackEntity> findByPlaylist(PlaylistEntity playlist);

    Optional<PlaylistTrackEntity> findByPlaylistAndTrack(PlaylistEntity playlist, TrackEntity track);

    @Modifying
    @Query("DELETE FROM PlaylistTrackEntity pt WHERE pt.playlist = :playlist AND pt.track = :track")
    void deleteByPlaylistAndTrack(@Param("playlist") PlaylistEntity playlist,
                                  @Param("track") TrackEntity track);

    @Query("SELECT COUNT(pt) FROM PlaylistTrackEntity pt WHERE pt.playlist = :playlist")
    int countByPlaylist(@Param("playlist") PlaylistEntity playlist);

    @Modifying
    @Query("DELETE FROM PlaylistTrackEntity pt WHERE pt.playlist = :playlist")
    void deleteByPlaylist(@Param("playlist") PlaylistEntity playlist);

    @Modifying
    @Query("DELETE FROM PlaylistTrackEntity pt WHERE pt.track = :track")
    void deleteByTrack(@Param("track") TrackEntity track);
}