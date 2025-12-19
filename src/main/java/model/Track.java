package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Track {
    @NotBlank
    private String name;

    @NotNull
    private Artist artist;

    @NotBlank
    @Pattern(regexp = "^\\d{1,2}:\\d{2}(?::\\d{2})?$")
    private String duration;

    private Album album;
}
