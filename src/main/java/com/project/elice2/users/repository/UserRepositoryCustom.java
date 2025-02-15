package com.project.elice2.users.repository;

import com.project.elice2.users.dto.UserDto;
import com.project.elice2.users.dto.UserSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {

    List<UserDto> search(UserSearchCondition condition);
    Page<UserDto> searchPage(UserSearchCondition condition, Pageable pageable);
    Page<UserDto> searchPage2(UserSearchCondition condition, Pageable pageable);
    Page<UserDto> searchPage3(UserSearchCondition condition, Pageable pageable);
}
