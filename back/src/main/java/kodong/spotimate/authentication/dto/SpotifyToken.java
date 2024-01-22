package kodong.spotimate.authentication.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SpotifyToken {
    private String access_token;
    private String token_type;
    private String scope;
    private String refresh_token;
    private int expires_in;
    private LocalDateTime expiryTime; // 토큰의 만료 시간

    // 만료 시간을 계산하는 생성자
    public SpotifyToken(String access_token, String token_type, String scope, String refresh_token, int expires_in) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.scope = scope;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
        this.expiryTime = calculateExpiryTime(expires_in);
    }

    // 토큰 생성 시 만료 시간 계산하여 설정
    public LocalDateTime calculateExpiryTime(int expiresIn) {
        return LocalDateTime.now().plusSeconds(expiresIn).minusMinutes(10);
    }

    // 토큰 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
