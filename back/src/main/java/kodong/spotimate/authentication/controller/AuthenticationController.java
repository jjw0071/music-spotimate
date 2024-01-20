package kodong.spotimate.authentication.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kodong.spotimate.authentication.dto.SpotifyToken;
import kodong.spotimate.authentication.dto.SpotifyUserProfile;
import kodong.spotimate.authentication.dto.UserAuthorizeResponse;
import kodong.spotimate.authentication.service.MemberService;
import kodong.spotimate.authentication.util.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
// @RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    /**
     * cliendId, redirectUri 작성해야 함!
     */
    @Value("${clientId}")
    String clientId;
    @Value("${redirectUri}")
    String redirectUri;

    private final MemberService memberService;

    @GetMapping("/login")
    public ModelAndView login() {
        String state = RandomStringGenerator.generate(16);
        String scope = "user-read-private user-read-email " +
                "user-library-read user-read-recently-played user-top-read " +
                "user-follow-read playlist-modify-public playlist-modify-private " +
                "playlist-read-collaborative playlist-read-private user-read-currently-playing " +
                "user-modify-playback-state user-read-playback-state";

        String authorizationUri = "https://accounts.spotify.com/authorize?" +
                "response_type=code" +
                "&client_id=" + clientId +
                "&scope=" + scope +
                "&redirect_uri=" + redirectUri +
                "&state=" + state;

        return new ModelAndView("redirect:" + authorizationUri);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(UserAuthorizeResponse userAuthorizeResponse, HttpServletRequest request){
        Map<String, Object> response = new HashMap<>();
        String error = userAuthorizeResponse.getError();
        String state = userAuthorizeResponse.getState();

        if (error != null && error.equals("access_denied")) {
            response.put("agree access", false);
            return ResponseEntity.ok(response);
        }

        // 받아온 정보 기반으로 각종 token 요청 후 받아오기
        SpotifyToken spotifyToken = memberService.requestToken(userAuthorizeResponse);

        // 세션 생성 및 토큰 저장
        HttpSession session = request.getSession();
        session.setAttribute("SpotifyToken", spotifyToken);

        // 세션에 사용자 정보 저장
        SpotifyUserProfile data = memberService.getUserProfile(spotifyToken);
        session.setAttribute("UserData", data);

        Long id =memberService.addUserDatatoDB(data);

        response.put("agree access", true);
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api")
    public ResponseEntity<?> apiRequest(){
        //TODO JSESSION으로 httpSession 찾기

        //TODO 유효성 확인하는 Service 함수 호출

        //TODO 토큰 갱신 및 httpSession update

        //TODO 필요한 api 호출하는 Service 함수 호출

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/get/user/data")
    public ResponseEntity<?> getUserData(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        //세션이 존재하는 경우 (로그인이 되어 있는 경우 JSESSION으로 토큰 가져옴)
        if (session != null) {
            SpotifyToken spotifyToken = (SpotifyToken) session.getAttribute("SpotifyToken");

            SpotifyUserProfile data = memberService.getUserProfile(spotifyToken);
            return ResponseEntity.ok().body(data);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
    }
}
