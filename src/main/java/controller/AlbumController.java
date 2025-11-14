package controller;

import entity.AlbumEntity;
import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import model.Album;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.AlbumService;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.springframework.http.HttpStatus.CREATED;
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
@Validated


public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<AlbumEntity> getAlbum(String name) {
        return ResponseEntity.ok()
                .body(albumService.getAlbum(name));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<AlbumEntity> addAlbum(
            @Valid @RequestBody Album album){
        return ResponseEntity.status(CREATED)
                .header("Name", album.getName())
                .body(albumService.addAlbum(album));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAlbum(@PathVariable String name) {
        albumService.removeAlbum(name);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumEntity> updateAlbum(
            @PathVariable String name,
            @RequestBody Album updatedFields) {

        AlbumEntity updated = albumService.updateAlbum(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}

