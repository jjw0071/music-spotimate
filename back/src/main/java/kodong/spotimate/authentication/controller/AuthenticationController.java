package kodong.spotimate.authentication.controller;

import kodong.spotimate.authentication.dto.SpotifyToken;
import kodong.spotimate.authentication.dto.UserAuthorizeResponse;
import kodong.spotimate.authentication.service.MemberService;
import kodong.spotimate.authentication.util.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
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
    String clientId = "";
    String redirectUri ="";

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
    public ResponseEntity<Map<String, Boolean>> callback(UserAuthorizeResponse userAuthorizeResponse){
        Map<String, Boolean> response = new HashMap<>();
        String error = userAuthorizeResponse.getError();
        String state = userAuthorizeResponse.getState();

        if (error != null && error.equals("access_denied")) {
            response.put("accept request", false);
            return ResponseEntity.ok(response);
        }

        // 받아온 정보 기반으로 각종 token 요청 후 받아오기
        SpotifyToken token = memberService.requestToken(userAuthorizeResponse);


        response.put("accept request", true);
        return ResponseEntity.ok(response);
    }
}
