package com.example.auth_service.security;

import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = extractEmail(oauth2User, registrationId);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email));

        return new DefaultOAuth2User(
                Set.of(() -> "ROLE_USER"),
                oauth2User.getAttributes(),
                "email"
        );
    }

    private User createUser(String email) {
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name("ROLE_USER")
                                        .build()
                        )
                );

        User user = User.builder()
                .email(email)
                .password("OAUTH2_USER") // tidak dipakai
                .fullName(email)
                .enabled(true)
                .build();

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    private String extractEmail(OAuth2User user, String provider) {

        Map<String, Object> attrs = user.getAttributes();

        if (provider.equals("google")) {
            return (String) attrs.get("email");
        }

        if (provider.equals("github")) {
            return (String) attrs.get("email");
        }

        throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_provider"));
    }
}
