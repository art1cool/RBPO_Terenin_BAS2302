package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tracks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private AlbumEntity album;

    @Column(nullable = false)
    private String duration;
}
