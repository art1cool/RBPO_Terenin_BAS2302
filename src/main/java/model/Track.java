package service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Track {
    private final String name;
    private final String artist;
}
