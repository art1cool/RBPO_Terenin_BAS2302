package controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import model.Playlist;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.PlaylistService;
import service.PlaylistTrackService;
import org.springframework.security.access.prepost.PreAuthorize;
import util.MappingUtil;
import entity.TrackEntity;

import static org.springframework.http.HttpStatus.CREATED;
import entity.PlaylistEntity;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
@Validated
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistTrackService playlistTrackService;
    private final MappingUtil mappingUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<Playlist> getPlaylist(String name) {
        PlaylistEntity entity = playlistService.getPlaylist(name);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        Playlist dto = mappingUtil.toDto(entity);

        try {
            var tracks = playlistTrackService.getTracksInPlaylist(name);
            var trackNames = tracks.stream()
                    .map(TrackEntity::getName)
                    .collect(java.util.stream.Collectors.toList());
            dto.setTracks(trackNames);
        } catch (RuntimeException e) {
            dto.setTracks(java.util.Collections.emptyList());
        }

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<Playlist> addPlaylist(
            @Valid @RequestBody Playlist playlist){
        PlaylistEntity savedEntity = playlistService.addPlaylist(playlist);
        Playlist dto = mappingUtil.toDto(savedEntity);
        dto.setTracks(java.util.Collections.emptyList());
        return ResponseEntity.status(CREATED)
                .header("Name", playlist.getName())
                .body(dto);
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<Void> removePlaylist(@PathVariable String name) {
        playlistService.removePlaylist(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<Playlist> updatePlaylist(
            @PathVariable String name,
            @RequestBody Playlist updatedFields) {

        PlaylistEntity updated = playlistService.updatePlaylist(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        Playlist dto = mappingUtil.toDto(updated);

        try {
            var tracks = playlistTrackService.getTracksInPlaylist(updated.getName());
            var trackNames = tracks.stream()
                    .map(TrackEntity::getName)
                    .collect(java.util.stream.Collectors.toList());
            dto.setTracks(trackNames);
        } catch (RuntimeException e) {
            dto.setTracks(java.util.Collections.emptyList());
        }

        return ResponseEntity.ok(dto);
    }
}
