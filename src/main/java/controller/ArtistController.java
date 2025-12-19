package controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ArtistService;
import org.springframework.security.access.prepost.PreAuthorize;
import util.MappingUtil;

import static org.springframework.http.HttpStatus.CREATED;
import model.Artist;
import entity.ArtistEntity;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
@Validated
public class ArtistController {
    private final ArtistService artistService;
    private final MappingUtil mappingUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<Artist> getArtist(String name) {
        ArtistEntity entity = artistService.getArtist(name);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(entity));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artist> addArtist(
            @Valid @RequestBody Artist artist){
        ArtistEntity savedEntity = artistService.addArtist(artist);
        return ResponseEntity.status(CREATED)
                .header("Name", artist.getName())
                .body(mappingUtil.toDto(savedEntity));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeArtist(@PathVariable String name) {
        artistService.removeArtist(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artist> updateArtist(
            @PathVariable String name,
            @RequestBody Artist updatedFields) {

        ArtistEntity updatedEntity = artistService.updateArtist(name, updatedFields);
        if (updatedEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mappingUtil.toDto(updatedEntity));
    }
}