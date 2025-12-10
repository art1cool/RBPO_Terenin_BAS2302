package service;
//1
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import model.Track;
import entity.TrackEntity;
import repository.TrackRepository;
import entity.ArtistEntity;
import repository.ArtistRepository;
import entity.AlbumEntity;
import repository.AlbumRepository;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository; // Добавляем

    private static final String DURATION_REGEX = "^\\d{1,2}:\\d{2}(?::\\d{2})?$";

    public TrackEntity getTrack(String name) {
        return trackRepository.findByName(name);
    }

    public TrackEntity getTrackById(UUID id) {
        return trackRepository.findById(id).orElse(null);
    }

    @Transactional
    public TrackEntity addTrack(Track track) {
        ArtistEntity artist = artistRepository.findByName(track.getArtist().getName());
        if (artist == null) {
            throw new RuntimeException("Artist not found: " + track.getArtist().getName());
        }
        if (track.getDuration() == null || !Pattern.matches(DURATION_REGEX, track.getDuration())) {
            throw new IllegalArgumentException("Invalid duration format. Expected x:xx, xx:xx, x:xx:xx or xx:xx:xx");
        }

        TrackEntity trackEntity = new TrackEntity();
        trackEntity.setName(track.getName());
        trackEntity.setArtist(artist);
        trackEntity.setDuration(track.getDuration());

        // Если указан альбом, находим и устанавливаем его
        if (track.getAlbum() != null && track.getAlbum().getName() != null) {
            AlbumEntity album = albumRepository.findByName(track.getAlbum().getName());
            if (album == null) {
                throw new RuntimeException("Album not found: " + track.getAlbum().getName());
            }
            trackEntity.setAlbum(album);
        }

        return trackRepository.save(trackEntity);
    }

    public void removeTrack(String name) {
        trackRepository.delete(getTrack(name));
    }

    @Transactional
    public TrackEntity updateTrack(String name, Track updatedFields) {
        TrackEntity existing = trackRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }

        if (updatedFields.getArtist() != null
                && updatedFields.getArtist().getName() != null
                && !updatedFields.getArtist().getName().isBlank()) {
            ArtistEntity newArtist = artistRepository.findByName(updatedFields.getArtist().getName());
            if (newArtist == null) {
                throw new RuntimeException("Artist not found: " + updatedFields.getArtist().getName());
            }
            existing.setArtist(newArtist);
        }

        if (updatedFields.getDuration() != null) {
            String dur = updatedFields.getDuration();
            if (dur.isBlank() || !Pattern.matches(DURATION_REGEX, dur)) {
                throw new IllegalArgumentException("Invalid duration format. Expected x:xx, xx:xx, x:xx:xx or xx:xx:xx");
            }
            existing.setDuration(dur);
        }

        // Обновление альбома
        if (updatedFields.getAlbum() != null) {
            if (updatedFields.getAlbum().getName() == null ||
                    updatedFields.getAlbum().getName().isBlank()) {
                // Если имя альбома пустое, удаляем связь
                existing.setAlbum(null);
            } else {
                // Находим и устанавливаем новый альбом
                AlbumEntity album = albumRepository.findByName(updatedFields.getAlbum().getName());
                if (album == null) {
                    throw new RuntimeException("Album not found: " + updatedFields.getAlbum().getName());
                }
                existing.setAlbum(album);
            }
        }

        return trackRepository.save(existing);
    }

    @Transactional
    public TrackEntity removeTrackFromAlbum(String trackName) {
        TrackEntity existing = trackRepository.findByName(trackName);
        if (existing == null) {
            return null;
        }
        existing.setAlbum(null); // Удаляем связь с альбомом
        return trackRepository.save(existing);
    }
}
