package com.project.elice2.users.dto;

import com.project.elice2.users.domain.Authority;
import lombok.Data;

@Data
public class UserSearchCondition {

    private String searchType;
    private String keyword;
    private String username;
    private String email;
    private String authority;
    private Boolean idDeleted;
}
