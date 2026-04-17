package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.Provider;
import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.info(">>> Có người đang đăng nhập bằng Google: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(Provider.GOOGLE)) {
                user.setProvider(Provider.GOOGLE);
                userRepository.save(user);
            }
        } else {
            user = User.builder()
                    .email(email)
                    .fullName(name)
                    .role(UserRole.STUDENT)
                    .provider(Provider.GOOGLE)
                    .isActive(true)
                    .build();
            userRepository.save(user);
        }

        return oAuth2User ;
    }
}
