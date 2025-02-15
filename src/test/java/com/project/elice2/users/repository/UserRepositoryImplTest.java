package com.project.elice2.users.repository;

import com.project.elice2.users.domain.Provider;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.UserDto;
import com.project.elice2.users.dto.UserSearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Users user1 = Users.createUser("username1", "user1@gmail.com", "1111", Provider.LOCAL);
        Users user2 = Users.createUser("username2", "user2@gmail.com", "1111", Provider.LOCAL);
        Users user3 = Users.createUser("username3", "user3@gmail.com", "1111", Provider.LOCAL);
        Users user4 = Users.createUser("username4", "user4@gmail.com", "1111", Provider.LOCAL);

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);
    }

    @Test
    public void searchTest() {

        UserSearchCondition condition = new UserSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<UserDto> result = userRepository.searchPage(condition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
//        assertThat(result.getContent()).extracting("username").containsExactly("update2", "test2", "username1");
    }
}