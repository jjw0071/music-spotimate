package kodong.spotimate.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotifyUserProfile {
    @JsonProperty("display_name")
    private String displayName;

    // @JsonProperty("email")
    private String email;

    // @JsonProperty("id")
    //The unique string identifying the Spotify user that you can find at the end of the Spotify URI for the user.
    private String id;

    // @JsonProperty("uri")
    private String uri;
}
