package service;

import entity.AlbumEntity;
import entity.ArtistEntity;
import entity.TrackEntity;
import lombok.RequiredArgsConstructor;
import model.Album;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AlbumRepository;
import repository.ArtistRepository;
import repository.TrackRepository;
import repository.PlaylistTrackRepository;
import util.MappingUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final MappingUtil mappingUtil;

    @Transactional(readOnly = true)
    public AlbumEntity getAlbum(String name) {
        AlbumEntity album = albumRepository.findByName(name);
        if (album != null) {
            album.getTracks().size();
        }
        return album;
    }

    public AlbumEntity addAlbum(Album album) {
        ArtistEntity artist = artistRepository.findByName(album.getArtist().getName());
        if (artist == null) {
            throw new RuntimeException("Artist not found: " + album.getArtist().getName());
        }

        AlbumEntity albumEntity = mappingUtil.toEntity(album);
        albumEntity.setArtist(artist);
        return albumRepository.save(albumEntity);
    }

    @Transactional
    public void removeAlbum(String name) {
        AlbumEntity album = albumRepository.findByName(name);
        if (album != null) {
            List<TrackEntity> tracks = album.getTracks();
            for (TrackEntity track : tracks) {
                playlistTrackRepository.deleteByTrack(track);
                trackRepository.delete(track);
            }
            albumRepository.delete(album);
        }
    }

    public AlbumEntity updateAlbum(String name, Album updatedFields) {
        AlbumEntity existing = albumRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getYear() != 0) {
            existing.setYear(updatedFields.getYear());
        }
        if (updatedFields.getArtist() != null
                && updatedFields.getArtist().getName() != null
                && !updatedFields.getArtist().getName().isBlank()) {

            ArtistEntity artist = artistRepository.findByName(updatedFields.getArtist().getName());
            if (artist == null) {
                throw new RuntimeException("Artist not found: " + updatedFields.getArtist().getName());
            }
            existing.setArtist(artist);
        }

        return albumRepository.save(existing);
    }
}