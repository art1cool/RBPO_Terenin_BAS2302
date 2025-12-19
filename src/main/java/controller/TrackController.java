package controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import model.Track;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.TrackService;
import org.springframework.security.access.prepost.PreAuthorize;
import util.MappingUtil;

import static org.springframework.http.HttpStatus.CREATED;
import entity.TrackEntity;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
@Validated
public class TrackController {
    private final TrackService trackService;
    private final MappingUtil mappingUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<Track> getTrack(String name) {
        TrackEntity entity = trackService.getTrack(name);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Track> addTrack(
            @Valid @RequestBody Track track){
        TrackEntity savedEntity = trackService.addTrack(track);
        return ResponseEntity.status(CREATED)
                .header("Name", track.getName())
                .body(mappingUtil.toDto(savedEntity));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeTrack(@PathVariable String name) {
        trackService.removeTrack(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Track> updateTrack(
            @PathVariable String name,
            @RequestBody Track updatedFields) {

        TrackEntity updatedEntity = trackService.updateTrack(name, updatedFields);
        if (updatedEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(updatedEntity));
    }

    @PatchMapping("/{name}/remove-from-album")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Track> removeTrackFromAlbum(@PathVariable String name) {
        TrackEntity updatedEntity = trackService.removeTrackFromAlbum(name);
        if (updatedEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(updatedEntity));
    }
}
