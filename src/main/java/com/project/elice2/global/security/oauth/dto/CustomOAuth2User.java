package com.project.elice2.global.security.oauth.dto;

import com.project.elice2.users.domain.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oauth2Response;
    private final String authority;
    private final Users user;

    public CustomOAuth2User(OAuth2Response oAuth2Response, String authority, Users user) {
        this.oauth2Response = oAuth2Response;
        this.authority = authority;
        this.user = user;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return authority;
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return oauth2Response.getName();
    }

    //TODO: getUsername으로 바꿔야 하나?? 이부분 아직 이해가 안됨
    public String getEmail() {
        return oauth2Response.getEmail();
    }
}
