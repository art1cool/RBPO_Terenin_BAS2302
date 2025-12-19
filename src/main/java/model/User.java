package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class User {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank()
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
            message = "Password must contain upper, lower case letters, digit and special character"
    )
    private String password;

    private List<String> playlists;
}
