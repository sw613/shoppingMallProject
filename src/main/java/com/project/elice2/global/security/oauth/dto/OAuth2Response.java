package com.project.elice2.global.security.oauth.dto;

public interface OAuth2Response {

    String getProvider(); //제공자: google, naver...

    String getProviderId();

    String getEmail();

    String getName();
}
