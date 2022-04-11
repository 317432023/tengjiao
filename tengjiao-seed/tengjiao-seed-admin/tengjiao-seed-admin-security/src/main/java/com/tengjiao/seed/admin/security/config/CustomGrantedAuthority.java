package com.tengjiao.seed.admin.security.config;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * CustomGrantedAuthority
 *
 * @author Administrator
 * @date 2022/4/10 22:38
 */
public class CustomGrantedAuthority implements GrantedAuthority, Serializable {
    private final String role;

    public CustomGrantedAuthority(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }
}
