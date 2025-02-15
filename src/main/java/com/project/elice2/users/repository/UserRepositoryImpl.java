package com.project.elice2.users.repository;

import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.dto.QUserDto;
import com.project.elice2.users.dto.UserDto;
import com.project.elice2.users.dto.UserSearchCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.project.elice2.users.domain.QUsers.*;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<UserDto> search(UserSearchCondition condition) {
        return queryFactory
                .select(new QUserDto(
                        users.id,
                        users.username,
                        users.email,
                        users.emailVerified,
                        users.provider,
                        users.authority,
                        users.isDeleted
                ))
                .from(users)
                .where(
                        emailEq(condition.getEmail()),
                        usernameEq(condition.getUsername()),
                        isDeletedEq(condition.getIdDeleted())
                )
                .fetch();
    }

    @Override
    public Page<UserDto> searchPage(UserSearchCondition condition, Pageable pageable) {

        List<UserDto> contents = queryFactory
                .select(new QUserDto(
                        users.id,
                        users.username,
                        users.email,
                        users.emailVerified,
                        users.provider,
                        users.authority,
                        users.isDeleted
                ))
                .from(users)
                .where(
                        emailEq(condition.getEmail()),
                        usernameEq(condition.getUsername()),
                        isDeletedEq(condition.getIdDeleted())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory
                .select(users.count())
                .from(users)
                .where(
                        emailEq(condition.getEmail()),
                        usernameEq(condition.getUsername()),
                        isDeletedEq(condition.getIdDeleted())
                )
                .fetchOne()).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }

    @Override
    public Page<UserDto> searchPage2(UserSearchCondition condition, Pageable pageable) {

        List<UserDto> contents = queryFactory
                .select(new QUserDto(
                        users.id,
                        users.username,
                        users.email,
                        users.emailVerified,
                        users.provider,
                        users.authority,
                        users.isDeleted
                ))
                .from(users)
                .where(
                        emailEq(condition.getEmail()),
                        usernameEq(condition.getUsername()),
                        isDeletedEq(condition.getIdDeleted())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(users.count())
                .from(users)
                .where(
                        emailEq(condition.getEmail()),
                        usernameEq(condition.getUsername()),
                        isDeletedEq(condition.getIdDeleted()));

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    //카운트 쿼리 최적화
    //TODO: 이게 맞나??
    @Override
    public Page<UserDto> searchPage3(UserSearchCondition condition, Pageable pageable) {

        List<UserDto> contents = queryFactory
                .select(new QUserDto(
                        users.id,
                        users.username,
                        users.email,
                        users.emailVerified,
                        users.provider,
                        users.authority,
                        users.isDeleted
                ))
                .from(users)
                .where(
                        emailContains(condition.getKeyword()),
                        usernameContains(condition.getKeyword()),
                        emailContains(condition.getEmail()),
                        usernameContains(condition.getUsername()),
                        authorityEq(condition.getAuthority()),
                        accountStatusEq(condition.getIdDeleted())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(users.count())
                .from(users)
                .where(
                        emailContains(condition.getKeyword()),
                        usernameContains(condition.getKeyword()),
                        emailContains(condition.getEmail()),
                        usernameContains(condition.getUsername())
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }


    private BooleanExpression emailContains(String email) {
        return StringUtils.hasText(email) ? users.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression usernameContains(String username) {
        return StringUtils.hasText(username) ? users.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression authorityEq(String authority) {
        return StringUtils.hasText(authority) ? (authority.equals("ROLE_ADMIN") ? users.authority.eq(Authority.ROLE_ADMIN) : users.authority.eq(Authority.ROLE_USER)) : null;
    }

    private BooleanExpression accountStatusEq(Boolean idDeleted) {
        return idDeleted != null ? users.isDeleted.eq(idDeleted) : null;
    }



    private BooleanExpression emailEq(String email) {
        return StringUtils.hasText(email) ? users.email.eq(email) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? users.username.eq(username) : null;
    }

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        return isDeleted != null ? users.isDeleted.eq(isDeleted) : null;
    }
}
