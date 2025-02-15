package com.project.elice2.users.dto;

import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Provider;
import com.project.elice2.users.domain.Users;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private boolean emailVerified;
    private String provider;
    private Authority authority;
    private Boolean isDeleted;
    //TODO: 주소 추가할까??


    @QueryProjection //querydsl
    public UserDto(Long id, String username, String email, boolean emailVerified, Provider provider, Authority authority, Boolean isDeleted) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.provider = provider.name();
        this.authority = authority;
        this.isDeleted = isDeleted;
    }

    public UserDto toUserDto(Users user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.isEmailVerified(), user.getProvider(), user.getAuthority(), user.getIsDeleted());
    }
}
