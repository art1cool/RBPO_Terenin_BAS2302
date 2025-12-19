package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class Playlist {
    @NotBlank
    private String name;

    @NotNull
    private User user;

    private List<String> tracks;
}
