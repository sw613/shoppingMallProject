package com.project.elice2.global.security.service;

import com.project.elice2.users.domain.Users;
import com.project.elice2.global.security.dto.CustomUserDetails;
import com.project.elice2.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users userData = userRepository.findByEmail(username).orElse(null);
        if (userData == null) {
//            return null;
            throw new UsernameNotFoundException(username);
        }

        return new CustomUserDetails(userData);
    }
}
