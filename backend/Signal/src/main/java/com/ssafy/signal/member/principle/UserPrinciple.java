package com.ssafy.signal.member.principle;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrinciple extends User {
    private static final String PASSWORD_ERASED_VALUE = "[PASSWORD_ERASED]";
    private final String loginId;


    public UserPrinciple(String loginId, String username, Collection<? extends GrantedAuthority> authorities) {
        super(username, PASSWORD_ERASED_VALUE, authorities);
        this.loginId = loginId;
    }

    @Override
    public String toString() {
        return "UserPrinciple(" +
                "login_id=" + loginId +
                " username=" + getUsername() +
                ')';
    }
}
