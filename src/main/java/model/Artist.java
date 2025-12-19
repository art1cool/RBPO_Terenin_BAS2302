package model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class Artist {
    @NotBlank
    private String name;
    @NotBlank
    private String genre;
    private List<Album> albums;
    private List<Track> tracks;
}
