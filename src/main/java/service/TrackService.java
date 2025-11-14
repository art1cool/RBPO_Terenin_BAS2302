package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import model.Track;
import entity.TrackEntity;
import repository.TrackRepository;
import entity.ArtistEntity;
import repository.ArtistRepository;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    private static final String DURATION_REGEX = "^\\d{1,2}:\\d{2}(?::\\d{2})?$";

    public TrackEntity getTrack(String name) {
        return trackRepository.findByName(name);
    }

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

        return trackRepository.save(trackEntity);
    }

    public void removeTrack(String name) {
        trackRepository.delete(getTrack(name));
    }

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

        return trackRepository.save(existing);
    }
}
