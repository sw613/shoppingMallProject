package com.project.elice2.global.security.oauth;

import com.project.elice2.users.domain.Provider;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.repository.UserRepository;
import com.project.elice2.global.security.oauth.dto.CustomOAuth2User;
import com.project.elice2.global.security.oauth.dto.GoogleResponse;
import com.project.elice2.global.security.oauth.dto.NaverResponse;
import com.project.elice2.global.security.oauth.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = getOAuth2Response(registrationId, oAuth2User);
        if (oAuth2Response == null) return null; //TODO: 왜 null을 반환하는 거지? 다음 필터에서 걸러지나?

        Users existData = userRepository.findByEmail(oAuth2Response.getEmail()).orElse(null);

        // provider 값 검증
        // 예외 발생 코드 수정
        if (existData != null && existData.getProvider() == Provider.LOCAL) {
            OAuth2Error error = new OAuth2Error("invalid_request", "이메일 가입 회원입니다. 구글 로그인이 불가능합니다.", null);
            throw new OAuth2AuthenticationException(error);
        }

        Users user = saveOrUpdateUser(existData, oAuth2Response);
        return new CustomOAuth2User(oAuth2Response, user.getAuthority().toString(), user);
    }


    private static OAuth2Response getOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else{
            return null;
        }
        return oAuth2Response;
    }

    private Users saveOrUpdateUser(Users existData, OAuth2Response oAuth2Response) {
        if (existData == null) {
            Users user = Users.createUser(oAuth2Response.getName(), oAuth2Response.getEmail(), "!passw0rd", findProvider(oAuth2Response));
            Users savedUser = userRepository.save(user);
            //추가
            savedUser.setEmailVerified(true);
            System.out.println("#####Saved User: " + savedUser);
            return savedUser;
        } else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(oAuth2Response.getName());
            existData.setEmailVerified(true);
            return existData;
        }
    }

    private Provider findProvider(OAuth2Response oAuth2Response) {
        if (oAuth2Response.getProvider().equals("google")) {
            return Provider.GOOGLE;
        } else {
            return Provider.KAKAO;
        }
    }


}
