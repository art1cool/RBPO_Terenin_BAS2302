package service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Album {
    private final int name;
    private final String year;
    private final String artist
}
