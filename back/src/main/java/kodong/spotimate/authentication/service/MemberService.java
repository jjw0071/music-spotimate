package kodong.spotimate.authentication.service;

import kodong.spotimate.authentication.domain.Members;
import kodong.spotimate.authentication.dto.SpotifyToken;
import kodong.spotimate.authentication.dto.SpotifyUserProfile;
import kodong.spotimate.authentication.dto.UserAuthorizeResponse;
import kodong.spotimate.authentication.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private static final String TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
    private final String userProfileEndpoint = "https://api.spotify.com/v1/me";

    @Value("${clientId}")
    String clientId;
    @Value("${clientSecret}")
    String clientSecret;

    /**
     * 현재 사용자 정보 DB에 있는지 확인 후 없으면 DB에 저장
     * @param spotifyUserProfile
     * @return 저장 후 DB에서의 id
     */
    public Long addUserDatatoDB(SpotifyUserProfile spotifyUserProfile) {
        String userId = spotifyUserProfile.getId();

        // 데이터베이스에서 기존 멤버 조회
        Optional<Members> existingMember = memberRepository.findByUserId(userId);

        // 이미 저장된 멤버가 있으면 바로 ID 반환
        if (existingMember.isPresent()) {
            return existingMember.get().getId();
        }

        // 새 멤버 생성
        Members newMember = new Members();
        newMember.setUserId(userId);
        newMember.setEmail(spotifyUserProfile.getEmail());
        newMember.setDisplayName(spotifyUserProfile.getDisplayName());

        // 새 멤버를 데이터베이스에 저장
        Members savedMember = memberRepository.save(newMember);

        // 새로 저장된 멤버의 ID 반환
        return savedMember.getId();
    }

    /**
     * spotify로 token 얻기위한 요청 보냄
     * @param userAuthorizeResponse
     * @return token얻음
     */
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
     * httpSession을 통해 해당 사용자의 profile 받아옴.
     * @param spotifyToken
     * @return
     */
    public SpotifyUserProfile getUserProfile(SpotifyToken spotifyToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(spotifyToken.getAccess_token());
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<SpotifyUserProfile> response = restTemplate.exchange(
                userProfileEndpoint,
                HttpMethod.GET,
                entity,
                SpotifyUserProfile.class);

        return response.getBody();
    }

    public SpotifyToken updateTokenIfNeeded(SpotifyToken currentToken) {
        // 토큰 유효성 확인 및 필요시 업데이트
        if (!isTokenExpired(currentToken)) {
            return refreshToken(currentToken);
        }
        return currentToken;
    }

    private SpotifyToken refreshToken(SpotifyToken currentToken) {
        // TODO token refresh하는 로직 구현
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret); // 기본 인증 헤더 설정

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", currentToken.getRefresh_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SpotifyToken> response = restTemplate.postForEntity(TOKEN_ENDPOINT, request, SpotifyToken.class);

        SpotifyToken newToken = response.getBody();
        if (newToken != null && newToken.getRefresh_token() == null) {
            // 새 토큰에 refresh_token이 없는 경우, 기존의 refresh_token을 유지
            newToken.setRefresh_token(currentToken.getRefresh_token());
        }

        return newToken;
    }

    private boolean isTokenExpired(SpotifyToken currentToken) {
        return currentToken.isExpired();
    }
}
