package service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Artist
{
    private final String name;
    private final String janre;
}
