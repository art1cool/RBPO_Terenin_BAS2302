package service;
//1
import entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.PlaylistTrackRepository;
import repository.PlaylistRepository;
import repository.TrackRepository;
import repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    private boolean isPlaylistOwner(PlaylistEntity playlist) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }
        return playlist.getUser().getId().equals(user.get().getId());
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // Добавить трек в плейлист (по именам)
    @Transactional
    public void addTrackToPlaylist(String playlistName, String trackName) {
        PlaylistEntity playlist = playlistRepository.findByName(playlistName);
        if (playlist == null) {
            throw new RuntimeException("Playlist not found: " + playlistName);
        }

        // Проверка прав доступа
        if (!isPlaylistOwner(playlist) && !isAdmin()) {
            throw new AccessDeniedException("You can only add tracks to your own playlists");
        }

        TrackEntity track = trackRepository.findByName(trackName);
        if (track == null) {
            throw new RuntimeException("Track not found: " + trackName);
        }

        // Проверить, не добавлен ли уже трек
        Optional<PlaylistTrackEntity> existing = playlistTrackRepository
                .findByPlaylistAndTrack(playlist, track);

        if (existing.isPresent()) {
            throw new RuntimeException("Track already exists in playlist");
        }

        // Создаем связь без позиции (если не нужно отслеживать порядок)
        PlaylistTrackEntity playlistTrack = new PlaylistTrackEntity();
        playlistTrack.setPlaylist(playlist);
        playlistTrack.setTrack(track);

        playlistTrackRepository.save(playlistTrack);
    }

    // Удалить трек из плейлиста (по именам)
    @Transactional
    public void removeTrackFromPlaylist(String playlistName, String trackName) {
        PlaylistEntity playlist = playlistRepository.findByName(playlistName);
        if (playlist == null) {
            throw new RuntimeException("Playlist not found: " + playlistName);
        }

        // Проверка прав доступа
        if (!isPlaylistOwner(playlist) && !isAdmin()) {
            throw new AccessDeniedException("You can only remove tracks from your own playlists");
        }

        TrackEntity track = trackRepository.findByName(trackName);
        if (track == null) {
            throw new RuntimeException("Track not found: " + trackName);
        }

        playlistTrackRepository.deleteByPlaylistAndTrack(playlist, track);
    }

    // Получить все треки в плейлисте (по имени плейлиста)
    public List<TrackEntity> getTracksInPlaylist(String playlistName) {
        PlaylistEntity playlist = playlistRepository.findByName(playlistName);
        if (playlist == null) {
            throw new RuntimeException("Playlist not found: " + playlistName);
        }

        return playlistTrackRepository.findByPlaylist(playlist).stream()
                .map(PlaylistTrackEntity::getTrack)
                .collect(Collectors.toList());
    }
}