package service;

import entity.PlaylistEntity;
import entity.TrackEntity;
import entity.PlaylistTrackEntity;
import entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.PlaylistRepository;
import repository.TrackRepository;
import repository.PlaylistTrackRepository;
import repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistOperationService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }
        return user.get();
    }

    private boolean isPlaylistOwner(PlaylistEntity playlist) {
        UserEntity currentUser = getCurrentUser();
        return playlist.getUser().getId().equals(currentUser.getId());
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @Transactional
    public PlaylistEntity mergePlaylists(String firstPlaylistName, String secondPlaylistName, String mergedPlaylistName) {
        PlaylistEntity firstPlaylist = playlistRepository.findByName(firstPlaylistName);
        PlaylistEntity secondPlaylist = playlistRepository.findByName(secondPlaylistName);

        if (firstPlaylist == null) {
            throw new RuntimeException("First playlist not found: " + firstPlaylistName);
        }
        if (secondPlaylist == null) {
            throw new RuntimeException("Second playlist not found: " + secondPlaylistName);
        }

        if (!isPlaylistOwner(firstPlaylist) && !isAdmin()) {
            throw new AccessDeniedException("You can only merge your own playlists");
        }

        if (!isPlaylistOwner(secondPlaylist) && !isAdmin()) {
            throw new AccessDeniedException("You can only merge your own playlists");
        }

        if (!isAdmin() && !firstPlaylist.getUser().getId().equals(secondPlaylist.getUser().getId())) {
            throw new AccessDeniedException("Cannot merge playlists of different users");
        }

        PlaylistEntity existingPlaylist = playlistRepository.findByName(mergedPlaylistName);
        if (existingPlaylist != null) {
            throw new RuntimeException("Playlist with name '" + mergedPlaylistName + "' already exists");
        }

        PlaylistEntity mergedPlaylist = new PlaylistEntity();
        mergedPlaylist.setName(mergedPlaylistName);
        mergedPlaylist.setUser(firstPlaylist.getUser());
        playlistRepository.save(mergedPlaylist);

        List<PlaylistTrackEntity> firstTracks = playlistTrackRepository.findByPlaylist(firstPlaylist);
        List<PlaylistTrackEntity> secondTracks = playlistTrackRepository.findByPlaylist(secondPlaylist);

        for (PlaylistTrackEntity track : firstTracks) {
            boolean exists = playlistTrackRepository
                    .findByPlaylistAndTrack(mergedPlaylist, track.getTrack())
                    .isPresent();

            if (!exists) {
                PlaylistTrackEntity newLink = new PlaylistTrackEntity();
                newLink.setPlaylist(mergedPlaylist);
                newLink.setTrack(track.getTrack());
                playlistTrackRepository.save(newLink);
            }
        }

        for (PlaylistTrackEntity track : secondTracks) {
            boolean exists = playlistTrackRepository
                    .findByPlaylistAndTrack(mergedPlaylist, track.getTrack())
                    .isPresent();

            if (!exists) {
                PlaylistTrackEntity newLink = new PlaylistTrackEntity();
                newLink.setPlaylist(mergedPlaylist);
                newLink.setTrack(track.getTrack());
                playlistTrackRepository.save(newLink);
            }
        }

        return mergedPlaylist;
    }
}
