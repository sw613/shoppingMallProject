package com.project.elice2.users.controller;

import com.project.elice2.users.domain.Provider;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

//@Component
@RequiredArgsConstructor
public class InitUser {

    private final InitUserService initUserService;

    @PostConstruct
    public void init() {
        initUserService.init();
    }

//    @Component
    @RequiredArgsConstructor
    static class InitUserService {

        private final EntityManager em;
        private final BCryptPasswordEncoder passwordEncoder;

        @Transactional
        public void init() {
            for (int i = 1; i <= 100; i++) {
                Users user = Users.createUser("test" + i, "test" + i + "@test.com", passwordEncoder.encode("11111111"), Provider.LOCAL);
                em.persist(user);
            }
        }
    }
}
