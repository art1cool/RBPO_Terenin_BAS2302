package service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Playlist {
    private final String name;
    private final int number_of_tracks;
}
