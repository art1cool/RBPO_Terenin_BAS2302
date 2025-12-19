package controller;

import entity.PlaylistEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.PlaylistOperationService;

import java.util.Map;

@RestController
@RequestMapping("/playlists/operations")
@RequiredArgsConstructor
public class PlaylistOperationController {

    private final PlaylistOperationService playlistOperationService;

    @PostMapping("/merge")
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<?> mergePlaylists(
            @RequestBody Map<String, String> request) {
        try {
            String firstPlaylist = request.get("firstPlaylist");
            String secondPlaylist = request.get("secondPlaylist");
            String mergedPlaylistName = request.get("mergedPlaylistName");

            if (firstPlaylist == null || secondPlaylist == null || mergedPlaylistName == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required parameters");
            }

            PlaylistEntity mergedPlaylist = playlistOperationService
                    .mergePlaylists(firstPlaylist, secondPlaylist, mergedPlaylistName);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Playlists merged successfully. New playlist: " + mergedPlaylist.getName());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
