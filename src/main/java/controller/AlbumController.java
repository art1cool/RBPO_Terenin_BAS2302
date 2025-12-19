package controller;

import entity.AlbumEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import model.Album;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.AlbumService;
import org.springframework.security.access.prepost.PreAuthorize;
import util.MappingUtil;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
@Validated
public class AlbumController {
    private final AlbumService albumService;
    private final MappingUtil mappingUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<Album> getAlbum(String name) {
        AlbumEntity entity = albumService.getAlbum(name);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> addAlbum(
            @Valid @RequestBody Album album){
        AlbumEntity savedEntity = albumService.addAlbum(album);
        return ResponseEntity.status(CREATED)
                .header("Name", album.getName())
                .body(mappingUtil.toDto(savedEntity));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAlbum(@PathVariable String name) {
        albumService.removeAlbum(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Album> updateAlbum(
            @PathVariable String name,
            @RequestBody Album updatedFields) {

        AlbumEntity updatedEntity = albumService.updateAlbum(name, updatedFields);
        if (updatedEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(updatedEntity));
    }
}

