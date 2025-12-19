package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import model.Artist;
import entity.ArtistEntity;
import entity.AlbumEntity;
import entity.TrackEntity;
import repository.ArtistRepository;
import repository.AlbumRepository;
import repository.TrackRepository;
import repository.PlaylistTrackRepository;
import util.MappingUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final MappingUtil mappingUtil;

    public ArtistEntity addArtist(Artist artist) {
        ArtistEntity artistEntity = mappingUtil.toEntity(artist);
        return artistRepository.save(artistEntity);
    }

    @Transactional(readOnly = true)
    public ArtistEntity getArtist(String name) {
        ArtistEntity artist = artistRepository.findByName(name);
        if (artist != null) {
            artist.getAlbums().size();
            artist.getTracks().size();
        }
        return artist;
    }

    @Transactional
    public void removeArtist(String name) {
        ArtistEntity artist = artistRepository.findByName(name);
        if (artist != null) {
            List<AlbumEntity> albums = artist.getAlbums();

            for (AlbumEntity album : albums) {
                List<TrackEntity> albumTracks = trackRepository.findByAlbum(album);
                for (TrackEntity track : albumTracks) {
                    playlistTrackRepository.deleteByTrack(track);
                    trackRepository.delete(track);
                }
                albumRepository.delete(album);
            }

            List<TrackEntity> standaloneTracks = artist.getTracks();
            for (TrackEntity track : standaloneTracks) {
                if (track != null && trackRepository.existsById(track.getId())) {
                    playlistTrackRepository.deleteByTrack(track);
                    trackRepository.delete(track);
                }
            }

            artistRepository.delete(artist);
        }
    }

    public ArtistEntity updateArtist(String name, Artist updatedFields) {
        ArtistEntity existing = artistRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        // Обновляем только переданные поля
        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getGenre() != null && !updatedFields.getGenre().isBlank()) {
            existing.setGenre(updatedFields.getGenre());
        }

        return artistRepository.save(existing);
    }
}
