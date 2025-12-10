package entity;
//1
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "albums")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private int year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    @OneToMany(mappedBy = "album")
    private List<TrackEntity> tracks; // Треки в альбоме
}
