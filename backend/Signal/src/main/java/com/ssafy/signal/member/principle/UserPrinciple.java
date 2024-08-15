package com.ssafy.signal.member.principle;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class UserPrinciple extends User {
    private static final String PASSWORD_ERASED_VALUE = "[PASSWORD_ERASED]";
    private final String loginId;


    public UserPrinciple(String loginId, String username, Collection<? extends GrantedAuthority> authorities) {
        super(username != null ? username : "", PASSWORD_ERASED_VALUE, authorities != null ? authorities : new ArrayList<>());
        this.loginId = loginId != null ? loginId : "";
    }

    @Override
    public String toString() {
        return "UserPrinciple(" +
                "login_id=" + loginId +
                " username=" + getUsername() +
                ')';
    }
}
