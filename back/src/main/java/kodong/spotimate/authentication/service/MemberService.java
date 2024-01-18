package kodong.spotimate.authentication.service;

import kodong.spotimate.authentication.dto.SpotifyToken;
import kodong.spotimate.authentication.dto.UserAuthorizeResponse;
import kodong.spotimate.authentication.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private static final String TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
    String clientId = "";
    String clientSecret = "";

    public SpotifyToken requestToken(UserAuthorizeResponse userAuthorizeResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", userAuthorizeResponse.getCode());
        map.add("redirect_uri", "http://localhost:8080/callback");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SpotifyToken> response = restTemplate.postForEntity(TOKEN_ENDPOINT, request, SpotifyToken.class);

        return response.getBody();
    }

    /**
     * 권한제공에 동의했는제 확인
     */

}
