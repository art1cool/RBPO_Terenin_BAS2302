package controller;

import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import model.Track;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.TrackService;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.springframework.http.HttpStatus.CREATED;
import entity.TrackEntity;
@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
@Validated


public class TrackController {
    private final TrackService trackService;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<TrackEntity> getTrack(String name) {
        return ResponseEntity.ok()
                .body(trackService.getTrack(name));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<TrackEntity> addTrack(
            @Valid @RequestBody Track track){
        return ResponseEntity.status(CREATED)
                .header("Name", track.getName())
                .body(trackService.addTrack(track));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeTrack(@PathVariable String name) {
        trackService.removeTrack(name);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrackEntity> updateTrack(
            @PathVariable String name,
            @RequestBody Track updatedFields) {

        TrackEntity updated = trackService.updateTrack(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}


