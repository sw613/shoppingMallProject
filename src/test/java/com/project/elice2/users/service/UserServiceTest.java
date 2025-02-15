package com.project.elice2.users.service;

import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.SignupRequest;
import com.project.elice2.users.dto.UpdateUserRequest;
import com.project.elice2.users.dto.UserDto;
import com.project.elice2.users.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
//@Transactional
//class UserServiceTest {
//
//    @Autowired
//    EntityManager em;
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private UserRepository userRepository;
//
//
//    @Test
////    @Rollback(value = false)
//    public void 회원가입() throws Exception {
//        //given
//        SignupRequest signupRequest = new SignupRequest("kim", "kim@test.com", "1111", "1111");
//
//        //when
//        UserDto savedUser = userService.signup(signupRequest);
//        Long savedId = savedUser.getId();
//
//        //then
//        Assertions.assertEquals(savedId, userRepository.findById(savedId).get().getId());
//        Assertions.assertEquals("kim", userRepository.findById(savedId).get().getUsername());
//        Assertions.assertEquals("kim@test.com", userRepository.findById(savedId).get().getEmail());
//    }
//
//    @Test
//    public void 중복_회원_예외() throws Exception {
//        //given
//        SignupRequest signupRequest1 = new SignupRequest("kim", "kim@test.com", "1111", "1111");
//        SignupRequest signupRequest2 = new SignupRequest("kim", "kim@test.com", "1111", "1111");
//
//        //when
//        userService.signup(signupRequest1);
//        userService.signup(signupRequest2); //예외 발생
//
//        //then
//        //TODO: 예외 검증 어떻게 하더라.. 찾아서 검증하기
//    }
//
//    @Test
//    public void 회원정보수정() throws Exception {
//        //given
//        SignupRequest signupRequest = new SignupRequest("kim", "kim@test.com", "1111", "1111");
//        UserDto savedUser = userService.signup(signupRequest);
//        Long savedId = savedUser.getId();
//
//        //when
//        UpdateUserRequest updateUserRequest = new UpdateUserRequest("update");
//        userService.update(savedId, updateUserRequest);
//        em.flush();
//        em.clear();
//
//        //then
//        Users findUser = userRepository.findById(savedId).get();
//        org.assertj.core.api.Assertions.assertThat(findUser.getUsername()).isEqualTo("update");
//    }
//}