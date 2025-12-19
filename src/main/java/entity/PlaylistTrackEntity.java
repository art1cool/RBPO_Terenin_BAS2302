package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "playlist_tracks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playlist_id", "track_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlaylistEntity playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private TrackEntity track;
}