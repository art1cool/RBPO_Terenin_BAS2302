package repository;

import entity.TrackEntity;
import entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, UUID> {
    TrackEntity findByName(String name);

    @Query(value = "SELECT * FROM tracks WHERE artist = :artist", nativeQuery = true)
    TrackEntity findByArtist(String artist);

    void deleteByAlbum(AlbumEntity album);

    List<TrackEntity> findByAlbum(AlbumEntity album);
}
