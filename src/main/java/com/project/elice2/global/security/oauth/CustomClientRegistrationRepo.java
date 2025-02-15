package com.project.elice2.global.security.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class CustomClientRegistrationRepo {

    private final SocialClientRegistration socialClientRegistration;

    public CustomClientRegistrationRepo(SocialClientRegistration socialClientRegistration) {
        this.socialClientRegistration = socialClientRegistration;
    }

    @Bean
    public ClientRegistrationRepository getClientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(socialClientRegistration.googleClientRegistration());
    }
}
