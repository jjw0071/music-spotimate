package kodong.spotimate.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotifyToken {
    private String access_token;
    private String token_type;
    private String scope;
    private String refresh_token;
    private int expires_in;
}
