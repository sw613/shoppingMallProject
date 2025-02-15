package com.project.elice2.users.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.global.BaseUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter//임시 허용
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor //임시로 허용
public class Users extends BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

//    @Column(nullable = false)
    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    private Boolean isDeleted;

    private String phoneNumber;


    //생성 메서드
    public static Users createUser(String username, String email, String password, Provider provider) {
        Users user = new Users();
        user.username = username;
        user.email = email;
        user.password = password;
        user.provider = provider;
        user.authority = Authority.ROLE_USER;
        user.emailVerified = false;
        user.isDeleted = false;

        return user;
    }

    //TODO: 리팩토링 - 매개변수를 Authority 타입으로 하면 IllegalArgumentException 터질 확률 없어지고 좋을듯??
    public void changeAuthority(String authority) {

        try {
            this.authority = Authority.valueOf(authority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid authority: " + authority);
        }
    }

    public String genToken() {
        return this.emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

}
