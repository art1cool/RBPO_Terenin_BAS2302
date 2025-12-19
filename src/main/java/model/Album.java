package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class Album {
    @NotBlank
    private String name;
    private int year;
    @NotNull
    private Artist artist;
    private List<Track> tracks;
}