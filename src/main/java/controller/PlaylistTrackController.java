package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.PlaylistTrackService;
import entity.TrackEntity;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistTrackController {

    private final PlaylistTrackService playlistTrackService;

    @PostMapping("/{playlistName}/tracks/{trackName}")
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<?> addTrackToPlaylist(
            @PathVariable String playlistName,
            @PathVariable String trackName) {
        try {
            playlistTrackService.addTrackToPlaylist(playlistName, trackName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Track '" + trackName + "' added to playlist '" + playlistName + "' successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{playlistName}/tracks/{trackName}")
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<?> removeTrackFromPlaylist(
            @PathVariable String playlistName,
            @PathVariable String trackName) {
        try {
            playlistTrackService.removeTrackFromPlaylist(playlistName, trackName);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{playlistName}/tracks")
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<List<TrackEntity>> getPlaylistTracks(
            @PathVariable String playlistName) {
        try {
            List<TrackEntity> tracks = playlistTrackService
                    .getTracksInPlaylist(playlistName);
            return ResponseEntity.ok(tracks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}