package controller;

import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ArtistService;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.springframework.http.HttpStatus.CREATED;
import model.Artist;
import entity.ArtistEntity;
@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
@Validated


public class ArtistController {
    private final ArtistService artistService;

    @GetMapping
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<ArtistEntity> getArtist(String name) {
        return ResponseEntity.ok()
                .body(artistService.getArtist(name));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('modify')")
    public ResponseEntity<ArtistEntity> addArtist(
            @Valid @RequestBody Artist artist){
        return ResponseEntity.status(CREATED)
                .header("Name", artist.getName())
                .body(artistService.addArtist(artist));
    }

    @DeleteMapping("by-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeArtist(@PathVariable String name) {
        artistService.removeArtist(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistEntity> updateArtist(
            @PathVariable String name,
            @RequestBody Artist updatedFields) {

        ArtistEntity updated = artistService.updateArtist(name, updatedFields);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}