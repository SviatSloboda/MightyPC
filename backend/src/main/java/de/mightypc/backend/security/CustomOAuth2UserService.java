package de.mightypc.backend.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        if ("github".equals(userRequest.getClientRegistration().getRegistrationId())) {
            String emailEndpoint = "https://api.github.com/user/emails";
            String accessToken = userRequest.getAccessToken().getTokenValue();
            List<Map<String, Object>> emails = restTemplate.getForObject(emailEndpoint + "?access_token=" + accessToken, List.class);
            if (emails != null && !emails.isEmpty()) {
                oAuth2User.getAttributes().put("email", emails.getFirst().get("email"));
            }
        }

        return oAuth2User;
    }
}
