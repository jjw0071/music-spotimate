package kodong.spotimate.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAuthorizeResponse {

    private String code;
    private String error;
    private String state;

}
